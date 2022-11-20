package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;


public class CoverActivity extends AppCompatActivity {
  private ImageView image_view;
    private DatabaseReference mUserDatabase,mGroup;
    private FirebaseUser mCurrentUser;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage;
    String data,gname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        image_view=(ImageView)findViewById(R.id.image_view);

        data = getIntent().getStringExtra("data");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mGroup = FirebaseDatabase.getInstance().getReference().child("Groups");

        mUserDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference();



        if (("groups").equals(data)){
            gname = getIntent().getStringExtra("gname");
            mGroup.child(gname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final String cover_image = dataSnapshot.child("gcover_image").getValue().toString();
                    Picasso.with(CoverActivity.this).load(cover_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_send_image).into(image_view, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(CoverActivity.this).load(cover_image).placeholder(R.drawable.default_send_image).into(image_view);

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String cover_image = dataSnapshot.child("cover_image").getValue().toString();


                    Picasso.with(CoverActivity.this).load(cover_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_send_image).into(image_view, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(CoverActivity.this).load(cover_image).placeholder(R.drawable.default_send_image).into(image_view);

                        }
                    });




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Background Task=new Background(CoverActivity.this);
              Task.execute("mcover_select");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(3,2 )
                    .setMinCropWindowSize(500, 500)
                    .start(this);



        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(CoverActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                if (data.equals("groups")){

                    File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));
                    Bitmap thumb_bitmap = new Compressor(this)
                            .compressToBitmap(thumb_filePath);
                    String current_user_id = mCurrentUser.getUid();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 45, baos);
                    final byte[] thumb_byte = baos.toByteArray();
                    final StorageReference filepath = mImageStorage.child("group_covers").child(current_user_id + ".jpg");
                    UploadTask uploadTask2 = filepath.putBytes(thumb_byte);

                    uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloaduri = (Uri) task.getResult();
                                        assert downloaduri != null;
                                        final String download_url = downloaduri.toString();
                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("gcover_image", download_url);

                                        mGroup.child(gname).updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(CoverActivity.this, "Cover Updated Successfully", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });


                }else{
                    String current_user_id = mCurrentUser.getUid();
                    final StorageReference filepath = mImageStorage.child("cover_images").child(current_user_id + ".jpg");
                    File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));
                    Bitmap thumb_bitmap = new Compressor(this)
                            .compressToBitmap(thumb_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 45, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    UploadTask uploadTask2 = filepath.putBytes(thumb_byte);

                    uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloaduri = (Uri) task.getResult();
                                        assert downloaduri != null;
                                        final String download_url = downloaduri.toString();
                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("cover_image", download_url);

                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(CoverActivity.this, "Cover Updated Successfully", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }


    }
    public static class Background extends AsyncTask<String,String,String> {

        WeakReference<CoverActivity> activityWeakReference;

        Background(CoverActivity activity){
            activityWeakReference = new  WeakReference<CoverActivity>(activity);
        }
        @Override
        protected String doInBackground(String... strings) {
            final CoverActivity activity=activityWeakReference.get();

            if (strings[0].equals("mcover_select")){

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.nothing,R.anim.zoom_out);
    }
}
