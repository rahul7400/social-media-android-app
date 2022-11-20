package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class CurrentProfileActivity extends AppCompatActivity {

    private TextView uname,ustatus;
    private CircleImageView uprofile;
    private ImageView ucover;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    String profile;
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_profile);



        mImageStorage = FirebaseStorage.getInstance().getReference();
        uname = (TextView) findViewById(R.id.p_name);
        ustatus = (TextView) findViewById(R.id.p_status);
        uprofile = (CircleImageView) findViewById(R.id.p_profile);
        ucover=(ImageView) findViewById(R.id.p_cover);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                String cover = dataSnapshot.child("cover_image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                uname.setText(name);
                ustatus.setText(status);

                ustatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CurrentProfileActivity.this,StatusActivity.class);
                        intent.putExtra("status_value",status);
                        startActivity(intent);
                    }
                });

                Picasso.with(CurrentProfileActivity.this).load(cover).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_send_image).into( ucover, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with( CurrentProfileActivity.this).load(cover).placeholder(R.drawable.default_send_image).into(ucover);

                    }
                });

                Picasso.with(CurrentProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into( uprofile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with( CurrentProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(uprofile);

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





   ucover.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent intent =new Intent(CurrentProfileActivity.this,CoverActivity.class);
           intent.putExtra("data","other");
           startActivity(intent);
           overridePendingTransition(R.anim.zoom_in,R.anim.nothing);
       }
   });

   uprofile.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent galleryIntent = new Intent();
           galleryIntent.setType("image/*");
           galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
           startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
       }
   });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(CurrentProfileActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();


                Bitmap thumb_bitmap =new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(30)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                final byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");



                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final String download_url = task.getResult().toString();
                                    final UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);



                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> thumb_task) {


                                            thumb_filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    final String thumb_downloadUrl = task.getResult().toString();
                                                    if(thumb_task.isSuccessful()){

                                                        Map update_hashMap = new HashMap();
                                                        update_hashMap.put("image", download_url);
                                                        update_hashMap.put("thumb_image", thumb_downloadUrl);

                                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    mProgressDialog.dismiss();
                                                                    MDToast mdToast = MDToast.makeText(CurrentProfileActivity.this, "Profile photo updated",MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                                                    mdToast.show();


                                                                }

                                                            }
                                                        });


                                                    } else {

                                                        MDToast mdToast = MDToast.makeText(CurrentProfileActivity.this, "Error in updating profile",MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                                        mdToast.show();
                                                        mProgressDialog.dismiss();

                                                    }
                                                }
                                            });





                                        }
                                    });
                                }
                            });







                        } else {

                            MDToast mdToast = MDToast.makeText(CurrentProfileActivity.this, "Error in updating profile",MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                            mProgressDialog.dismiss();

                        }

                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }


    }
}
