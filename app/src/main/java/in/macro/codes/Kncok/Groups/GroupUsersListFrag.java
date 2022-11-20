package in.macro.codes.Kncok.Groups;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import in.macro.codes.Kncok.ChatActivity;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Users;


public class GroupUsersListFrag extends Fragment {
    private View mMainView;
    private RecyclerView mConvList;
    private LinearLayout locked;
    private DatabaseReference mgroup;
    private GUListAdapter mAdapter;

    private ArrayList<Users> usersArrayList =new ArrayList<>();
    private ArrayList<String> userList =new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_group_users_list, container, false);
        assert getArguments() != null;
        String gname = getArguments().getString("gname");
       locked = (LinearLayout) mMainView.findViewById(R.id.grp_locked);
       mConvList =(RecyclerView) mMainView.findViewById(R.id.grp_ulist);


        LinearLayoutManager linearLayoutManager2= new LinearLayoutManager(getContext());
        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager2);
       mAdapter = new GUListAdapter(getContext(),userList,gname);
       mConvList.setAdapter(mAdapter);
        assert gname != null;
        mgroup = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname).child("Users");

       Background task = new Background(GroupUsersListFrag.this);
       task.execute();

        return mMainView;
    }



    private static class Background extends AsyncTask<String, String, String> {

        private WeakReference<GroupUsersListFrag> activityWeakReference;
        Background(GroupUsersListFrag activity){
            activityWeakReference=new WeakReference<GroupUsersListFrag>(activity);
        }
        @Override
        protected String doInBackground(String... urls) {


            final GroupUsersListFrag activity=activityWeakReference.get();
            activity.mgroup.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                        activity.mConvList.setVisibility(View.VISIBLE);
                        activity.locked.setVisibility(View.GONE);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            activity.userList.add(snapshot.getKey());
                            activity.mAdapter.notifyDataSetChanged();
                        }
                    }else{
                        activity.mConvList.setVisibility(View.GONE);
                        activity.locked.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }
}
}
