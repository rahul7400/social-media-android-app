package in.macro.codes.Kncok.Story;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Users;

public class StoryAdapter extends  RecyclerView.Adapter<StoryAdapter.Viewholder> {

    private Context mContext;
    private ArrayList<Story> mStory=new ArrayList<>();
    private List<String> friendList = new ArrayList<>();

    public StoryAdapter(Context mContext, ArrayList<Story> mStory ) {
        this.mContext = mContext;
        this.mStory = mStory;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        if (i==0){
            View v = LayoutInflater.from(mContext).inflate(R.layout.add_story,parent,false);
            return new Viewholder(v);
        }
        else{
            View v = LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false);
            return new Viewholder(v);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {


        Story story = mStory.get(position);

        Log.e("called", String.valueOf(mStory.size()));
        if (!story.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            userInfo(holder,story.getUserId(),position,"other");

            AnimationDrawable animationDrawable = (AnimationDrawable)holder.anim_layout.getBackground();
            animationDrawable.setEnterFadeDuration(4500);
            animationDrawable.setExitFadeDuration(4500);
            animationDrawable.start();
        }else{
            userInfo(holder,story.getUserId(),position,"current");
        }



        if (holder.getAdapterPosition()!=0){
            seenStory(holder,story.getUserId());
        }

        if (holder.getAdapterPosition()==0){
            myStory(holder.addStory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() ==0){
                    myStory(holder.addStory_text,holder.story_plus,true);
                }else {

                        Intent intent = new Intent(mContext, StoryActivity.class);
                        intent.putExtra("userid", story.getUserId());
                        mContext.startActivity(intent);
                        ((Activity)mContext).overridePendingTransition(R.anim.zoom_in,R.anim.no_anim);


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private ImageView story_photo , story_plus,story_photo_seen,addStory;
        private TextView story_username,addStory_text;
        private RelativeLayout anim_layout;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            addStory =(ImageView) itemView.findViewById(R.id.add_story);
            story_photo =(ImageView) itemView.findViewById(R.id.story_photo);
            story_plus =(ImageView) itemView.findViewById(R.id.story_plus);
            story_photo_seen =(ImageView) itemView.findViewById(R.id.story_photo_seen);
            story_username =(TextView) itemView.findViewById(R.id.story_username);
            addStory_text =(TextView) itemView.findViewById(R.id.addStory_text);

            anim_layout =(RelativeLayout) itemView.findViewById(R.id.anim_layout);



        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return  0 ;
        }
        return 1;
    }
    private void  userInfo(final Viewholder viewholder , String userId , final int pos, final String dat){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final Users users = dataSnapshot.getValue(Users.class);

                assert users != null;

                if (!dat.equals("current")){


                    Picasso.with(mContext).load(users.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(viewholder.story_photo, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(users.getThumb_image())
                                    .placeholder(R.drawable.default_avatar).into(viewholder.story_photo);

                        }
                    });
                }else{
                  Picasso.with(mContext).load(users.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(viewholder.addStory, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(mContext).load(users.getThumb_image())
                                    .placeholder(R.drawable.default_avatar).into(viewholder.addStory);

                        }
                    });
                }

                if (pos!=0){
                    Picasso.with(mContext).load(users.getThumb_image()).into(viewholder.story_photo_seen);
                    viewholder.story_username.setText(users.getName());
                    viewholder.story_username.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void myStory(final TextView textView , final ImageView imageView, final boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("story").child(FirebaseAuth
                .getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int count =0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    if (timecurrent>story.getTimestart() && timecurrent<story.getTimend()){
                        count++;
                    }
                }
                if (click) {
                    if (count>0){

                        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                alertDialog.dismiss();
                                Intent intent = new Intent(mContext, StoryActivity.class);
                                intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                mContext.startActivity(intent);
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                alertDialog.dismiss();
                                Intent intent = new Intent(mContext, AddStoryActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        });
                        alertDialog.show();

                        //TODO Alert Dialog Leaking Memory and giving error

                       /* AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        final AlertDialog dialog = builder.create();
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which){

                                    case 1 :
                                        dialog.dismiss();
                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                        break;

                                    case 0 :
                                        dialog.dismiss();
                                         Intent intent2 = new Intent(mContext, StoryActivity.class);
                                           intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mContext.startActivity(intent);
                                        break;
                                }


                            }
                        });
                        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations;
                        dialog.show();*/




                    }else{
                        Intent intent = new Intent(mContext,AddStoryActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }else if(count > 0){
                    textView.setText("My Story");
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    imageView.setVisibility(View.GONE);
                }else{
                    textView.setText("ADD Story");
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    imageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final Viewholder viewholder , String userId){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("story").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                    && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimend()){
                        i++;
                    }
                }

                if (i>0){
                    viewholder.story_photo.setVisibility(View.VISIBLE);
                    viewholder.story_photo_seen.setVisibility(View.GONE);
                }else{
                    viewholder.story_photo.setVisibility(View.GONE);
                    viewholder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
