package in.macro.codes.Kncok.Groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.ChatActivity;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.RealPathUtils;

public class GCover_Activity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage;
    private ImageView image_view;
    InputStream inputStream;
    private String gname;

    String fileRealPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcover_);
        image_view=(ImageView)findViewById(R.id.gimage_view);
         gname = getIntent().getStringExtra("gname");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        assert gname != null;
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname);
        mUserDatabase.keepSynced(true);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String cover_image = Objects.requireNonNull(dataSnapshot.child("gcover_image").getValue()).toString();


                Picasso.with(GCover_Activity.this).load(cover_image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_send_image).into(image_view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(GCover_Activity.this).load(cover_image).placeholder(R.drawable.default_send_image).into(image_view);

                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Background Task=new Background(GCover_Activity.this);
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

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {



                final Uri imageUri = result.getUri();
                final StorageReference filepath = mImageStorage.child("group_covers").child( gname + ".jpg");
                assert imageUri != null;

                Bitmap bmp = null;
              /*  if (Build.VERSION.SDK_INT < 11)
                    fileRealPath = RealPathUtils.getRealPathFromURI_BelowAPI11(GCover_Activity.this, imageUri);

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    fileRealPath = RealPathUtils.getRealPathFromURI_API11to18(GCover_Activity.this, imageUri);

                    // SDK > 19 (Android 4.4)
                else {
                    String id=null;
                    try {
                        id = DocumentsContract.getDocumentId(imageUri);
                        inputStream  = getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    File file = new File(getCacheDir().getAbsolutePath()+"/"+id);


                    fileRealPath = file.getAbsolutePath();


                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(fileRealPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/


                    File thumb_filePath = new File(Objects.requireNonNull(imageUri.getPath()));

                Picasso.with(GCover_Activity.this).load(thumb_filePath).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_send_image).into(image_view, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(GCover_Activity.this).load(thumb_filePath).placeholder(R.drawable.default_send_image).into(image_view);

                    }
                });

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
                                    String download_url = Objects.requireNonNull(task.getResult()).toString();

                                    DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Groups")
                                            .child(gname);
                                    updateData.child("gcover_image").setValue(download_url);



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


}

    public static class Background extends AsyncTask<String,String,String> {

        WeakReference<GCover_Activity> activityWeakReference;

        Background(GCover_Activity activity){
            activityWeakReference = new  WeakReference<GCover_Activity>(activity);
        }
        @Override
        protected String doInBackground(String... strings) {
            final GCover_Activity activity=activityWeakReference.get();

            if (strings[0].equals("mcover_select")){

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }

            return null;
        }
    }
}

