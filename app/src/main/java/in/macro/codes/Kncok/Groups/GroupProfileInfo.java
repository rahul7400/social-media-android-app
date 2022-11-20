package in.macro.codes.Kncok.Groups;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.macro.codes.Kncok.ChatActivity;
import in.macro.codes.Kncok.FirstMainActivity;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Users;

public class GroupProfileInfo extends Fragment {
    private View mMainView;
    private TextView group_name,g_rules,g_community;
    private Button grp_join;
    String mCurrent_user;
    String gname;
    private DatabaseReference mGroupData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView= inflater.inflate(R.layout.fragment_group_profile_info, container, false);

        assert getArguments() != null;
         gname = getArguments().getString("gname");

        g_rules = (TextView) mMainView.findViewById(R.id.grules);
        g_community = (TextView) mMainView.findViewById(R.id.gcategory);
        grp_join = (Button) mMainView.findViewById(R.id.join_grp);

        mCurrent_user= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        assert gname != null;
        mGroupData = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname);


        mGroupData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String grp_rules = Objects.requireNonNull(dataSnapshot.child("gstatus").getValue()).toString();
                String grp_category = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();

                g_rules.setText(grp_rules);
                g_community.setText(grp_category);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Background task = new Background(GroupProfileInfo.this);
        task.execute();
        return mMainView;
    }


    private static class Background extends AsyncTask<String, String, String> {

        private WeakReference<GroupProfileInfo> activityWeakReference;
        Background(GroupProfileInfo activity){
            activityWeakReference=new WeakReference<GroupProfileInfo>(activity);
        }
        @Override
        protected String doInBackground(String... urls) {
            final GroupProfileInfo activity=activityWeakReference.get();

            activity.mGroupData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String grp_mode = Objects.requireNonNull(dataSnapshot.child("grp_mode").getValue()).toString();
                    if (dataSnapshot.child("Users").hasChild(activity.mCurrent_user)){
                        activity.grp_join.setText("Leave Group");
                        activity.grp_join.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Groups")
                                        .child(activity.gname).child("Users");
                                mDatabase3.child(activity.mCurrent_user).removeValue();
                                Intent intent = new Intent(activity.getContext(), FirstMainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                                Objects.requireNonNull(activity.getActivity()).finish();
                            }
                        });
                    }else{
                        activity.grp_join.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                activity.grp_join.setText("Join Group");
                                DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Groups")
                                        .child(activity.gname).child("Users");
                                final Map<String, Object> messageMap = new HashMap<String, Object>();
                                messageMap.put(activity.mCurrent_user, activity.mCurrent_user);
                                mDatabase3.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Intent gchatIntent = new Intent(activity.getContext(), GroupChatActivty.class);
                                        gchatIntent.putExtra("group_name",activity.gname);
                                        gchatIntent.putExtra("grp_mode",grp_mode);
                                        activity.startActivity(gchatIntent);
                                        Objects.requireNonNull(activity.getActivity()).finish();
                                    }
                                });
                            }
                        });
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
