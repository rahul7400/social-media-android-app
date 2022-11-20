package in.macro.codes.Kncok;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;


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
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.Story.AddStoryFinal;


public class ChatActivity extends AppCompatActivity {


    private static final String TAG = "yeah";
    private static final int VIDEO_PICK = 2;
    private static final int CAMERA_PICK = 5;
    private static final int AUDIO_PICK = 3;
    private static final int REQUEST_CODE_DOC = 4;

    int tap =0;
    private String mChatUser;
    private Uri cameraUri;
    private Toolbar mChatToolbar;
    List<String> imagesEncodedList;
    private DatabaseReference mRootRef;
    String fileRealPath,extension;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private List<Uri>multipleUri = new ArrayList<>();

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;

    private EmojiEditText mChatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;

    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 2000;
    private int mCurrentPage = 1;
    InputStream inputStream;
    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;
    private AnimationDrawable animationDrawable;

    private ImageView emojiMenu;



    private LinearLayout messageLayout,pointint_layout;
    private TextView pointing_msg;
    private ImageView pointing_close;
    //New Solution
    private int itemPos = 0;
    private RelativeLayout container;

    private String mLastKey = "";
    private String mPrevKey = "";
    String imageEncoded;
    int y=0;
    private List<String> data = new ArrayList<>();
    DatabaseReference mTyping;
    String image;
    private RelativeLayout main_bg,close_main_bg;
    private LinearLayout bg_linear1,bg_linear2;
    private CircleImageView camera,gallery,audio,video,document,microphone;
    long delay = 2000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    EmojiPopup emojiPopup;
    private Runnable input_finish_checker ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatMessageView = (EmojiEditText) findViewById(R.id.chat_message_view);
        container=(RelativeLayout)findViewById(R.id.container) ;
        emojiPopup = EmojiPopup.Builder.fromRootView(container).build(mChatMessageView);
        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----
         image = getIntent().getStringExtra("profile");
        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        messageLayout = (LinearLayout) findViewById(R.id.linearLayout);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);


        mAdapter = new MessageAdapter(ChatActivity.this,messagesList);

        close_main_bg=(RelativeLayout)findViewById(R.id.close_main_bg);
        main_bg=(RelativeLayout)findViewById(R.id.background);
        bg_linear1=(LinearLayout)findViewById(R.id.bg_linear1);
        bg_linear2=(LinearLayout)findViewById(R.id.bg_linear2);
        camera=(CircleImageView)findViewById(R.id.camera);
        gallery=(CircleImageView)findViewById(R.id.gallery);
        video=(CircleImageView)findViewById(R.id.video);
        audio=(CircleImageView)findViewById(R.id.audio);
        document=(CircleImageView)findViewById(R.id.document);
        microphone=(CircleImageView)findViewById(R.id.microphone);


        emojiMenu = (ImageView) findViewById(R.id.emojiMenu);



        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mRefreshLayout.setRefreshing(true);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);

        /*animationDrawable = (AnimationDrawable) container.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();*/







        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatActivity.this,ProfileActivity.class);
                intent.putExtra("user_id",mChatUser);
                intent.putExtra("profile",image);
                ActivityOptions activityOptions= ActivityOptions.makeCustomAnimation(ChatActivity.this,R.anim.profile_enter,R.anim.nothing);
                startActivity(intent,activityOptions.toBundle());
            }
        });

        emojiMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emojiPopup.toggle(); // Toggles visibility of the Popup.
            }
        });



        mTitleView.setText(userName);


        //TODO BACKGROUND SERVICES HAVE BEEN STARTED FROM BELOW..............................................................................




       Handler handler = new Handler();
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {

               Background background=new Background(ChatActivity.this);
               background.execute("load_messages");
               mRefreshLayout.setRefreshing(false);
           }
       },1);



        Background getSeenTime=new Background(ChatActivity.this);
        getSeenTime.execute("getSeenTime");

        Background setChatTIme = new Background(ChatActivity.this);
        setChatTIme.execute("setChatTime");


        mTyping = mRootRef.child("Users")
                .child(mCurrentUserId);

        input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    final Map<String, Object> typingMap = new HashMap<String, Object>();
                    typingMap.put("value", "false");
                    typingMap.put("to", mChatUser);
                    mTyping.child("typing").setValue(typingMap);

                }else{
                    final Map<String, Object> typingMap = new HashMap<String, Object>();

                    typingMap.put("value", "true");
                    typingMap.put("to", mChatUser);
                    mTyping.child("typing").setValue(typingMap);

                }
            }
        };


        mChatMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final Map<String, Object> typingMap = new HashMap<String, Object>();
                typingMap.put("value", "true");
                typingMap.put("to", mChatUser);
                mTyping.child("typing").setValue(typingMap);            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(input_finish_checker);
            }


            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }else{

                    mTyping.child("typing").child("value").setValue("false");

                }

            }
        });
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Background Task=new Background(ChatActivity.this);
                Task.doInBackground("send_message");
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_add();
                Background Task=new Background(ChatActivity.this);
                Task.doInBackground("cam_select");
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_add();
                Background Task=new Background(ChatActivity.this);
                Task.doInBackground("img_select");
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_add();
                Toast.makeText(ChatActivity.this, "Currently app is in testing mode. Coming soon.", Toast.LENGTH_SHORT).show();
