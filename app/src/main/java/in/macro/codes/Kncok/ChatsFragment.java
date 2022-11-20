package in.macro.codes.Kncok;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private RecyclerView mConvList,mSuggestionList;
    private String userID;
    private List<String> list = new ArrayList<>();
    private List<String> suggList = new ArrayList<>();
    String city;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    private ChatsFragmentAdapter mAdapter;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    AlertDialog alertDialog;
    private TextView suggested_txt;
    private View mMainView;
    private SuggestionAdapter mSuggestionAdapter;
    private ArrayList<Users> suggestList;
    private ArrayList<Conv> convs = new ArrayList<>();
    int counter = 0;
    private DatabaseReference mFriendreq,mFriends;
    public ChatsFragment() {
        // Required empty public constructor
    }



    public  ChatsFragment newInstance( ArrayList<Users> usersDetails) {
        ChatsFragment fragment = new ChatsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mSuggestionList = (RecyclerView) mMainView.findViewById(R.id.suggestion_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        suggestList = new ArrayList<>();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);



        suggested_txt = (TextView) mMainView.findViewById(R.id.txt2);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mMessageDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        mFriendreq = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);

        LinearLayoutManager linearLayoutManager2= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);


        mSuggestionList.setHasFixedSize(true);
        mSuggestionList.setLayoutManager(linearLayoutManager2);
        ((SimpleItemAnimator) Objects.requireNonNull(mSuggestionList.getItemAnimator())).setSupportsChangeAnimations(false);
        mSuggestionAdapter = new SuggestionAdapter(suggList,getContext());
        mSuggestionAdapter.setHasStableIds(true);
        mSuggestionList.setAdapter(mSuggestionAdapter);

        mAdapter = new ChatsFragmentAdapter(list,getContext(),convs);
        mConvList.setAdapter(mAdapter);

        Background Task=new Background(ChatsFragment.this);
        Task.doInBackground("getUser");




        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Background Task2=new Background(ChatsFragment.this);
                Task2.doInBackground("getSuggestions");
            }
        },3);


      registerForContextMenu(mConvList);

        // Inflate the layout for this fragment
        return mMainView;
    }







    public static class Background extends AsyncTask<String,String,String>{
        private WeakReference<ChatsFragment> activityWeakReference;
        Background(ChatsFragment activity){
            activityWeakReference=new WeakReference<ChatsFragment>(activity);
        }
        @Override
        protected String doInBackground(String... strings) {
            final ChatsFragment activity=activityWeakReference.get();

            switch (strings[0]){

                case "getUser" :
               activity.list.clear();
               //TODO Old position glitch will be solved from here
                    Query conversationQuery = activity.mConvDatabase.orderByChild("timestamp");
                    conversationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot :  dataSnapshot.getChildren()){
                                Conv conv = snapshot.getValue(Conv.class);
                                if (!activity.list.contains(snapshot.getKey())){
                                    activity.convs.add(conv);
                                    activity.list.add(snapshot.getKey());
                                }else {

                                    activity.convs.clear();
                                    activity.convs.add(conv);
                                    activity.list.clear();
                                    activity.list.add(snapshot.getKey());
                                }

                            }
                           activity.mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    break;


                case "getSuggestions" :
                    DatabaseReference mFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(activity.mCurrent_user_id);
                    DatabaseReference mFriendreq = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(activity.mCurrent_user_id);


                    activity.mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            activity.suggList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Users users  = snapshot.getValue(Users.class);
                                assert users != null;
                                activity.mUsersDatabase.child(activity.mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String city = Objects.requireNonNull(dataSnapshot.child("city").getValue()).toString();
                                        if (!city.equals("")){
                                            if (city.equalsIgnoreCase(users.getCity())){
                                                if (!users.getUid().equals(activity.mCurrent_user_id)){
                                                    mFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (!dataSnapshot.hasChild(users.getUid())){
                                                                mFriendreq.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (!dataSnapshot.hasChild(users.getUid())){
                                                                            activity.suggList.add(users.getUid());
                                                                            activity.mSuggestionAdapter.notifyDataSetChanged();
                                                                        }
                                                                        if ( activity.suggList.size()== 0){
                                                                            activity.suggested_txt.setVisibility(View.GONE);

                                                                        }else {
                                                                            activity.suggested_txt.setVisibility(View.VISIBLE);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    break;


            }

            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
        menu.setHeaderIcon(R.drawable.icon);
        inflater.inflate(R.menu.override_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.clear_chat:
               mAdapter.clearChats(mConvList);
                return true;

            case R.id.view_profile:
                mAdapter.gotoProfile();
                return true;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onDestroy() {
     mAdapter.destroyAlertBox();
        super.onDestroy();
    }



}