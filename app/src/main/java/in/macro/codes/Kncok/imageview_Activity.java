package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;


import pl.droidsonroids.gif.GifImageView;

public class imageview_Activity extends AppCompatActivity {
    private GifImageView Image_view;
    private GifImageView download_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_imageview);
        Image_view=(GifImageView)findViewById(R.id.image_viewer);
        download_img=(GifImageView)findViewById(R.id.download_img);
        final String filepath=  getIntent().getStringExtra("filepath");
       final String img_link=  getIntent().getStringExtra("view_img");
        final String name=   getIntent().getStringExtra("img_ID");


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        assert filepath != null;
        if (!filepath.equals("null")){


            File imgFile = new  File(filepath);
            if (imgFile.exists()){

                Glide.with(this)
                        .load(filepath)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Glide.with(imageview_Activity.this)
                                        .load(R.drawable.default_send_image).into(Image_view);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(Image_view);

                download_img.setVisibility(View.GONE);

            }else{

                assert name != null;
                File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Downloaded Images",name);
                if (file.exists()){
                    Glide.with(this)
                            .load(file)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Glide.with(imageview_Activity.this)
                                            .load(R.drawable.default_send_image).into(Image_view);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(Image_view);

                    download_img.setVisibility(View.GONE);
                }else{
                    Glide.with(this)
                            .load(img_link)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(Image_view);

                    download_img.setVisibility(View.VISIBLE);
                }

            }



        }else {
            if (name.equals("default")){

                Glide.with(this)
                        .asBitmap()
                        .load(img_link)
                        .into(Image_view);
                download_img.setVisibility(View.GONE);

            }else{
                checkFile(name,img_link);
            }


        }




       //Starting download Image service
       download_img.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Background Task=new Background(imageview_Activity.this);
               Task.execute();

           }
       });

    }

    public void checkFile(String name, final String link){
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Downloaded Images",name);
        if (file.exists()){


            Glide.with(this)
                    .load(file)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Glide.with(imageview_Activity.this)
                                    .load(R.drawable.default_send_image).into(Image_view);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(Image_view);

            download_img.setVisibility(View.GONE);


        }else
            {

                Glide.with(this)
                        .load(link)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Glide.with(imageview_Activity.this)
                                        .load(link).into(Image_view);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(Image_view);
                download_img.setVisibility(View.VISIBLE);
        }

    }




    private static class Background extends AsyncTask<String,String,String>{

        private WeakReference<imageview_Activity> activityWeakReference;

        Background(imageview_Activity activity){
            activityWeakReference=new WeakReference<imageview_Activity>(activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Downloaded Images");
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


            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
            final imageview_Activity activity=activityWeakReference.get();

            if (activity==null||activity.isFinishing()){
                return "";
            }

            final String name=  activity.getIntent().getStringExtra("img_ID");
            assert name != null;
            StorageReference ref=storageReference.child("message_images/").child(name);
            final File localFile = new File(directory,name);


            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ",";local tem file created  created " +localFile.toString());
                    Toast.makeText(activity, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                    activity.download_img.setVisibility(View.GONE);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ",";local tem file not created  created " +exception.toString());

                }
            });
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.nothing,R.anim.zoom_out);
    }
}
