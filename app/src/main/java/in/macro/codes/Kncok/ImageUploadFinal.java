package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.Story.StoryActivity;

public class ImageUploadFinal extends AppCompatActivity {

    String filepath2, mChatUser, mCurrentUserId,type;
    Uri mImageuri;
    EditText caption;
    FloatingActionButton send;
    ImageView imageView;
    CircleImageView crop;
    DatabaseReference mRootRef;
    StorageReference mImageStorage;
    Uri resultUri;
    final int CROP_PIC_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_final);

        type = getIntent().getStringExtra("type");
        if (type.equals("single"))
        {
            mImageuri = Uri.parse(getIntent().getStringExtra("uri"));
            filepath2 = getIntent().getStringExtra("filepath");
            mChatUser = getIntent().getStringExtra("user_id");
            mCurrentUserId = getIntent().getStringExtra("current_id");

        }else{

            Bundle bundle = getIntent().getExtras();
            assert bundle != null;
            List<Uri> imageUri = bundle.getParcelable("data");
            mChatUser = getIntent().getStringExtra("user_id");
            mCurrentUserId = getIntent().getStringExtra("current_id");
        }


        mImageStorage = FirebaseStorage.getInstance().getReference();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        caption = (EditText) findViewById(R.id.f_caption);
        send = (FloatingActionButton) findViewById(R.id.f_send);
        imageView = (ImageView) findViewById(R.id.f_image);
        crop = (CircleImageView) findViewById(R.id.f_crop);

        if(type.equals("single")){
            CropImage.activity(mImageuri)
                    .start(ImageUploadFinal.this);

            crop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity(mImageuri)
                            .start(ImageUploadFinal.this);

                }
            });

        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
                uploadImage();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                resultUri = result.getUri();
                Picasso.with(ImageUploadFinal.this).load(resultUri).placeholder(R.drawable.default_send_image).into(imageView);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "error - " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void uploadImage() {

        final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
        final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUserId).child(mChatUser).push();

        final String push_id = user_message_push.getKey();
        final String filename = push_id + ".jpg";
        final StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");


        String text = caption.getText().toString();

        final Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put("name", filename);
        messageMap.put("seen", false);
        messageMap.put("message", "default");
        messageMap.put("filepath", filepath2);
        messageMap.put("type", "image");
        messageMap.put("caption", text);
        messageMap.put("uri", resultUri.toString());
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);

        Map<String, Object> messageUserMap = new HashMap<String, Object>();
        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                assert resultUri != null;

                if (databaseError != null) {

                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                }

            }
        });
        assert resultUri != null;


        File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));
        Bitmap thumb_bitmap = new Compressor(this)
                .setMaxWidth(200)
                .setMaxHeight(200)
                .compressToBitmap(thumb_filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
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



