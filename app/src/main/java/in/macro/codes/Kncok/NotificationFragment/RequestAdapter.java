package in.macro.codes.Kncok.NotificationFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.ProfileActivity;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Users;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.Viewholder> {

    private Context mContext;
    private List<RequestModel> list =new ArrayList<>();
    private String mCurrent_user;

    RequestAdapter(Context mContext, List<RequestModel> list,String mCurrent_user) {
        this.mContext = mContext;
        this.list = list;
        this.mCurrent_user=mCurrent_user;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.req_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        RequestModel requestModel = list.get(position);
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("Users");
        reference3.child(mCurrent_user).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(requestModel.getFollower())){
                    holder.confirm.setText("Followed");
                    holder.confirm.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(requestModel.getFollower());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Users users = dataSnapshot.getValue(Users.class);
                holder.mUsername.setText(users.getName()+"\t started following you");
                String user_id =users.getUid();

                DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                Picasso.with(holder.itemView.getContext()).load(users.getThumb_image()).placeholder(R.drawable.default_avatar).into(holder.mProfile);



                holder.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                        Map friendsMap = new HashMap();
                        friendsMap.put("Friends/" + mCurrent_user + "/" + user_id + "/date", currentDate);
                        friendsMap.put("Friends/" + user_id + "/"  + mCurrent_user + "/date", currentDate);


                        friendsMap.put("Friend_req/" + mCurrent_user + "/" + user_id, null);
                        friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user, null);

                        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                notifyDataSetChanged();
                                MDToast mdToast = MDToast.makeText(mContext, "Friend request accepted",MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                                mdToast.show();

                            }
                        });


                        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference().child("Users");
                        final Map<String, Object> messageMap22 = new HashMap<String, Object>();
                        messageMap22.put(mCurrent_user,mCurrent_user);
                        reference2.child(user_id).child("followers").updateChildren(messageMap22);

                        final Map<String, Object> messageMap2 = new HashMap<String, Object>();
                        messageMap2.put(user_id,user_id);

                        reference2.child(mCurrent_user).child("following").updateChildren(messageMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                holder.confirm.setVisibility(View.INVISIBLE);
                            }
                        });


                        String current_user_ref = "messages/" + mCurrent_user + "/" + user_id;
                        String chat_user_ref = "messages/" + user_id + "/" + mCurrent_user;

                        DatabaseReference user_message_push = mRootRef.child("messages")
                                .child(mCurrent_user).child(user_id).push();

                        String push_id = user_message_push.getKey();

                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("message",   " Accepted " + users.getName() + "'s"+" friend request");
                        messageMap.put("seen", false);
                        messageMap.put("type", "special");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("to", user_id);
                        messageMap.put("from", mCurrent_user);

                        Map<String, Object> messageUserMap = new HashMap<String, Object>();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);



                        mRootRef.child("Chat").child(mCurrent_user).child(user_id).child("seen").setValue(true);
                        mRootRef.child("Chat").child(mCurrent_user).child(user_id).child("timestamp").setValue(ServerValue.TIMESTAMP);

                        mRootRef.child("Chat").child(user_id).child(mCurrent_user).child("seen").setValue(false);
                        mRootRef.child("Chat").child(user_id).child(mCurrent_user).child("timestamp").setValue(ServerValue.TIMESTAMP);

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





                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRootRef.child("Friend_req").child(mCurrent_user).child(user_id).removeValue();
                        MDToast mdToast = MDToast.makeText(mContext, "Friend request deleted",MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
                        notifyDataSetChanged();
                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        mContext.startActivity(profileIntent);
                        ((Activity) mContext).overridePendingTransition(R.anim.profile_enter, R.anim.nothing);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public TextView mUsername;
        public CircleImageView mProfile;
        public Button confirm,cancel;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            mUsername = (TextView) itemView.findViewById(R.id.req_name);
            mProfile =(CircleImageView) itemView.findViewById(R.id.req_profile);
            confirm = (Button) itemView.findViewById(R.id.req_confirm);
            cancel = (Button) itemView.findViewById(R.id.req_cancel);

        }
    }
}
