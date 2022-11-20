package in.macro.codes.Kncok;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


import static android.content.Context.MODE_PRIVATE;

public class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.Viewholder> {
    private List<String> mUserList;
    private Context mContext;
    private String typing_status;
    private List<Conv> convs;
    public String userId;
    AlertDialog alertDialog;

    int counter = 0;

    public ChatsFragmentAdapter(List<String> mUserList,Context mContext,List<Conv> convs) {

        this.mUserList = mUserList;
        this.mContext=mContext;
        this.convs=convs;

    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_single_layout ,parent, false);

        return new Viewholder(v);
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public TextView mDisplayname,mLastmsg,timestamp,msg_count;
        public CircleImageView mProfile;
        public ImageView online_icon;
        private RelativeLayout main;


        public Viewholder(@NonNull View itemView) {
            super(itemView);


            mDisplayname=(TextView)itemView.findViewById(R.id.user_single_name);
            mLastmsg=(TextView)itemView.findViewById(R.id.user_single_status);
            mProfile=(CircleImageView) itemView.findViewById(R.id.user_single_image);
            timestamp=(TextView) itemView.findViewById(R.id.msg_time);
            online_icon = (ImageView) itemView.findViewById(R.id.user_single_online_icon);
            msg_count = (TextView) itemView.findViewById(R.id.msg_count);
            main = (RelativeLayout) itemView.findViewById(R.id.main);

        }



    }


    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int i) {

        Conv conv = convs.get(i);
        userId = mUserList.get(i);
        holder.timestamp.setText(GetTimeAgo.getTimeAgo(conv.timestamp,mContext));
        if (counter == 0){
            holder.msg_count.setVisibility(View.GONE);
        }
      /* SharedPreferences.Editor editor = mContext.getSharedPreferences("message_count", MODE_PRIVATE).edit();
        editor.putInt(mUserList.get(i),counter);
        editor.apply();

        SharedPreferences prefs = mContext.getSharedPreferences("message_count", MODE_PRIVATE);
       counter = prefs.getInt(mUserList.get(i), 0); //0 is the default value.*/

        if (!conv.isSeen()){
            holder.mLastmsg.setTypeface(holder.mLastmsg.getTypeface(), Typeface.BOLD);
            holder.mLastmsg.setTextColor(Color.parseColor("#000000"));
            holder.timestamp.setTextColor(Color.parseColor("#ff5521"));
            holder.msg_count.setVisibility(View.VISIBLE);
            holder.msg_count.setText(""+counter);
        }else{
            holder.mLastmsg.setTypeface(holder.mLastmsg.getTypeface(), Typeface.NORMAL);
            holder.mLastmsg.setTextColor(Color.parseColor("#808080"));
            holder.timestamp.setTextColor(Color.parseColor("#808080"));
            counter = 1;
            holder.msg_count.setVisibility(View.GONE);
        }


        ((Activity)mContext).registerForContextMenu(holder.itemView);

        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(String.valueOf(mUserList.get(i)));
            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users c = dataSnapshot.getValue(Users.class);
                    assert c != null;
                    holder.mDisplayname.setText(c.getName());

                    if (dataSnapshot.hasChild("typing")){
                        if (dataSnapshot.child("typing").hasChild("to")){
                            String id = Objects.requireNonNull(dataSnapshot.child("typing").child("to").getValue()).toString();
                            if (id.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                typing_status = Objects.requireNonNull(dataSnapshot.child("typing").child("value").getValue()).toString();
                            }
                        }
                    }
                    else{
                        typing_status = "false";
                    }
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.with(holder.mProfile.getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(holder.mProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(holder.mProfile.getContext()).load(image).placeholder(R.drawable.default_avatar).into(holder.mProfile);

                        }
                    });

                    if(dataSnapshot.hasChild("online")) {

                        String userOnline = dataSnapshot.child("online").getValue().toString();
                        if(userOnline.equals("true")){

                            holder.online_icon.setVisibility(View.VISIBLE);

                        } else {

                            holder.online_icon.setVisibility(View.GONE);

                        }


                    }

              holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent chatIntent = new Intent(mContext, ChatActivity.class);
                            chatIntent.putExtra("user_id", c.getUid());
                            chatIntent.putExtra("user_name", c.getName());
                            chatIntent.putExtra("profile",image);

                            mContext.startActivity(chatIntent);

                        }
                    });

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String  mCurrent_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                    DatabaseReference mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
                    Query lastMessageQuery = mMessageDatabase.child(c.getUid()).limitToLast(1);
                    lastMessageQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                           String  data = dataSnapshot.child("message").getValue().toString();
                             String data_type=dataSnapshot.child("type").getValue().toString();
                            if (!conv.isSeen()){
                                ++counter;
                            }




                                if(("true").equals(typing_status)){
                                    holder.mLastmsg.setText("typing..");
                                    holder.mLastmsg.setTypeface(holder.mLastmsg.getTypeface(), Typeface.BOLD);
                                    holder.mLastmsg.setTextColor(Color.parseColor("#ff5521"));
                                } else {

                                    if (data_type.equals("text")){
                                        holder.mLastmsg.setText(data);

                                    }else if(data_type.equals("image")){

                                        holder.mLastmsg.setText("Image File");

                                    }else if(data_type.equals("video")){

                                        holder.mLastmsg.setText("Video File");
                                    }else if (data_type.equals("invite")){
                                        holder.mLastmsg.setText("Group Invitation");
                                    }else if (data_type.equals("special")){
                                        holder.mLastmsg.setText("Friend request accepted");

                                    }else if (data_type.equals("audio")){
                                        holder.mLastmsg.setText("Audio File");

                                    }else if (data_type.equals("document")){
                                        holder.mLastmsg.setText("Document File");
                                    }



                                }


                            }


                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }



    public void gotoProfile(){
        Intent intent =  new Intent(mContext,ProfileActivity.class);
        intent.putExtra("user_id",userId);
        ActivityOptions activityOptions= ActivityOptions.makeCustomAnimation(mContext
                ,R.anim.profile_enter,R.anim.nothing);
        mContext.startActivity(intent,activityOptions.toBundle());
    }


    public void clearChats(RecyclerView mConvList){
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("All your media and chats will be permanently deleted and can't be recovered ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DatabaseReference mChats = FirebaseDatabase.getInstance().getReference().child("messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mChats.child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(mConvList, "Chats Cleared", Snackbar.LENGTH_LONG)
                                        .setAction("CLOSE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        })
                                        .setActionTextColor(mContext.getResources().getColor(R.color.white ))
                                        .show();
                            }
                        });
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    public void destroyAlertBox(){
        if (alertDialog !=null){
            alertDialog.dismiss();
        }
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