//                Background Task=new Background(ChatActivity.this);
//                Task.doInBackground("video_select");
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_add();
                Toast.makeText(ChatActivity.this, "Currently app is in testing mode. Coming soon.", Toast.LENGTH_SHORT).show();

//                Background Task=new Background(ChatActivity.this);
//                Task.doInBackground("audio_select");
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_add();
                Toast.makeText(ChatActivity.this, "Currently app is in testing mode. Coming soon.", Toast.LENGTH_SHORT).show();

//                Background Task=new Background(ChatActivity.this);
//                Task.doInBackground("doc_select");
            }
        });

        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_add();
                Toast.makeText(ChatActivity.this, "Currently app is in testing mode. Coming soon.", Toast.LENGTH_SHORT).show();

//                Background Task=new Background(ChatActivity.this);
//                Task.doInBackground("doc_select");
            }
        });


        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(tap ==0){
                    close_main_bg.setVisibility(View.VISIBLE);
                    main_bg.setVisibility(View.VISIBLE);
                    Animation animation   =    AnimationUtils.loadAnimation(ChatActivity.this, R.anim.rg3);
                    animation.setDuration(200);
                    main_bg.setAnimation(animation);
                    main_bg.animate();
                    animation.start();
                    tap =1;
                    mChatAddBtn.setImageResource(R.drawable.ic_baseline_close_24);
                }else{

                    tap =0;
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









    }

    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void close_add(){

        if (main_bg.getVisibility()==View.VISIBLE){

            mChatAddBtn.setImageResource(R.drawable.ic_baseline_attach_file_24);
            main_bg.setVisibility(View.GONE);
            Animation animation   =    AnimationUtils.loadAnimation(ChatActivity.this, R.anim.profile_exit);
            animation.setDuration(200);
            main_bg.setAnimation(animation);
            main_bg.animate();
            animation.start();


            close_main_bg.setVisibility(View.GONE);



        }else if (emojiPopup.isShowing()){
            emojiPopup.dismiss();
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


                if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
                    if (data.getData()!=null){
                    final Uri imageUri = data.getData();
                    if(Build.VERSION.SDK_INT < 26){
                        try {
                            assert imageUri != null;
                            fileRealPath= PathUtil.getPath(ChatActivity.this,imageUri);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            fileRealPath =PathUtilvideo.getPath(ChatActivity.this,imageUri);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }


                    Intent intent =new Intent(ChatActivity.this,ImageUploadFinal.class);
                    intent.putExtra("current_id",mCurrentUserId);
                    intent.putExtra("user_id",mChatUser);
                    intent.putExtra("type","single");
                    intent.putExtra("uri",imageUri.toString());
                    intent.putExtra("filepath",fileRealPath);
                    startActivity(intent);

                }else if (data.getClipData()!=null){
                int total = data.getClipData().getItemCount();

                for (int i=0; i<total;i++){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    multipleUri.add(imageUri);

                    Intent intent =new Intent(ChatActivity.this,ImageUploadFinal.class);
                    intent.putExtra("current_id",mCurrentUserId);
                    intent.putExtra("user_id",mChatUser);
                    intent.putExtra("type","multiple");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", (Parcelable) multipleUri);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

            }


           /* filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()){



                                }
                            }
                        });
                    }

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();



                }
            });*/

        }else if (requestCode==CAMERA_PICK && resultCode == RESULT_OK){


                Uri selectedImage = cameraUri;
                if(Build.VERSION.SDK_INT < 26){
                    try {
                        assert selectedImage != null;
                        fileRealPath= PathUtil.getPath(ChatActivity.this,selectedImage);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        fileRealPath =PathUtilvideo.getPath(ChatActivity.this,selectedImage);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }


                Intent intent =new Intent(ChatActivity.this,ImageUploadFinal.class);
                intent.putExtra("current_id",mCurrentUserId);
                intent.putExtra("user_id",mChatUser);
                intent.putExtra("type","single");
                intent.putExtra("uri",selectedImage.toString());
                intent.putExtra("filepath",fileRealPath);
                startActivity(intent);


        }
        else if (requestCode==VIDEO_PICK && resultCode == RESULT_OK){
            final Uri videoUri = data.getData();
            if(Build.VERSION.SDK_INT < 26){
                try {
                    assert videoUri != null;
                    fileRealPath= PathUtil.getPath(ChatActivity.this,videoUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }else{
                fileRealPath = RealPathUtils.getRealPathFromURI_API19(ChatActivity.this, videoUri);
            }

            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock");
          /*  GiraffeCompressor.create() //two implementations: mediacodec and ffmpeg,default is mediacodec
                    .input(String.valueOf(videoUri)) //set video to be compressed
                    .output(file) //set compressed video output
                    .bitRate(20173600)//set bitrate 码率
                    .resizeFactor((float) 1.0)
                    .ready()
                    .observeOn(AndroidSchedulers.);*/

            /*Intent intent = new Intent(ChatActivity.this,VideoUploadFinal.class);
            assert videoUri != null;
            intent.putExtra("uri",videoUri.toString());
            intent.putExtra("user_id",mChatUser);
            intent.putExtra("current_id",mCurrentUserId);
            intent.putExtra("path",fileRealPath);
            startActivity(intent);*/

        }
        else if (requestCode==AUDIO_PICK && resultCode == RESULT_OK){

            final Uri audioUri = data.getData();
            if(Build.VERSION.SDK_INT < 26){
                try {
                    assert audioUri != null;
                    fileRealPath= PathUtil.getPath(ChatActivity.this,audioUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    fileRealPath =PathUtilvideo.getPath(ChatActivity.this,audioUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }




            final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_push.getKey();
            final String filename=push_id+".mp3";
            final StorageReference filepath = mImageStorage.child("message_audio").child( filename);


            assert audioUri != null;


            final Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("name",filename);
            messageMap.put("seen", false);
            messageMap.put("message", "default");
            messageMap.put("filepath",fileRealPath);
            messageMap.put("type", "audio");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map<String, Object> messageUserMap = new HashMap<String, Object>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    assert audioUri != null;

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });


            filepath.putFile(audioUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()){

                                    final String download_url = task.getResult().toString();
                                    assert push_id != null;
                                    DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("messages")
                                            .child(mCurrentUserId).child(mChatUser).child(push_id);
                                    updateData.child("message").setValue(download_url);

                                    DatabaseReference updateData2 = FirebaseDatabase.getInstance().getReference("messages")
                                            .child(mChatUser).child(mCurrentUserId).child(push_id);
                                    updateData2.child("message").setValue(download_url);



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

        } else if (requestCode==REQUEST_CODE_DOC && resultCode == RESULT_OK){
            final Uri docUri = data.getData();

            String newPath = "";
            if(Build.VERSION.SDK_INT < 26){
                try {
                    assert docUri != null;
                    newPath= PathUtil.getPath(ChatActivity.this,docUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    newPath =PathUtilvideo.getPath(ChatActivity.this,docUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }


            Toast.makeText(this, "path - " + newPath, Toast.LENGTH_SHORT).show();
            assert docUri != null;
            String extension =  getMimeType(ChatActivity.this,docUri);

            final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            final String push_id = user_message_push.getKey();
            final String filename = push_id+"."+extension;
            final StorageReference filepath = mImageStorage.child("message_documents").child( filename);





            final Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("name",filename);
            messageMap.put("seen", false);
            messageMap.put("message", "default");
            messageMap.put("filepath",newPath);
            messageMap.put("type", "document");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map<String, Object> messageUserMap = new HashMap<String, Object>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    assert docUri != null;

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });


            filepath.putFile(docUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){
                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()){

                                    final String download_url = task.getResult().toString();
                                    assert push_id != null;
                                    DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("messages")
                                            .child(mCurrentUserId).child(mChatUser).child(push_id);
                                    updateData.child("message").setValue(download_url);

                                    DatabaseReference updateData2 = FirebaseDatabase.getInstance().getReference("messages")
                                            .child(mChatUser).child(mCurrentUserId).child(push_id);
                                    updateData2.child("message").setValue(download_url);



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

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

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
        mTyping.child("typing").child("value").setValue("false");

        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map<String, Object> messageUserMap = new HashMap<String, Object>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    mMessagesList.scrollToPosition(messagesList.size() - 1);
                    if(databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }



    public static class Background extends AsyncTask<String ,String,String>{
        private WeakReference<ChatActivity>activityWeakReference;
        Background(ChatActivity activity){
            activityWeakReference=new WeakReference<ChatActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final ChatActivity activity=activityWeakReference.get();




            switch (strings[0]){

                case "img_select" :


                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    photoPickerIntent.setType("image/*"); //add video/* for video selection
                    activity.startActivityForResult(photoPickerIntent, GALLERY_PICK);
                    break;

                case "cam_select" :

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                    Uri uri =   FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider",photo);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            uri);
                    activity.cameraUri = Uri.fromFile(photo);
                    activity.startActivityForResult(cameraIntent, CAMERA_PICK);
                    break;
                case "load_messages" :
                    DatabaseReference messageRef = activity.mRootRef.child("messages").child(activity.mCurrentUserId).child(activity.mChatUser);
                    messageRef.keepSynced(true);

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
                            activity. mAdapter.notifyDataSetChanged();
                            assert message != null;

                            activity.mMessagesList.scrollToPosition(activity.messagesList.size() - 1);





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

                case "getSeenTime" :

                    activity.mRootRef.child("Users").child(activity.mChatUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String online = dataSnapshot.child("online").getValue().toString();


                            if(dataSnapshot.hasChild("typing") && dataSnapshot.child("typing")
                                    .child("to").equals(activity.mCurrentUserId)) {


                                String userOnline = dataSnapshot.child("typing").getValue().toString();
                                if(userOnline.equals("true")){
                                    activity.mLastSeenView.setText("typing..");
                                } else {

                                    if(online.equals("true")) {

                                        activity.mLastSeenView.setText("online");

                                    } else {

                                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                                        long lastTime = Long.parseLong(online);
                                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, activity.getApplicationContext());
                                        activity.mLastSeenView.setText(lastSeenTime);

                                    }

                                }


                            }


                            Picasso.with(activity.getApplicationContext()).load(activity.image).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.default_avatar).into(activity.mProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(activity.getApplicationContext()).load(activity.image)
                                            .placeholder(R.drawable.default_avatar).into(activity.mProfileImage);

                                }
                            });




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;


                case "setChatTime" :
                    activity.mRootRef.child("Chat").child(activity.mCurrentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(!dataSnapshot.hasChild(activity.mChatUser)){

                                Map<String, Object> chatAddMap = new HashMap<>();
                                chatAddMap.put("seen", false);
                                chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                                Map<String, Object> chatUserMap = new HashMap<String, Object>();
                                chatUserMap.put("Chat/" + activity.mCurrentUserId + "/" + activity.mChatUser, chatAddMap);
                                chatUserMap.put("Chat/" + activity.mChatUser + "/" + activity.mCurrentUserId, chatAddMap);

                                activity.mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if(databaseError != null){

                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                        }

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;

                case "video_select" :

                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent,"Select Video"),VIDEO_PICK);
                    break;

                case "audio_select" :

                    Intent intent2 = new Intent();
                    intent2.setType("audio/*");
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent2,"Select Audio"),AUDIO_PICK);
                    break;


                case "doc_select" :
                    activity.browseDocuments();
                    break;

            }




            return null;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }
    @Override
    public void onBackPressed() {

        if (main_bg.getVisibility()==View.VISIBLE){
            main_bg.setVisibility(View.GONE);
            Animation animation   =    AnimationUtils.loadAnimation(ChatActivity.this, R.anim.profile_exit);
            animation.setDuration(200);
            main_bg.setAnimation(animation);
            main_bg.animate();
            animation.start();
            close_main_bg.setVisibility(View.GONE);
        }else if (emojiPopup.isShowing()){
            emojiPopup.dismiss();

        }else{
            super.onBackPressed();
        }




    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void browseDocuments(){

        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip", "application/rar,", "application/vnd.android.package-archive"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), REQUEST_CODE_DOC);

    }



    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }
}
