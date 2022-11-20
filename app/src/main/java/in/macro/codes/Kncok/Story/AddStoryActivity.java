package in.macro.codes.Kncok.Story;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.net.URISyntaxException;

import in.macro.codes.Kncok.FirstMainActivity;
import in.macro.codes.Kncok.PathUtilvideo;
import in.macro.codes.Kncok.R;

public class AddStoryActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG =2 ;
    private StorageTask storageTask;
    StorageReference storageReference;
    private Uri mImageUri;
    String myUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);
        storageReference = FirebaseStorage.getInstance().getReference("story");
        //CropImage.activity().setAspectRatio(9,16).start(AddStoryActivity.this); CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  CropImage.ActivityResult result =CropImage.getActivityResult(data);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*"); //add video/* for video selection
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == RESULT_LOAD_IMG &&resultCode==RESULT_OK ){
            String filepath = null;

            assert data != null;
            mImageUri =data.getData();

            if (mImageUri.toString().contains("image")){
                try {
                    filepath = PathUtilvideo.getPath(AddStoryActivity.this,mImageUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(AddStoryActivity.this, EditStoryActivity.class);
                intent.putExtra("uri",mImageUri.toString());
                intent.putExtra("type","image");
                intent.putExtra("path",filepath);
                startActivity(intent);
                finish();
            }else if (mImageUri.toString().contains("video")){
                try {
                    filepath = PathUtilvideo.getPath(AddStoryActivity.this,mImageUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(AddStoryActivity.this, EditStoryActivity.class);
                intent.putExtra("uri",mImageUri.toString());
                intent.putExtra("type","video");
                intent.putExtra("path",filepath);
                startActivity(intent);
                finish();
            }



        }else{

            MDToast mdToast = MDToast.makeText(AddStoryActivity.this, "Something went wrong.",MDToast.LENGTH_LONG, MDToast.TYPE_ERROR);
            mdToast.show();
            startActivity(new Intent(AddStoryActivity.this, FirstMainActivity.class));
            finish();
        }
    }
}
