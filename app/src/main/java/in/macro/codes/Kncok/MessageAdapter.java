package in.macro.codes.Kncok;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Model> mModelList;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    String name1,name2;
    private boolean isPlaying = false;
    int totalTime;
    private AppCompatActivity activity;
    MediaPlayer player;

    public MessageAdapter(AppCompatActivity activity, List<Messages> mMessageList) {

        this.mMessageList = mMessageList;
        this.activity = activity;


    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);

    }



    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public EmojiTextView  messageText, messageText2;
        public TextView not_download, caption1, caption2;
        public TextView img_time, img_time2, txt_msg_time, grp_text, special_msg,extension,extension2;
        public LinearLayout message_text_layout;
        public ImageView image, image2, image_video, image2_video;
        public FrameLayout grp_request;
        public RelativeLayout messageImage2, messageImage, audio_layout, audio_layout2;
        public LinearLayout doc_layout , doc_layout2;
        public Button grp_join, grp_decline;
        public ImageView audio_play, audio_pause, audio_play2, audio_pause2;
        public ProgressBar audio_prg, audio_prg2,doc_prg;
        public SeekBar seekBar, seekBar2;
        public Button doc_open , doc_open2;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (EmojiTextView) view.findViewById(R.id.message_text_layout);

            messageText2 = (EmojiTextView) view.findViewById(R.id.message_text_layout2);
            messageImage = (RelativeLayout) view.findViewById(R.id.message_image_layout);
            message_text_layout = (LinearLayout) view.findViewById(R.id.test);
            messageImage2 = (RelativeLayout) view.findViewById(R.id.message_image_layout2);
            image_video = (ImageView) view.findViewById(R.id.image_video);
            image2_video = (ImageView) view.findViewById(R.id.image2_video);
            image2 = (ImageView) view.findViewById(R.id.image2);
            image = (ImageView) view.findViewById(R.id.image);
            not_download = (TextView) view.findViewById(R.id.not_downloaded);
            img_time = (TextView) view.findViewById(R.id.img_time);
            img_time2 = (TextView) view.findViewById(R.id.img_time2);
            // txt_msg_time=(TextView)view.findViewById(R.id.txt_msg_time);

            grp_request = (FrameLayout) view.findViewById(R.id.grp_request);
            grp_join = (Button) view.findViewById(R.id.grp_req_confirm);
            grp_decline = (Button) view.findViewById(R.id.grp_req_dismiss);
            special_msg = (TextView) view.findViewById(R.id.special_msg);
            grp_text = (TextView) view.findViewById(R.id.grp_req_txt);
            caption1 = (TextView) view.findViewById(R.id.caption1);
            caption2 = (TextView) view.findViewById(R.id.caption2);
            audio_layout = (RelativeLayout) view.findViewById(R.id.message_audio_layout);
            audio_layout2 = (RelativeLayout) view.findViewById(R.id.message_audio_layout2);
            audio_play = (ImageView) view.findViewById(R.id.audio_play);
            audio_play2 = (ImageView) view.findViewById(R.id.audio_play2);
            audio_pause = (ImageView) view.findViewById(R.id.audio_pause);
            audio_pause2 = (ImageView) view.findViewById(R.id.audio_pause2);
            audio_prg = (ProgressBar) view.findViewById(R.id.audio_prg);
            audio_prg2 = (ProgressBar) view.findViewById(R.id.audio_prg2);
            seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            seekBar2 = (SeekBar) view.findViewById(R.id.seekBar2);
            doc_layout = (LinearLayout) view.findViewById(R.id.message_doc_layout);
            doc_layout2 = (LinearLayout) view.findViewById(R.id.message_doc_layout2);
            extension = (TextView) view.findViewById(R.id.extension);
            extension2 = (TextView) view.findViewById(R.id.extension2);
            doc_prg=(ProgressBar) view.findViewById(R.id.doc_prg);
            doc_open = (Button) view.findViewById(R.id.doc_open);
            doc_open2 = (Button) view.findViewById(R.id.doc_open2);



        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        final Messages c = mMessageList.get(i);
        String sender_user = mAuth.getCurrentUser().getUid();
        String from_user = c.getFrom();
        String message_type = c.getType();


        String to_user = c.getTo();


        if (from_user.equals(sender_user)) {

            viewHolder.messageText2.setVisibility(View.VISIBLE);
            viewHolder.messageText.setVisibility(View.GONE);

            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageImage2.setVisibility(View.GONE);

            viewHolder.grp_request.setVisibility(View.GONE);
            viewHolder.special_msg.setVisibility(View.GONE);
            viewHolder.audio_layout.setVisibility(View.GONE);
            viewHolder.audio_layout2.setVisibility(View.GONE);
            viewHolder.doc_layout2.setVisibility(View.GONE);
            viewHolder.doc_layout.setVisibility(View.GONE);

        } else {

            viewHolder.messageText2.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageImage2.setVisibility(View.GONE);
            viewHolder.grp_request.setVisibility(View.GONE);
            viewHolder.special_msg.setVisibility(View.GONE);
            viewHolder.audio_layout.setVisibility(View.GONE);
            viewHolder.audio_layout2.setVisibility(View.GONE);
            viewHolder.doc_layout2.setVisibility(View.GONE);
            viewHolder.doc_layout.setVisibility(View.GONE);


            //  viewHolder.txt_msg_time.setVisibility(View.VISIBLE);
        }
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (message_type.equals("text")) {
            viewHolder.special_msg.setVisibility(View.GONE);
            if (from_user.equals(sender_user)) {


                //hasAlphabet method is there which tells us where there is alphabet present or not.

                viewHolder.messageText2.setEmojiSize(80);
                viewHolder.messageText2.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.rightfade));
                viewHolder.messageText2.setText(c.getMessage());
                viewHolder.special_msg.setVisibility(View.GONE);




            } else {
                viewHolder.messageText.setEmojiSize(80);
                viewHolder.message_text_layout.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.leftfade));
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.special_msg.setVisibility(View.GONE);
            }


        } else if (message_type.equals("special")) {

            viewHolder.audio_layout.setVisibility(View.GONE);
            viewHolder.audio_layout2.setVisibility(View.GONE);
            viewHolder.doc_layout2.setVisibility(View.GONE);
            viewHolder.doc_layout.setVisibility(View.GONE);
            viewHolder.messageText2.setVisibility(View.GONE);
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.messageImage2.setVisibility(View.GONE);
            viewHolder.grp_request.setVisibility(View.GONE);


            DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("Users");


            if (from_user.equals(sender_user)) {
                mUser.child(c.getTo()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name2 = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        viewHolder.special_msg.setVisibility(View.VISIBLE);
                        viewHolder.special_msg.setText("You" + " and " + name2 + " are now friends");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            } else {
                mUser.child(c.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name1 = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        viewHolder.special_msg.setVisibility(View.VISIBLE);
                        viewHolder.special_msg.setText("You" + " and " + name1 + " are now friends");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        } else if (message_type.equals("image")) {


            if (from_user.equals(sender_user)) {

                if (c.getCaption().equals("")) {
                    viewHolder.caption2.setVisibility(View.GONE);
                } else {
                    viewHolder.caption2.setVisibility(View.VISIBLE);
                    viewHolder.caption2.setText(c.getCaption());

                }
                viewHolder.messageImage2.setVisibility(View.VISIBLE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.image_video.setVisibility(View.GONE);
                viewHolder.not_download.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);


                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(String.valueOf(c.getTime())));
                String date = DateFormat.format("hh:mm aa", cal).toString();
                viewHolder.img_time2.setText(date);


                Uri uri = Uri.parse(c.getUri());
                String path1 = uri.getPath();
                assert path1 != null;
                File imgFile = new File(path1);
                if (imgFile.exists()) {


                    Glide.with(activity)
                            .asBitmap()
                            .error(R.drawable.default_send_image)
                            .load(path1)
                            .into(viewHolder.image2);
                } else {
                    File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Downloaded Images");
                    final File checkFile = new File(directory, c.getName());

                    if (checkFile.exists()) {

                        Glide.with(activity)
                                .asBitmap()
                                .override(50,50)
                                .error(R.drawable.default_send_image)
                                .fitCenter()
                                .format(DecodeFormat.PREFER_RGB_565)
                                .load(checkFile)
                                .into(viewHolder.image2);


                    } else {

                        Glide.with(activity)
                                .asBitmap()
                                .load(c.getMessage())
                                .format(DecodeFormat.PREFER_RGB_565)
                                .fitCenter()
                                .override(50, 50)
                                .into(viewHolder.image2);

                    }

                }


                viewHolder.messageImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent view_image = new Intent(view.getContext(), imageview_Activity.class);
                        view_image.putExtra("view_img", c.getMessage());
                        view_image.putExtra("img_ID", c.getName());
                        view_image.putExtra("filepath", path1);
                        activity.startActivity(view_image);
                        activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);


                    }
                });

            } else {

                if (c.getCaption().equals("")) {
                    viewHolder.caption1.setVisibility(View.GONE);
                } else {
                    viewHolder.caption1.setVisibility(View.VISIBLE);
                    viewHolder.caption1.setText(c.getCaption());

                }
                File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Downloaded Images");
                final File checkFile = new File(directory, c.getName());

                viewHolder.messageImage.setVisibility(View.VISIBLE);
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.image_video.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(String.valueOf(c.getTime())));
                String date = DateFormat.format("hh:mm aa", cal).toString();
                viewHolder.img_time.setText(date);

                if (checkFile.exists()) {


                    Glide.with(activity)
                            .asBitmap()
                            .error(R.drawable.default_send_image)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .load(checkFile)
                            .into(viewHolder.image);
                    viewHolder.not_download.setVisibility(View.GONE);

                } else {


                    Glide.with(activity)
                            .asBitmap()
                            .error(R.drawable.default_send_image)
                            .load(c.getMessage())
                            .fitCenter()
                            .override(50, 50)
                            .format(DecodeFormat.PREFER_RGB_565)
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
                        if (c.getMessage().equals("default")) {
                            Toast.makeText(activity, "File is being uploaded by the user.PLease Wait for some time", Toast.LENGTH_LONG).show();
                        } else {
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


        } else if (message_type.equals("video")) {
            if (from_user.equals(sender_user)) {
                viewHolder.messageImage2.setVisibility(View.VISIBLE);
                viewHolder.image2_video.setVisibility(View.VISIBLE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);

                RequestOptions myOptions = new RequestOptions()

                        .override(50, 50);
                Glide.with(activity)
                        .asBitmap()
                        .apply(myOptions)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .load(Uri.fromFile(new File(c.getFilepath())))
                        .into(viewHolder.image2);

                viewHolder.messageImage2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), play_video.class);
                        intent.putExtra("path", c.getFilepath());
                        intent.putExtra("uid", c.getFrom());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);
                    }
                });


            } else {
                viewHolder.messageImage.setVisibility(View.VISIBLE);
                viewHolder.image_video.setVisibility(View.VISIBLE);
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);


                RequestOptions myOptions = new RequestOptions()

                        .override(50, 50);
                Glide.with(activity)
                        .asBitmap()
                        .apply(myOptions)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .load(c.getMessage())
                        .into(viewHolder.image);

                viewHolder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), play_video.class);
                        intent.putExtra("video_url", c.getMessage());
                        intent.putExtra("uid", c.getFrom());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.zoom_in, R.anim.no_anim);
                    }
                });
            }
        } else if (message_type.equals("invite")) {


            if (from_user.equals(sender_user)) {
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);


                DatabaseReference mUser2 = FirebaseDatabase.getInstance().getReference().child("Users").child(c.getTo());
                mUser2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        viewHolder.grp_text.setText("You requested " + name + " to join " + c.getMessage());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference mGrp = FirebaseDatabase.getInstance().getReference().child("Groups").child(c.getMessage()).child("Users");
                mGrp.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(c.getTo())) {
                            viewHolder.messageImage2.setVisibility(View.GONE);
                            viewHolder.image2_video.setVisibility(View.GONE);
                            viewHolder.messageImage.setVisibility(View.GONE);
                            viewHolder.messageText.setVisibility(View.GONE);
                            viewHolder.messageText2.setVisibility(View.GONE);
                            viewHolder.grp_request.setVisibility(View.VISIBLE);
                            viewHolder.special_msg.setVisibility(View.GONE);
                            viewHolder.audio_layout.setVisibility(View.GONE);
                            viewHolder.audio_layout2.setVisibility(View.GONE);
                            viewHolder.doc_layout2.setVisibility(View.GONE);
                            viewHolder.doc_layout.setVisibility(View.GONE);
                            viewHolder.grp_join.setEnabled(false);
                            viewHolder.grp_join.setText("Already Joined");
                            viewHolder.grp_decline.setVisibility(View.GONE);
                            
                        } else {
                            viewHolder.messageImage2.setVisibility(View.GONE);
                            viewHolder.image2_video.setVisibility(View.GONE);
                            viewHolder.messageImage.setVisibility(View.GONE);
                            viewHolder.messageText.setVisibility(View.GONE);
                            viewHolder.messageText2.setVisibility(View.GONE);
                            viewHolder.grp_request.setVisibility(View.VISIBLE);
                            viewHolder.special_msg.setVisibility(View.GONE);
                            viewHolder.grp_join.setEnabled(false);
                            viewHolder.audio_layout.setVisibility(View.GONE);
                            viewHolder.audio_layout2.setVisibility(View.GONE);
                            viewHolder.doc_layout2.setVisibility(View.GONE);
                            viewHolder.doc_layout.setVisibility(View.GONE);

                            viewHolder.grp_join.setText("Requested");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            } else {

                DatabaseReference mUser2 = FirebaseDatabase.getInstance().getReference().child("Users").child(c.getFrom());
                mUser2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        viewHolder.grp_text.setText(name + " has requested you to join " + c.getMessage());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.grp_request.setVisibility(View.VISIBLE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);


                String mCurrent_uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


                DatabaseReference mGrpCheck = FirebaseDatabase.getInstance().getReference().child("Groups").child(c.getMessage()).child("Users");
                mGrpCheck.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            viewHolder.grp_join.setEnabled(false);
                            viewHolder.grp_join.setText("Already Joined");
                            viewHolder.grp_decline.setVisibility(View.GONE);
                        } else {

                            viewHolder.grp_join.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Groups")
                                            .child(c.getMessage()).child("Users");
                                    final Map<String, Object> messageMap = new HashMap<String, Object>();
                                    messageMap.put(mCurrent_uid, mCurrent_uid);
                                    mDatabase3.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

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
            }

        } else if (message_type.equals("audio")) {
            if (from_user.equals(sender_user)) {

                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.grp_request.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.VISIBLE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);

            } else {
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.grp_request.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.VISIBLE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.GONE);
            }
        }
        else if (message_type.equals("document")) {
            if (from_user.equals(sender_user)) {
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.grp_request.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.VISIBLE);
                viewHolder.doc_layout.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);


                viewHolder.doc_layout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            openFile(activity.getApplicationContext(),new File(c.getFilepath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                viewHolder.doc_open2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            openFile(activity.getApplicationContext(),new File(c.getFilepath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                String s= c.getName();
                String[] parts = s.split("\\."); // escape .
                String part2 = parts[1];

                viewHolder.extension2.setText("Document."+part2);


            }else{
                viewHolder.messageImage2.setVisibility(View.GONE);
                viewHolder.image2_video.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageText2.setVisibility(View.GONE);
                viewHolder.grp_request.setVisibility(View.GONE);
                viewHolder.special_msg.setVisibility(View.GONE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout2.setVisibility(View.GONE);
                viewHolder.doc_layout.setVisibility(View.VISIBLE);
                viewHolder.audio_layout2.setVisibility(View.GONE);
                viewHolder.audio_layout.setVisibility(View.GONE);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl(c.getMessage());

                String s= c.getName();
                String[] parts = s.split("\\."); // escape .
                String part2 = parts[1];
                viewHolder.extension.setText("Document."+part2);


                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Message Documents");
                if (!directory.exists()){
                    if (directory.mkdirs())
                    {

                        Log.i("CreateDir","App dir created");
                    }
                    else
                    {
                        Log.w("CreateDir","Unable to create app dir!");
                    }


                }


                final File localFile = new File(directory,c.getName());
                if (localFile.exists()){

                    viewHolder.doc_open.setVisibility(View.VISIBLE);
                    viewHolder.doc_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                openFile(activity.getApplicationContext(),localFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    viewHolder.doc_open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                openFile(activity.getApplicationContext(),localFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }else{
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            viewHolder.doc_prg.setVisibility(View.GONE);
                            viewHolder.doc_open.setVisibility(View.VISIBLE);
                            viewHolder.doc_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        openFile(activity.getApplicationContext(),localFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            viewHolder.doc_open.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        openFile(activity.getApplicationContext(),localFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                            viewHolder.doc_prg.setVisibility(View.VISIBLE);

                        }
                    });
                }





            }
        }

    }



        public static void openFile(Context context, File url) throws IOException {
            // Create URI

            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // ZIP audio file
                intent.setDataAndType(uri, "application/zip");
            } else if(url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if(url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if(url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }



        private Boolean hasAlphabet(String str){

             str = str.toLowerCase();
            char[] charArray = str.toCharArray();
            for (int k = 0; k < charArray.length; k++) {
                char ch = charArray[k];
                if (!(ch >= 'a' && ch <= 'z')){

                    return false;
                }

            }
            return true;
        }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}