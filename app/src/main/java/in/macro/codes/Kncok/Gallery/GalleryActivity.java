package in.macro.codes.Kncok.Gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.R;

public class GalleryActivity extends AppCompatActivity {
    TextView nextScreen;
    ProgressBar progressBar;
    GridView gridView;
    ImageView camImage,close_gallery;
    ProgressDialog progressDialog;
    VideoView videoView;
    RelativeLayout rl;
    Spinner dirSpinner;
    String mCurrentUserId;
    StorageReference mImageStorage;
    private static final int NUM_GRID_COLUMNS = 4;
    String mPath;
    ArrayList<String> listPaths,dirPaths,directoryNames;
    GestureDetector gestureDetector;
    final String append = "file:/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        gridView = findViewById(R.id.galleryGrid);
        nextScreen = findViewById(R.id.gallery_next);
        progressBar = findViewById(R.id.galleryProgress);
        //progressBar.setVisibility(View.GONE);
        camImage = findViewById(R.id.camImage);
        videoView = findViewById(R.id.videoView);
        close_gallery = findViewById(R.id.gallery_close);
        dirSpinner = findViewById(R.id.spinnerGallery);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        rl = findViewById(R.id.gallery_rl);

        setupSpinner();


        //closing gallery
        close_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //moving to next screen
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }



    private void setupSpinner(){

        dirPaths = DirectoryScanner.getFileDirectories();
        directoryNames = DirectoryScanner.getDirectoryNames();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,directoryNames);
        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dirSpinner.setAdapter(arrayAdapter);

        dirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(listPaths !=null){ listPaths.clear();}

                //setting up gridView for selected directory
                setupGridView(dirPaths.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    private void setupGridView(final String selectedDirectory){

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        try {
            listPaths = new MediaFilesScanner(progressBar).execute(selectedDirectory).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        GridViewAdapter adapter = new GridViewAdapter(Objects.requireNonNull(this),R.layout.layout_grid_image_view,append, listPaths);
        gridView.setAdapter(adapter);

        if(listPaths !=null) {

            final int  position = 0;
            mPath = listPaths.get(position);
            if(MediaFilesScanner.isVideo(mPath)) { playVideo(mPath); }
            else { displayImage(mPath); }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPath = listPaths.get(position);
                if(MediaFilesScanner.isVideo(mPath)) { playVideo(mPath); }
                else { displayImage(mPath); }
            }
        });
    }




    private void playVideo(String path){

        camImage.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(path);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
    }


    private  void displayImage(String path){

        videoView.setVisibility(View.GONE);
        camImage.setVisibility(View.VISIBLE);
        //GlideImageLoader.loadImageWithTransition(getContext(), path,camImage);
        UniversalImageLoader.setImage(path, camImage, progressBar, append);
    }







}