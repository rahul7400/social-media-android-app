package in.macro.codes.Kncok.Groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.lang.ref.WeakReference;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.CoverActivity;
import in.macro.codes.Kncok.CurrentProfileActivity;
import in.macro.codes.Kncok.R;

import in.macro.codes.Kncok.TabsAnimation.ZoomOutTransformation;
import in.macro.codes.Kncok.imageview_Activity;

public class GroupProfileActivity extends AppCompatActivity {
    private ImageView gcover_image;
    private TextView group_name, grules;
    private CircleImageView gprofile_image;
    private String gname2;
    private DatabaseReference mGroupData;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private String mCurrent_user;
    private String  grp_profile,grp_covers;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private Button grp_join;
    private ViewPager mViewPager;
    private GroupProfilePageAdapter mSectionsPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        gname2 = getIntent().getStringExtra("gname");
        group_name = (TextView) findViewById(R.id.grp_name);
        group_name.setText(gname2);
        mViewPager = (ViewPager) findViewById(R.id.gProfile_tabPager2);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSectionsPagerAdapter = new GroupProfilePageAdapter(getSupportFragmentManager(),gname2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPageTransformer(true,new ZoomOutTransformation());
        mGroupData = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname2);
        gcover_image = (ImageView) findViewById(R.id.gcover_photo);
        gprofile_image=(CircleImageView) findViewById(R.id.gprofile_image);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mGroupData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gimage = dataSnapshot.child("gimage").getValue().toString();
                String gcover = dataSnapshot.child("gcover_image").getValue().toString();
                if (!gimage.equals("default")){
                    Picasso.with(GroupProfileActivity.this).load(gimage).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_send_image).into( gprofile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with( GroupProfileActivity.this).load(gimage).placeholder(R.drawable.default_avatar).into(gprofile_image);

                        }
                    });
                }

                if (!gcover.equals("default")){
                    Picasso.with(GroupProfileActivity.this).load(gcover).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_send_image).into( gcover_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with( GroupProfileActivity.this).load(gcover).placeholder(R.drawable.default_avatar).into(gcover_image);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mGroupData.child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){

                    gprofile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Background Task=new Background(GroupProfileActivity.this);
                            Task.execute("mprofile_select");
                        }
                    });

                    gcover_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(GroupProfileActivity.this, GCover_Activity.class);
                            intent.putExtra("gname",gname2);
                            intent.putExtra("data","groups");
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_in,R.anim.nothing);
                        }
                    });
                }else{

                    gprofile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(GroupProfileActivity.this,imageview_Activity.class);
                            intent.putExtra("view_img",grp_profile);
                            intent.putExtra("img_ID","default");
                            intent.putExtra("filepath","null");
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_in,R.anim.nothing);
                        }
                    });

                    gcover_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(GroupProfileActivity.this, GCover_Activity.class);
                            intent.putExtra("gname",gname2);
                            intent.putExtra("img_ID","default");
                            intent.putExtra("filepath","null");
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_in,R.anim.nothing);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));

                Picasso.with(GroupProfileActivity.this).load(thumb_filePath).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_send_image).into( gprofile_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with( GroupProfileActivity.this).load(thumb_filePath).placeholder(R.drawable.default_avatar).into(gprofile_image);

                    }
                });

                String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final StorageReference filepath = mImageStorage.child("group_profiles").child( gname2 + ".jpg");

                final Uri imageUri = data.getData();

                assert imageUri != null;
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
                                String download_url = task.getResult().toString();

                                DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Groups")
                                        .child(gname2);
                                updateData.child("gimage").setValue(download_url);



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





            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }


    }

    private static class Background extends AsyncTask<String, String, String> {

        private WeakReference<GroupProfileActivity> activityWeakReference;
        Background(GroupProfileActivity activity){
            activityWeakReference =new WeakReference<GroupProfileActivity>(activity);
        }
        @Override
        protected String doInBackground(String... urls) {

            final GroupProfileActivity activity=activityWeakReference.get();
            if (urls[0].equals("mprofile_select")){

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing,R.anim.profile_exit);
    }
}
