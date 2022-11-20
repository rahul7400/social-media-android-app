package in.macro.codes.Kncok.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class Sviewer extends Service {
    public Sviewer() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url  = intent.getStringExtra("url");
        String name = intent.getStringExtra("name");
        String type = intent.getStringExtra("type");
        assert type != null;
        DownloadFiles(url,name,type);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }



    private void DownloadFiles(String url,String name,String type){

                String fullname = "";
                if (type.equals("image")){
                    fullname = name+".jpg";
                }else{
                    fullname = name+".mp4";
                }


                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);


                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp");
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



                final File localFile = new File(directory,fullname);

                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


    }
}
