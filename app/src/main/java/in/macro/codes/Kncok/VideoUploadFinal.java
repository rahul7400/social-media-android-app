package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class VideoUploadFinal extends AppCompatActivity  {

    VideoView videoView;
    FloatingActionButton send;
    Uri videoUri;
    String fileRealPath,mCurrentUserId,mChatUser;
    DatabaseReference mRootRef;
    StorageReference mImageStorage;

    File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_final);


        mChatUser = getIntent().getStringExtra("user_id");
        mCurrentUserId = getIntent().getStringExtra("current_id");
        String uri = getIntent().getStringExtra("uri");
        videoUri = Uri.parse(uri);
        fileRealPath = getIntent().getStringExtra("path");

        videoView = (VideoView) findViewById(R.id.f_video);
        send = (FloatingActionButton) findViewById(R.id.f_vsend);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mRootRef = FirebaseDatabase.getInstance().getReference();



          /*  directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp/");
            if (!directory.exists()){
                if (directory.mkdirs())
                {

                    Log.i("CreateDir","App dir created");
                }
                else
                {
                    Log.w("CreateDir","Unable to create app dir!");
                }


         }*/


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoUri == null){
                    Toast.makeText(VideoUploadFinal.this, "Hold it for a sec", Toast.LENGTH_SHORT).show();
                }else {
                    finish();
                    uploadVideo();
                }

            }
        });

    }

    private  void uploadVideo(){

        final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
        final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUserId).child(mChatUser).push();

        final String push_id = user_message_push.getKey();
        final String filename=push_id+".mp4";
        final StorageReference filepath = mImageStorage.child("message_videos").child( filename);


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
        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

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
