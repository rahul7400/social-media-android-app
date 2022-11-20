package in.macro.codes.Kncok.Groups;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.GetTimeAgo;
import in.macro.codes.Kncok.Model;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Users;

public class GroupInviteAdapter extends RecyclerView.Adapter<GroupInviteAdapter.GroupInvite> {
    public Context context;
    public List<Users> username;

    private String gname;

    public GroupInviteAdapter(Context context, List<Users> username,String gname) {
        this.context = context;
        this.username = username;

        this.gname=gname;


    }
    @NonNull
    @Override

    public GroupInvite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.users_single_layout, parent, false);
        return new GroupInviteAdapter.GroupInvite(v);
    }


    @Override
    public void onBindViewHolder(@NonNull GroupInvite holder, int position) {

       DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        Users users =username.get(position);
        holder.gname.setText(users.getName());


        mRootRef.child("Users").child(users.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                final String image = dataSnapshot.child("thumb_image").getValue().toString();

                if(online.equals("true")) {

                    holder.gstatus.setText("Active Now");

                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, context);

                    holder.gstatus.setText(lastSeenTime);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Picasso.with(context).load(users.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(holder.gthumb_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(users.getThumb_image()).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);

            }
        });



        DatabaseReference mGrp = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname).child("Users");
        mGrp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(users.getUid())){
                    holder.grp_invite.setVisibility(View.GONE);
                }else{
                    holder.grp_invite.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String mCurrentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        holder.grp_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Users suser =username.get(position);
                holder.grp_invite.setText("Invite Sent");
                holder.grp_invite.setEnabled(false);

                String mChatUser = suser.getUid();

                String current_user_ref = "messages/" + mCurrentUserId + "/" + suser.getUid();
                String chat_user_ref = "messages/" + suser.getUid() + "/" + mCurrentUserId;

                DatabaseReference user_message_push = mRootRef.child("messages")
                        .child(mCurrentUserId).child(mChatUser).push();

                String push_id = user_message_push.getKey();

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", gname);
                messageMap.put("seen", false);
                messageMap.put("type", "invite");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("to",mChatUser);
                messageMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());

                Map<String, Object> messageUserMap = new HashMap<String, Object>();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);



                mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
                mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

                mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
                mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError != null){

                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                        }

                    }
                });

            }

        });
    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class GroupInvite extends RecyclerView.ViewHolder {
        public TextView gname, gstatus;
        public CircleImageView gthumb_image;
        public Button grp_invite;
        public GroupInvite(@NonNull View itemView) {
            super(itemView);
            gname = (TextView) itemView.findViewById(R.id.user_single_name);
            gstatus = (TextView) itemView.findViewById(R.id.user_single_status);
            gthumb_image = (CircleImageView) itemView.findViewById(R.id.user_single_image);
            grp_invite=(Button)itemView.findViewById(R.id.grp_invite);
        }
    }
}
