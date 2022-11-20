package in.macro.codes.Kncok.Groups;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.PathUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.ChatActivity;
import in.macro.codes.Kncok.ImageUploadFinal;
import in.macro.codes.Kncok.Messages;
import in.macro.codes.Kncok.PathUtilvideo;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.RealPathUtils;

public class GroupChatActivty extends AppCompatActivity {

    private static final String TAG = "yeah";
    private static final int VIDEO_PICK = 2;
    private String mChatUser;
    private Toolbar mChatToolbar;
    List<String> imagesEncodedList;
    private DatabaseReference mRootRef;
    String fileRealPath,extension;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private LinearLayout msg_layout;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;

    private GroupChatAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 1000;
    private int mCurrentPage = 1;
    InputStream inputStream;
    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;
    String group_name;


    //New Solution
    private int itemPos = 0;

    private String gmod;
    int tap =0;
    private String mLastKey = "";
    private String mPrevKey = "";
    String imageEncoded;
    private RelativeLayout main_bg,close_main_bg;
    private LinearLayout bg_linear1,bg_linear2;
    private CircleImageView camera,gallery,audio,video,location,microphone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        mChatToolbar = (Toolbar) findViewById(R.id.gchat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();



        group_name = getIntent().getStringExtra("group_name");
        gmod =getIntent().getStringExtra("grp_mode");


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);

        msg_layout=(LinearLayout)findViewById(R.id.glinearLayout);
        mChatAddBtn = (ImageButton) findViewById(R.id.gchat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.gchat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.gchat_message_view);

        close_main_bg=(RelativeLayout)findViewById(R.id.gclose_main_bg);
        main_bg=(RelativeLayout)findViewById(R.id.gbackground);
        bg_linear1=(LinearLayout)findViewById(R.id.gbg_linear1);
        bg_linear2=(LinearLayout)findViewById(R.id.gbg_linear2);
        camera=(CircleImageView)findViewById(R.id.gcamera);
        gallery=(CircleImageView)findViewById(R.id.ggallery);
        video=(CircleImageView)findViewById(R.id.gvideo);
        audio=(CircleImageView)findViewById(R.id.gaudio);
        location=(CircleImageView)findViewById(R.id.gdocument);
        microphone=(CircleImageView)findViewById(R.id.gmicrophone);
        mAdapter = new GroupChatAdapter(GroupChatActivty.this,messagesList,group_name,gmod);

