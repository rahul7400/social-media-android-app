package in.macro.codes.Kncok.SharePost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.ImageUploadFinal;
import in.macro.codes.Kncok.R;

public class EditPost extends AppCompatActivity {

    String uri,mCurrentUser;
    ImageView imageView,close_gallery;
    EditText caption;

    String mCurrentUserId;
    StorageReference mImageStorage;
    TextView next;
    ProgressBar progressBar;
    Uri moreUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        uri = getIntent().getStringExtra("uri");
        mCurrentUser = getIntent().getStringExtra("user_id");
        imageView = (ImageView) findViewById(R.id.camImage);
        caption = (EditText) findViewById(R.id.caption);
        close_gallery = findViewById(R.id.gallery_close);
        next = (TextView) findViewById(R.id.gallery_next);
        progressBar = (ProgressBar) findViewById(R.id.galleryProgress);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CropImage.activity(Uri.parse(uri))
                .setAspectRatio(4,5)
                .start(EditPost.this);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (moreUri != null){
                    uploadImage(moreUri);
                    finish();
                }

            }
        });

        close_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                moreUri = resultUri;
                Glide.with(this).load(resultUri).into(imageView);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "error - " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadImage(Uri latestUri) {

        final String current_user_ref = "posts/" ;


        DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("posts").push();

        final String push_id = user_message_push.getKey();
        final String filename = push_id + ".jpg";
        final StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");


        String text = caption.getText().toString();

        final Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put("name", filename);
        messageMap.put("seen", false);
        messageMap.put("message", "default");
        messageMap.put("type", "image");
        messageMap.put("push_id", push_id);
        messageMap.put("caption", text);
        messageMap.put("time",ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);

        Map<String, Object> messageUserMap = new HashMap<String, Object>();
        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);



        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {



                if (databaseError != null) {

                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                }

            }
        });



        File thumb_filePath = new File(Objects.requireNonNull(latestUri.getPath()));
        Bitmap thumb_bitmap = new Compressor(this)
                .setMaxWidth(300)
                .setMaxHeight(300)
                .compressToBitmap(thumb_filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        final byte[] thumb_byte = baos.toByteArray();

        UploadTask uploadTask2 = filepath.putBytes(thumb_byte);


        uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            String download_url = Objects.requireNonNull(task.getResult()).toString();
                            assert push_id != null;
                            DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("posts").child(push_id);
                            updateData.child("message").setValue(download_url);



                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress2 = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

            }
        });


    }

}