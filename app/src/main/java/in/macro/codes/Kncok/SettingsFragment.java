package in.macro.codes.Kncok;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {
    private View mMainView;
    private CircleImageView mProfile;
    private TextView mUsername , mStatus;
    private DatabaseReference mUserdatabase;
    private RecyclerView mConvList;
    private SttingsAdapter mAdapter;
    private  String name,status,profile,cover;
    private LinearLayout settings_pro;
    private ArrayList<String>settingsList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();
        mMainView=inflater.inflate(R.layout.fragment_settings, container, false);




        mUsername = (TextView) mMainView.findViewById(R.id.settings_name);
        mStatus = (TextView) mMainView.findViewById(R.id.settings_status);
        mProfile = (CircleImageView) mMainView.findViewById(R.id.settings_profile);
        settings_pro=(LinearLayout) mMainView.findViewById(R.id.settings_pro);
        mUserdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        settingsList.add(0,"Account");
        settingsList.add(1,"App ");
        settingsList.add(2,"Chats");
        settingsList.add(3,"Help");
        settingsList.add(4,"Log out");


        mConvList = (RecyclerView) mMainView.findViewById(R.id.settings_list);
       LinearLayoutManager mLinearLayout = new LinearLayoutManager(getContext());
        mAdapter = new SttingsAdapter(settingsList,getContext());
        mConvList.setLayoutManager(mLinearLayout);
        mConvList.setHasFixedSize(true);
        mConvList.setAdapter(mAdapter);

        settings_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), CurrentProfileActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("status",status);
                intent.putExtra("profile",profile);
                intent.putExtra("cover",cover);
                intent.putExtra("data","cover");

                Pair[] pairs = new Pair[3];

                pairs[0] = new Pair<View,String>(mProfile,"profile");
                pairs[1] = new Pair<View,String>(mUsername,"uname");
                pairs[2] = new Pair<View,String>(mStatus,"ustatus");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(),pairs);
                startActivity(intent,activityOptions.toBundle());
            }
        });

        Background task = new Background(SettingsFragment.this);
        task.execute();
        return mMainView;
    }

    public static class Background extends AsyncTask<String ,String,String> {
        private WeakReference<SettingsFragment> activityWeakReference;
        Background(SettingsFragment activity){
            activityWeakReference=new WeakReference<SettingsFragment>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final SettingsFragment activity=activityWeakReference.get();
            activity.mUserdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   activity.name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                     activity.status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    activity.profile = Objects.requireNonNull(dataSnapshot.child("thumb_image").getValue()).toString();
                    activity.cover = Objects.requireNonNull(dataSnapshot.child("cover_image").getValue()).toString();

                    activity.mUsername.setText(activity.name);
                    activity.mStatus.setText(activity.status);
                    Picasso.with( activity.getContext()).load(activity.profile).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into( activity.mProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with( activity.getContext()).load(activity.profile).placeholder(R.drawable.default_avatar).into( activity.mProfile);

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }


    }
}