        mMessagesList = (RecyclerView) findViewById(R.id.gmessages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.gmessage_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);



        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();






        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GroupChatActivty.this, GroupProfileActivity.class);
               intent.putExtra("gname",group_name);
                ActivityOptions activityOptions= ActivityOptions.makeCustomAnimation(GroupChatActivty.this,R.anim.profile_enter,R.anim.nothing);
                startActivity(intent,activityOptions.toBundle());
            }
        });

        DatabaseReference mGroup = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_name);
        mGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String image = Objects.requireNonNull(dataSnapshot.child("gimage").getValue()).toString();
               if (!image.equals("default")){
                   Picasso.with(GroupChatActivty.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                           .placeholder(R.drawable.default_send_image).into( mProfileImage, new Callback() {
                       @Override
                       public void onSuccess() {

                       }

                       @Override
                       public void onError() {
                           Picasso.with( GroupChatActivty.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                       }
                   });
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        mTitleView.setText(group_name);


        //TODO BACKGROUND SERVICES HAVE BEEN STARTED FROM BELOW..............................................................................


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Background Task=new Background(GroupChatActivty.this);
                Task.doInBackground("send_message");
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_add();
                Background Task=new Background(GroupChatActivty.this);
                Task.doInBackground("img_select");
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_add();
                Background Task=new Background(GroupChatActivty.this);
                Task.doInBackground("video_select");
            }
        });


        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(tap == 0 ){

                        close_main_bg.setVisibility(View.VISIBLE);
                        main_bg.setVisibility(View.VISIBLE);
                        Animation animation   =    AnimationUtils.loadAnimation(GroupChatActivty.this, R.anim.rg3);
                        animation.setDuration(200);
                        main_bg.setAnimation(animation);
                        main_bg.animate();
                        animation.start();
                        mChatAddBtn.setImageResource(R.drawable.ic_baseline_close_24);
                        tap =1;

                    }else{
                        tap = 0;
                        close_add();
                    }


            }
        });

        close_main_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_add();
            }
        });





        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });
        mRefreshLayout.setRefreshing(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                GroupChatActivty.Background background=new Background(GroupChatActivty.this);
                background.execute("load_messages");
                mRefreshLayout.setRefreshing(false);
            }
        },1);



        Background background2=new Background(GroupChatActivty.this);
        background2.execute("check_grp_mode");

    }


    public void close_add(){

        if (main_bg.getVisibility()==View.VISIBLE){


            main_bg.setVisibility(View.GONE);
            Animation animation   =    AnimationUtils.loadAnimation(GroupChatActivty.this, R.anim.profile_exit);
            animation.setDuration(200);
            main_bg.setAnimation(animation);
            main_bg.animate();
            animation.start();

            mChatAddBtn.setImageResource(R.drawable.ic_baseline_attach_file_24);
            close_main_bg.setVisibility(View.GONE);



        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            final Uri imageUri = data.getData();

            if(Build.VERSION.SDK_INT < 26){
                try {
                    assert imageUri != null;
                    fileRealPath= PathUtil.getPath(GroupChatActivty.this,imageUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    fileRealPath =PathUtilvideo.getPath(GroupChatActivty.this,imageUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            Intent intent =new Intent(GroupChatActivty.this, GroupImageEditActivity.class);
            intent.putExtra("current_id",mCurrentUserId);
            intent.putExtra("user_id",mChatUser);
            intent.putExtra("gname",group_name);
            intent.putExtra("type","single");
            intent.putExtra("uri",imageUri.toString());
            intent.putExtra("filepath",fileRealPath);
            startActivity(intent);







        }else if (requestCode==VIDEO_PICK && resultCode == RESULT_OK){
            final Uri videoUri = data.getData();


            if (Build.VERSION.SDK_INT < 11)
                fileRealPath = RealPathUtils.getRealPathFromURI_BelowAPI11(GroupChatActivty.this, videoUri);

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                fileRealPath = RealPathUtils.getRealPathFromURI_API11to18(GroupChatActivty.this, videoUri);

                // SDK > 19 (Android 4.4)
            else {
                String id = DocumentsContract.getDocumentId(videoUri);
                try {
                    inputStream  = getContentResolver().openInputStream(videoUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                File file = new File(getCacheDir().getAbsolutePath()+"/"+id);
                writeFile(inputStream, file);
                fileRealPath = file.getAbsolutePath();
            }

            extension = fileRealPath.substring(fileRealPath.lastIndexOf("."));

            final String current_user_ref = "Groups/" + group_name + "/" + "gmessages";
            DatabaseReference user_message_push = mRootRef.child(group_name).child("gmessages").push();

            final String push_id = user_message_push.getKey();
            final String filename=push_id+extension;
            final StorageReference filepath = mImageStorage.child("message_videos").child( push_id + extension);


            assert videoUri != null;


            final Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("name",filename);
            messageMap.put("seen", false);
            messageMap.put("message", "default");
            messageMap.put("filepath",fileRealPath);
            messageMap.put("type", "video");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map<String, Object> messageUserMap = new HashMap<String, Object>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);


            mChatMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    assert videoUri != null;

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });


            filepath.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()){

                                    final String download_url = task.getResult().toString();
                                    assert push_id != null;
                                    DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Groups")
                                            .child(group_name).child("gmessages").child(push_id);
                                    updateData.child("message").setValue(download_url);
                                }
                            }
                        });
                    }

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());


                }
            });



        }

    }

    void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child(group_name).child("gmessages");

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

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



    private void sendMessage() {


        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){
            final String current_user_ref = "Groups/" + group_name + "/" + "gmessages";
            DatabaseReference user_message_push = mRootRef.child(group_name).child("gmessages").push();

            String push_id = user_message_push.getKey();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map<String, Object> messageUserMap = new HashMap<String, Object>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");



            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }

    }



    public static class Background extends AsyncTask<String ,String,String>{
        private WeakReference<GroupChatActivty>activityWeakReference;
        Background(GroupChatActivty activity){
            activityWeakReference=new WeakReference<GroupChatActivty>(activity);
        }
        @Override
        protected String doInBackground(String... strings) {
            final GroupChatActivty activity=activityWeakReference.get();




            switch (strings[0]){

                case "img_select" :

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    photoPickerIntent.setType("image/*"); //add video/* for video selection
                    activity.startActivityForResult(photoPickerIntent, GALLERY_PICK);
                    break;


                case "load_messages" :
                    DatabaseReference messageRef = activity.mRootRef.child("Groups").child(activity.group_name).child("gmessages");

                    Query messageQuery = messageRef.limitToLast(activity.mCurrentPage * TOTAL_ITEMS_TO_LOAD);


                    messageQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            Messages message = dataSnapshot.getValue(Messages.class);

                            activity.itemPos++;

                            if(activity.itemPos == 1){

                                String messageKey = dataSnapshot.getKey();

                                activity.mLastKey = messageKey;
                                activity.mPrevKey = messageKey;

                            }

                            activity.messagesList.add(message);

                            activity.mAdapter.notifyDataSetChanged();


                            activity.mMessagesList.scrollToPosition(activity.messagesList.size() -1);
                            activity.mRefreshLayout.setRefreshing(false);

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
                    break;

                case "send_message" :
                    activity.sendMessage();
                    break;




                case "video_select" :

                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_PICK);
                    break;

                case "check_grp_mode" :
                    DatabaseReference mGroups = FirebaseDatabase.getInstance().getReference().child("Groups").child(activity.group_name);
                    mGroups.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String mode = Objects.requireNonNull(dataSnapshot.child("grp_mode").getValue()).toString();


                            if (mode.equals("admin")){
                                if (dataSnapshot.child("Admin").hasChild(activity.mCurrentUserId)){
                                    activity.msg_layout.setVisibility(View.VISIBLE);
                                } else {
                                    activity.msg_layout.setVisibility(View.GONE);
                                }
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grp_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.grp_set){
            Intent settingsIntent = new Intent(GroupChatActivty.this, GroupSettingsActivity.class);
            settingsIntent.putExtra("group_name",group_name);
            startActivity(settingsIntent);
        }
        if(item.getItemId()==R.id.grp_info){
            Intent settingsIntent = new Intent(GroupChatActivty.this, GroupInviteActivity.class);
            settingsIntent.putExtra("gname",group_name);
            startActivity(settingsIntent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (main_bg.getVisibility()==View.VISIBLE){
            main_bg.setVisibility(View.GONE);
            Animation animation   =    AnimationUtils.loadAnimation(GroupChatActivty.this, R.anim.profile_exit);
            animation.setDuration(200);
            main_bg.setAnimation(animation);
            main_bg.animate();
            animation.start();


            close_main_bg.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }

    }
}
