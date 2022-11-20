package in.macro.codes.Kncok.Groups;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.DoubleClickListener;
import in.macro.codes.Kncok.Messages;
import in.macro.codes.Kncok.ProfileActivity;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.imageview_Activity;
import in.macro.codes.Kncok.play_video;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatholder> {

    private List<Messages> mgMessageList;
    private DatabaseReference mUserDatabase;
    private String gname,gmod;

    private FirebaseAuth mAuth;
    private AppCompatActivity activity;
    public GroupChatAdapter(AppCompatActivity activity, List<Messages> mgMessageList, String gname, String gmod) {
        this.mgMessageList = mgMessageList;
        this.activity=activity;
        this.gname=gname;
        this.gmod=gmod;


    }

    @NonNull
    @Override
    public GroupChatholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_msg_layout ,parent, false);
        mAuth=FirebaseAuth.getInstance();
        return new GroupChatholder(v);
    }





    public class GroupChatholder extends RecyclerView.ViewHolder {

        public TextView messageText,messageText2,not_download,getMessageText3;
        public CircleImageView profileImage;
        public TextView displayName,gmessage_recieving3;
        public LinearLayout messageImage2,messageImage,messageTextLayout_rec,messageTextLayout_rec2;
        public ImageView image,image2,image_video,image2_video;
        public LinearLayout onlyAdminLayout,onlyAdminImg,big_heart_on,gdisp_layout;
        public ImageView onlyAdminimgView;
        public ImageView heart_on,heart_off;
        public View view_line;



        public GroupChatholder(@NonNull View view) {
            super(view);
            messageTextLayout_rec =(LinearLayout)view.findViewById(R.id.gmessage_text_layout);


            messageText = (TextView) view.findViewById(R.id.gmessage_recieving);
            profileImage = (CircleImageView) view.findViewById(R.id.gmessage_profile_layout);
            messageText2 = (TextView) view.findViewById(R.id.gmessage_text_layout2);
            messageImage = (LinearLayout) view.findViewById(R.id.gmessage_image_layout);
            messageImage2 = (LinearLayout) view.findViewById(R.id.gmessage_image_layout2);
            image_video=(ImageView)view.findViewById(R.id.gimage_video);
            image2_video=(ImageView)view.findViewById(R.id.gimage2_video);
            image2=(ImageView)view.findViewById(R.id.gimage2);
            image=(ImageView)view.findViewById(R.id.gimage);
            not_download=(TextView)view.findViewById(R.id.gnot_downloaded);
            displayName = (TextView) view.findViewById(R.id.guser_name);
            onlyAdminLayout=(LinearLayout)view.findViewById(R.id.gmessage_text_layout3);
            gmessage_recieving3=(TextView)view.findViewById(R.id.gmessage_recieving3);
            onlyAdminImg=(LinearLayout)view.findViewById(R.id.gmessage_image_layout3);
            onlyAdminimgView = (ImageView)view.findViewById(R.id.gimage3);
            heart_on=(ImageView) view.findViewById(R.id.heart_on);
            heart_off=(ImageView) view.findViewById(R.id.heart_off);
            big_heart_on=(LinearLayout)view.findViewById(R.id.big_heart_on);
            gdisp_layout=(LinearLayout) view.findViewById(R.id.gdisp_layout);
            view_line = (View) view.findViewById(R.id.view_line);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupChatholder viewHolder, int i) {
        final Messages c = mgMessageList.get(i);
        if (i>0){
            final Messages prev_c = mgMessageList.get(i-1);
            if (prev_c!=null){
                if (prev_c.getFrom().equals(c.getFrom())){

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewHolder.messageTextLayout_rec.getLayoutParams();
                    params.topMargin = -10;

                    viewHolder.gdisp_layout.setVisibility(View.GONE);
                    viewHolder.view_line.setVisibility(View.GONE);

                }else{
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewHolder.messageTextLayout_rec.getLayoutParams();
                    params.topMargin = 30;


                    viewHolder.gdisp_layout.setVisibility(View.VISIBLE);
                    viewHolder.view_line.setVisibility(View.VISIBLE);
                }
            }
        }

        final String sender_user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final String from_user = c.getFrom();
        String message_type = c.getType();






                if (from_user.equals(sender_user)){
                    viewHolder.onlyAdminImg.setVisibility(View.GONE);
                    viewHolder.messageTextLayout_rec.setVisibility(View.GONE);
                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.profileImage.setVisibility(View.GONE);
                    viewHolder.messageImage.setVisibility(View.GONE);
                    viewHolder.messageImage2.setVisibility(View.GONE);

                    if (gmod != null &&gmod.equals("admin")){
                        viewHolder.messageTextLayout_rec.setVisibility(View.GONE);
                        viewHolder.onlyAdminLayout.setVisibility(View.VISIBLE);

                    }else {
                        viewHolder.messageText2.setAnimation(AnimationUtils.loadAnimation(activity,R.anim.rightfade));
                        viewHolder.messageText2.setVisibility(View.VISIBLE);
                    }

                }else{
                    viewHolder.onlyAdminImg.setVisibility(View.GONE);
                    viewHolder.messageText2.setVisibility(View.GONE);
                    viewHolder.messageImage.setVisibility(View.GONE);
                    viewHolder.messageImage2.setVisibility(View.GONE);



                    if (gmod != null &&gmod.equals("admin")){
                        viewHolder.messageTextLayout_rec.setVisibility(View.GONE);
                        viewHolder.onlyAdminLayout.setVisibility(View.VISIBLE);

                    }else {
                        viewHolder.profileImage.setVisibility(View.VISIBLE);
                        viewHolder.messageText.setVisibility(View.VISIBLE);
                        viewHolder.messageTextLayout_rec.setAnimation(AnimationUtils.loadAnimation(activity,R.anim.leftfade));
                        viewHolder.messageTextLayout_rec.setVisibility(View.VISIBLE);
                    }

                }




                viewHolder.gdisp_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(activity, ProfileActivity.class);
                        profileIntent.putExtra("user_id", c.getFrom());
                        activity.startActivity(profileIntent);
                        activity.overridePendingTransition(R.anim.rg,R.anim.nothing);

                    }
                });


        //TODO Set visibility to particular item
        /*DatabaseReference mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname);
        Query lastMessageQuery = mMessageDatabase.child("gmessages").limitToLast(2);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String from =dataSnapshot.child("from").getValue().toString();
                if (from.equals(c.getFrom())){

                    viewHolder.displayName.setVisibility(View.GONE);
                    viewHolder.profileImage.setVisibility(View.GONE);
                }
                else{
                    viewHolder.displayName.setVisibility(View.VISIBLE);
                    viewHolder.profileImage.setVisibility(View.VISIBLE);
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
        });*/


       mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("thumb_image").getValue().toString();

               // String nickname = name.substring(0, name.indexOf(' '));
                viewHolder.displayName.setText(name);

                Random rand = new Random();
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                int randomColor = Color.rgb(r,g,b);

                viewHolder.displayName.setTextColor(randomColor);



                Picasso.with(activity).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(activity).load(image).placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(message_type.equals("text")) {

            if (from_user.equals(sender_user)){
                if (gmod != null &&gmod.equals("admin")){

                    viewHolder.gmessage_recieving3.setText(c.getMessage());

                }else {
                    viewHolder.messageText2.setText(c.getMessage());
                }

            }else{


                if (gmod != null &&gmod.equals("admin")){

                    viewHolder.gmessage_recieving3.setText(c.getMessage());

                }else {
                    viewHolder.messageText.setText(c.getMessage());
                }
            }




        } else if (message_type.equals("image")){


            if (from_user.equals(sender_user)){
                if (gmod != null &&gmod.equals("admin")){
                    viewHolder.onlyAdminImg.setVisibility(View.VISIBLE);
                    viewHolder.messageImage2.setVisibility(View.GONE);
                    viewHolder.onlyAdminLayout.setVisibility(View.GONE);
                    viewHolder.messageImage.setVisibility(View.GONE);
                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.messageText2.setVisibility(View.GONE);
                    viewHolder.image2_video.setVisibility(View.GONE);
                    viewHolder.image_video.setVisibility(View.GONE);
                    viewHolder.not_download.setVisibility(View.GONE);
                    File imgFile = new  File(c.getFilepath());

                    viewHolder.heart_off.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.heart_off.setVisibility(View.GONE);
                            viewHolder.heart_on.setVisibility(View.VISIBLE);
                        }
                    });

                    viewHolder.heart_on.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.heart_on.setVisibility(View.GONE);
                            viewHolder.heart_off.setVisibility(View.VISIBLE);
                        }
                    });



                    if (imgFile.exists()){


                        Glide.with(viewHolder.profileImage.getContext())
                                .asBitmap()
                                .error(R.drawable.default_send_image)
                                .load(c.getFilepath())
                                .into(viewHolder.onlyAdminimgView);
                    }else {
                        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Downloaded Images");
                        final File checkFile = new File(directory,c.getName());

                        if (checkFile.exists()){

                            Glide.with(viewHolder.profileImage.getContext())
                                    .asBitmap()
                                    .error(R.drawable.default_send_image)
                                    .load(checkFile)
                                    .into(viewHolder.onlyAdminimgView);


                        }else{
                            Glide.with(viewHolder.profileImage.getContext())
                                    .asBitmap()
                                    .load(c.getMessage())
                                    .centerCrop()
                                    .override(600,300)
                                    .into(viewHolder.onlyAdminimgView);

                        }
                        viewHolder.messageImage2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent view_image = new Intent(view.getContext(), imageview_Activity.class);
                                view_image.putExtra("view_img",c.getMessage());
                                view_image.putExtra("img_ID",c.getName());
                                view_image.putExtra("filepath",c.getFilepath());
                                activity.startActivity(view_image);
                                activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);


                            }
                        });


                    }



                    viewHolder.onlyAdminImg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Intent view_image = new Intent(view.getContext(), imageview_Activity.class);
                            view_image.putExtra("view_img",c.getMessage());
                            view_image.putExtra("img_ID",c.getName());
                            view_image.putExtra("filepath",c.getFilepath());
                            activity.startActivity(view_image);
                            activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);
                            return false;
                        }
                    });

                    viewHolder.onlyAdminImg.setOnClickListener(new DoubleClickListener() {

                        @Override
                        public void onSingleClick(View v) {

                        }

                        @Override
                        public void onDoubleClick(View v) {
                            viewHolder.heart_off.setVisibility(View.GONE);
                            viewHolder.big_heart_on.setVisibility(View.VISIBLE);
                            Animation animation   =    AnimationUtils.loadAnimation(activity, R.anim.zoom_in);
                            animation.setDuration(100);
                            viewHolder.big_heart_on.setAnimation(animation);
                            viewHolder.big_heart_on.animate();
                            animation.start();
                            viewHolder.heart_on.setVisibility(View.VISIBLE);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.big_heart_on.setVisibility(View.GONE);
                                    Animation animation   =    AnimationUtils.loadAnimation(activity, R.anim.zoom_out);
                                    animation.setDuration(200);
                                    viewHolder.big_heart_on.setAnimation(animation);
                                    viewHolder.big_heart_on.animate();
                                    animation.start();
                                }
                            }, 800);
                        }
                    });







                }else {
                    viewHolder.messageImage2.setVisibility(View.VISIBLE);
                    viewHolder.messageImage.setVisibility(View.GONE);
                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.messageText2.setVisibility(View.GONE);
                    viewHolder.image2_video.setVisibility(View.GONE);
                    viewHolder.image_video.setVisibility(View.GONE);
                    viewHolder.onlyAdminLayout.setVisibility(View.GONE);
                    viewHolder.not_download.setVisibility(View.GONE);
                    File imgFile = new  File(c.getFilepath());
                    if (imgFile.exists()){


                        Glide.with(viewHolder.profileImage.getContext())
                                .asBitmap()
                                .error(R.drawable.default_send_image)
                                .load(c.getFilepath())
                                .into(viewHolder.image2);
                    }else {
                        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Downloaded Images");
                        final File checkFile = new File(directory,c.getName());

                        if (checkFile.exists()){

                            Glide.with(viewHolder.profileImage.getContext())
                                    .asBitmap()
                                    .error(R.drawable.default_send_image)
                                    .load(checkFile)
                                    .into(viewHolder.image2);


                        }else{
                            Glide.with(viewHolder.profileImage.getContext())
                                    .asBitmap()
                                    .load(c.getMessage())
                                    .centerCrop()
                                    .override(600,300)
                                    .into(viewHolder.image2);

                        }

                    }











                    viewHolder.messageImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent view_image = new Intent(view.getContext(), imageview_Activity.class);
                            view_image.putExtra("view_img",c.getMessage());
                            view_image.putExtra("img_ID",c.getName());
                            view_image.putExtra("filepath",c.getFilepath());
                            activity.startActivity(view_image);
                            activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);


                        }
                    });

                }


            }else{


                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Downloaded Images");
                final File checkFile = new File(directory,c.getName());

                viewHolder.messageImage.setVisibility(View.VISIBLE);
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.image_video.setVisibility(View.GONE);

                if (checkFile.exists()){



                    Glide.with(viewHolder.profileImage.getContext())
                            .asBitmap()
                            .error(R.drawable.default_send_image)
                            .load(checkFile)
                            .into(viewHolder.image);
                    viewHolder.not_download.setVisibility(View.GONE);

                }else{


                    Glide.with(viewHolder.profileImage.getContext())
                            .asBitmap()
                            .error(R.drawable.default_send_image)
                            .load(c.getMessage())
                            .centerCrop()
                            .override(600,300)
                            .into(viewHolder.image);
                    viewHolder.not_download.setVisibility(View.VISIBLE);


                    viewHolder.not_download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                            builder1.setTitle("Info");
                            builder1.setMessage("The file which you are viewing is loaded online and is not downloaded on your device ");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Great",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Dismiss",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    });

                }




                viewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (c.getMessage().equals("default")){
                            Toast.makeText(activity, "File is being uploaded by the user.PLease Wait for some time", Toast.LENGTH_LONG).show();
                        }else {
                            Intent view_image = new Intent(view.getContext(), imageview_Activity.class);
                            view_image.putExtra("view_img", c.getMessage());
                            view_image.putExtra("img_ID", c.getName());
                            view_image.putExtra("filepath", "null");
                            activity.startActivity(view_image);
                            activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);
                        }
                    }
                });





                 /*   if (checkFile.exists()){
                        Picasso.with(viewHolder.profileImage.getContext())
                                .load(checkFile).placeholder(R.drawable.default_send_image).into(viewHolder.image);
                    }else {
                        Picasso.with(viewHolder.profileImage.getContext())
                                .load(c.getMessage()).placeholder(R.drawable.default_send_image).into(viewHolder.image);
                    }*/

            }




        }else if (message_type.equals("video")){
            if (from_user.equals(sender_user)){
                viewHolder.messageImage2.setVisibility(View.VISIBLE);
                viewHolder.image2_video.setVisibility(View.VISIBLE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);


                RequestOptions myOptions = new RequestOptions()

                        .override(600, 300);
                Glide.with(viewHolder.profileImage.getContext())
                        .asBitmap()
                        .apply(myOptions)
                        .load(Uri.fromFile(new File(c.getFilepath())))
                        .into(viewHolder.image2);

                viewHolder.messageImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), play_video.class);
                        intent.putExtra("path",c.getFilepath());
                        intent.putExtra("uid",c.getFrom());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);
                    }
                });


            }else {
                viewHolder.messageImage.setVisibility(View.VISIBLE);
                viewHolder.image_video.setVisibility(View.VISIBLE);
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);






                RequestOptions myOptions = new RequestOptions()

                        .override(600, 200);
                Glide.with(viewHolder.profileImage.getContext())
                        .asBitmap()
                        .apply(myOptions)
                        .load(c.getMessage())
                        .into(viewHolder.image);

                viewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),play_video.class);
                        intent.putExtra("video_url",c.getMessage());
                        intent.putExtra("uid",c.getFrom());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);
                    }
                });
            }
        }

    }


    @Override
    public int getItemCount() {
        return mgMessageList.size();
    }
}
