package in.macro.codes.Kncok.Story;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Service.Sviewer;
import in.macro.codes.Kncok.Users;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {


    int counter = 0;
    int vcounter = 0;
    long pressTime = 0L;
    long limit = 500L;

    Boolean isClicked =false;
    int firstVideo =0 ;

    StoriesProgressView storiesProgressView;
    ImageView image, story_photo;
    TextView story_username,sCaption,countComments;

    VideoView videoView;


    Users users;
    Boolean isclicked = false;
    CommentAdapter mAdapter;
    List<String> imageurl;
    List<String> Filefullname;
    List<String> friendList;
    List<String> UserIdList;
    List<String> backgroundurl;
    List<String> storyid;
    List<Float>  size;
    List<Float>  x;
    List<Float>  y;
    List<String> caption;
    List<String> type;
    ProgressBar story_prg;
    List<Comments> mComList = new ArrayList<>();
    List<Comments> mComList2= new ArrayList<>();
    String userid,future_userid;
    RecyclerView mConvView;

    EditText comment;
    CircleImageView sendComment,commentProfile;

    NestedScrollView nestedScrollView;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()){
                case  MotionEvent.ACTION_DOWN :
                    pressTime =System.currentTimeMillis();
                    storiesProgressView.pause();
                    return  false;

                case  MotionEvent.ACTION_UP :
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;

            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.story_image);
        story_photo = findViewById(R.id.story_photo);
        story_username = findViewById(R.id.story_username);
        videoView =(VideoView) findViewById(R.id.story_video);
        mConvView = (RecyclerView) findViewById(R.id.commentDisp);
        mAdapter = new CommentAdapter(StoryActivity.this,mComList);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        mConvView.setLayoutManager(linearLayoutManager);
        mConvView.setHasFixedSize(true);
        mConvView.setAdapter(mAdapter);
        nestedScrollView = findViewById(R.id.story_scroll_view);
        sCaption = (TextView) findViewById(R.id.scaption);
        comment = (EditText) findViewById(R.id.addcomment);
        sendComment = (CircleImageView) findViewById(R.id.sendcomment);
        commentProfile = (CircleImageView) findViewById(R.id.commentProfile);
        countComments = (TextView)  findViewById(R.id.countComments);
        userid = getIntent().getStringExtra("userid");

        story_prg=(ProgressBar) findViewById(R.id.story_prg);
        friendList = new ArrayList<>();
        UserIdList= new ArrayList<>();


        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY) {
                    //Scroll down
                    storiesProgressView.pause();
                    storiesProgressView.setVisibility(View.INVISIBLE);
                    pressTime =System.currentTimeMillis();

                }
                if (scrollY == 0) {
                    //old location
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    storiesProgressView.setVisibility(View.VISIBLE);


                }

            }
        });


        View reverse = findViewById(R.id.reverse);


        reverse.setOnClickListener( new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

                storiesProgressView.reverse();
            }

            @Override
            public void onDoubleClick(View view) {

                Toast.makeText(StoryActivity.this, "DOUBLE YEAAASSSS", Toast.LENGTH_SHORT).show();
            }
        }));

        reverse.setOnTouchListener(onTouchListener);



        View skip = findViewById(R.id.skip);
        skip.setOnClickListener( new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                storiesProgressView.skip();
            }

            @Override
            public void onDoubleClick(View view) {

                Toast.makeText(StoryActivity.this, "DOUBLE YEAAASSSS", Toast.LENGTH_SHORT).show();
            }
        }));

        skip.setOnTouchListener(onTouchListener);


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = comment.getText().toString();
                if (!text.isEmpty()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("story")
                            .child(userid).child(storyid.get(counter));
                    DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("story").push();
                    String push_id = user_message_push.getKey();






                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("comment", text);
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("userId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    Map<String, Object> messageUserMap = new HashMap<String, Object>();
                    messageUserMap.put("comments" + "/" + push_id, messageMap);

                    reference.updateChildren(messageUserMap);
                    comment.setText("");
                    hideKeyboard(StoryActivity.this);




                }
            }
        });



        getStories();
        userInfo(userid);
    }

    @Override
    public void onNext() {
        ++counter;
        sCaption.setText(caption.get(counter));
        if (type.get(counter).equals("image")){

            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",Filefullname.get(counter));
            videoView.setVisibility(View.GONE);


           if (file.exists()){
               storiesProgressView.setStoryDuration(5000L);
               Picasso.with(StoryActivity.this).load(file).into(image);
           }else{
               Picasso.with(StoryActivity.this).load(imageurl.get(counter)).networkPolicy(NetworkPolicy.OFFLINE)
                       .into(image, new Callback() {
                           @Override
                           public void onSuccess() {

                               storiesProgressView.setStoryDuration(5000L);
                               storiesProgressView.setStoriesListener(StoryActivity.this);
                               storiesProgressView.startStories(counter);

                           }

                           @Override
                           public void onError() {

                               Picasso.with(StoryActivity.this).load(imageurl.get(counter)).into(image);

                           }
                       });
               Intent intent = new Intent(StoryActivity.this, Sviewer.class);
               intent.putExtra("url" , imageurl.get(counter));
               intent.putExtra("name" , storyid.get(counter));
               intent.putExtra("type" , type.get(counter));
               startService(intent);
           }

        }else{


                String fullname = storyid.get(counter)+".mp4";
                File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",fullname);

                if (file.exists()){


                    Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).into(image);

                  //  Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).networkPolicy(NetworkPolicy.OFFLINE).into(image);

                    long timeInMillisec = getVideoTime(file);
                    storiesProgressView.setStoryDuration(timeInMillisec);

                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoPath(file.getPath());
                    videoView.setScaleX(size.get(counter));
                    videoView.setScaleY(size.get(counter));
                    videoView.setY(y.get(counter));
                    videoView.setX(x.get(counter));
                    videoView.start();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                        }
                    });

                }else{


                    String str = imageurl.get(counter);
                    Uri uri = Uri.parse(str);

                    videoView.setVideoURI(uri);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setScaleX(size.get(counter));
                    videoView.setScaleY(size.get(counter));
                    videoView.setY(y.get(counter));
                    videoView.setX(x.get(counter));
                    videoView.requestFocus();
                    videoView.start();


                    Intent intent = new Intent(StoryActivity.this, Sviewer.class);
                    intent.putExtra("url" , imageurl.get(counter));
                    intent.putExtra("name" , storyid.get(counter));
                    intent.putExtra("type" , type.get(counter));
                    startService(intent);
                }
            }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("story").child(userid);
                reference.child(storyid.get(counter)).child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mComList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Comments comments = snapshot.getValue(Comments.class);
                            if (comments!=null){
                                mComList.add(comments);
                                mAdapter.notifyDataSetChanged();
                            }



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        },10);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0 )  return;
        --counter;
        sCaption.setText(caption.get(counter));
        if (type.get(counter).equals("image")){


            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",Filefullname.get(counter));
            videoView.setVisibility(View.GONE);
            if (file.exists()){
                storiesProgressView.setStoryDuration(5000L);
                Picasso.with(StoryActivity.this).load(file).into(image);
            }else{

                Picasso.with(StoryActivity.this).load(imageurl.get(counter)).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(image, new Callback() {
                                @Override
                                public void onSuccess() {

                                    storiesProgressView.setStoryDuration(5000L);
                                    storiesProgressView.setStoriesListener(StoryActivity.this);
                                    storiesProgressView.startStories(counter);

                                }

                                @Override
                                public void onError() {

                                    Picasso.with(StoryActivity.this).load(imageurl.get(counter)).into(image);

                                }
                            });
                Intent intent = new Intent(StoryActivity.this, Sviewer.class);
                intent.putExtra("url" , imageurl.get(counter));
                intent.putExtra("name" , storyid.get(counter));
                intent.putExtra("type" , type.get(counter));
                startService(intent);
            }



        }else{

                String fullname = storyid.get(counter)+".mp4";
                File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",fullname);

                if (file.exists()){
                    Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).into(image);
                   // Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).networkPolicy(NetworkPolicy.OFFLINE).into(image);


                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(StoryActivity.this, Uri.fromFile(file));
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (time!=null){
                        long timeInMillisec = Long.parseLong(time);
                        storiesProgressView.setStoryDuration(timeInMillisec);
                    }else{
                        storiesProgressView.setStoryDuration(5000L);
                    }
                    retriever.release();

                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoPath(file.getPath());
                    videoView.setScaleX(size.get(counter));
                    videoView.setScaleY(size.get(counter));
                    videoView.setY(y.get(counter));
                    videoView.setX(x.get(counter));
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            videoView.start();
                        }
                    });

                }else{
                    String str = imageurl.get(counter);
                    Uri uri = Uri.parse(str);


                    Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).into(image);
                    videoView.setVideoURI(uri);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setScaleX(size.get(counter));
                    videoView.setScaleY(size.get(counter));
                    videoView.setY(y.get(counter));
                    videoView.setX(x.get(counter));
                    videoView.requestFocus();
                    videoView.start();


                    Intent intent = new Intent(StoryActivity.this, Sviewer.class);
                    intent.putExtra("url" , imageurl.get(counter));
                    intent.putExtra("name" , storyid.get(counter));
                    intent.putExtra("type" , type.get(counter));
                    startService(intent);
                }




        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("story").child(userid);
                reference.child(storyid.get(counter)).child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mComList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Comments comments = snapshot.getValue(Comments.class);
                            if (comments!=null){
                                mComList.add(comments);
                                mAdapter.notifyDataSetChanged();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        },10);




    }

    @Override
    public void onComplete() {




       //TODO Nest user ki story yahan lga






    }




    private void getStories(){
        imageurl = new ArrayList<>();
        storyid = new ArrayList<>();
        caption = new ArrayList<>();
        type = new ArrayList<>();
        size = new ArrayList<>();
        x= new ArrayList<>();
        y = new ArrayList<>();
        backgroundurl = new ArrayList<>();
        Filefullname = new ArrayList<>();






        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("story").child(userid);
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 imageurl.clear();
                 storyid.clear();
                 caption.clear();
                 type.clear();
                 x.clear();
                 y.clear();
                 Filefullname.clear();
                 backgroundurl.clear();



                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     Story story = snapshot.getValue(Story.class);
                     long timecurrent = System.currentTimeMillis();
                     assert story != null;
                     if (timecurrent > story.getTimestart() && timecurrent < story.getTimend()) {
                         imageurl.add(story.getImageurl());
                         storyid.add(story.getStoryId());
                         caption.add(story.getCaption());
                         type.add(story.getType());


                             size.add(story.getSize());
                             x.add(story.getX());
                             y.add(story.getY());
                             backgroundurl.add(story.getBackground());


                     }
                     String fullname = "";
                     if (story.getType().equals("image")) {
                         fullname = snapshot.getKey() + ".jpg";
                     } else {
                         fullname = snapshot.getKey() + ".mp4";
                     }

                     Filefullname.add(fullname);

                 }
                     getStory();
                     sCaption.setText(caption.get(counter));


             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


    }


    private void getStory(){
        int  size2 = type.size();
        storiesProgressView.setStoriesCount(size2);

        if (type.get(counter).equals("image")){
            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",Filefullname.get(counter));
            videoView.setVisibility(View.GONE);
            storiesProgressView.setStoryDuration(5000L);
            image.setVisibility(View.VISIBLE);
            if (file.exists()){

                Picasso.with(StoryActivity.this).load(file).into(image);

            }else{
                Picasso.with(StoryActivity.this).load(imageurl.get(counter)).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(image, new Callback() {
                            @Override
                            public void onSuccess() {

                                storiesProgressView.setStoryDuration(5000L);
                                storiesProgressView.setStoriesListener(StoryActivity.this);
                                storiesProgressView.startStories(counter);

                            }

                            @Override
                            public void onError() {

                                Picasso.with(StoryActivity.this).load(imageurl.get(counter)).into(image);

                            }
                        });
                /*Intent intent = new Intent(StoryActivity.this, Sviewer.class);
                intent.putExtra("url" , imageurl.get(counter));
                intent.putExtra("name" , storyid.get(counter));
                intent.putExtra("type" , type.get(counter));
                startService(intent);*/
            }
            storiesProgressView.setStoriesListener(StoryActivity.this);
            storiesProgressView.startStories(counter);
        }else{
            String fullname = storyid.get(counter)+".mp4";

            File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",fullname);
            if (file.exists()){

                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(StoryActivity.this, Uri.fromFile(file));
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (time!=null){
                        long timeInMillisec = Long.parseLong(time);
                        storiesProgressView.setStoryDuration(timeInMillisec);
                    }else{
                        storiesProgressView.setStoryDuration(5000L);
                    }
                    retriever.release();
                }catch (RuntimeException ex){

                    Toast.makeText(this, "error - " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }






                ++firstVideo;
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).into(image);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoPath(file.getPath());
                videoView.setScaleX(size.get(counter));
                videoView.setScaleY(size.get(counter));
                videoView.setY(y.get(counter));
                videoView.setX(x.get(counter));
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        videoView.start();
                    }
                });
            }else{
                String str = imageurl.get(counter);
                Uri uri = Uri.parse(str);

                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(str);

                story_prg.setVisibility(View.VISIBLE);

                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp"+fullname);
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



                final File localFile = new File(directory,fullname);

                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Picasso.with(StoryActivity.this).load(backgroundurl.get(counter)).into(image);
                        story_prg.setVisibility(View.GONE);

                        videoView.setVideoURI(Uri.fromFile(localFile));
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setScaleX(size.get(counter));
                        videoView.setScaleY(size.get(counter));
                        videoView.setY(y.get(counter));
                        videoView.setX(x.get(counter));
                        videoView.requestFocus();
                        videoView.start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                /*Intent intent = new Intent(StoryActivity.this, Sviewer.class);
                intent.putExtra("url" , imageurl.get(counter));
                intent.putExtra("name" , storyid.get(counter));
                intent.putExtra("type" , type.get(counter));
                startService(intent);*/
            }

        }


        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("story").child(userid);
                reference.child(storyid.get(counter)).child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mComList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Comments comments = snapshot.getValue(Comments.class);
                            if (comments!=null){
                                mComList.add(comments);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        },10);

    }

            private void userInfo(String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;

                Picasso.with(StoryActivity.this).load(users.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(story_photo, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(StoryActivity.this).load(users.getThumb_image()).placeholder(R.drawable.default_avatar).into(story_photo);

                    }
                });
                story_username.setText(users.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim,R.anim.zoom_out);
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

    public long getVideoTime(File file){

        long timeInMillisec = 0L;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(StoryActivity.this, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (time!=null){
             timeInMillisec = Long.parseLong(time);
        }
        retriever.release();
        return timeInMillisec;

    }
}
