package in.macro.codes.Kncok.QRCode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.macro.codes.Kncok.FirstMainActivity;
import in.macro.codes.Kncok.R;

public class ScanQRActivity extends AppCompatActivity {
    SurfaceView surfaceView;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    TextView textView;
    String intentData = "";
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_q_r);
        surfaceView = findViewById(R.id.surfaceView);
        textView=(TextView) findViewById(R.id.txt);

    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanQRActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanQRActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                    cameraSource.start(holder);

                } catch ( IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });





        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            isEmail = false;
                            intentData = barcodes.valueAt(0).displayValue;


                            Intent intent = new Intent(ScanQRActivity.this, FirstMainActivity.class);
                            startActivity(intent);
                            addFriend(intentData);
                            finish();
                        }
                    });


                }
            }
        });
    }

    private void addFriend(String user_id){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Friends");
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("messages");

        final Map<String, Object> messageMap = new HashMap<String, Object>();
        messageMap.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                ,Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.child(user_id).child("followers").updateChildren(messageMap);


        final Map<String, Object> messageMap3 = new HashMap<String, Object>();
        messageMap3.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                ,Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        reference.child(user_id).child("following").updateChildren(messageMap3);


        final Map<String, Object> messageMap2 = new HashMap<String, Object>();
        messageMap2.put(user_id,user_id);
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("following")
                .updateChildren(messageMap2);

        final Map<String, Object> messageMap4 = new HashMap<String, Object>();
        messageMap4.put(user_id,user_id);
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("followers")
                .updateChildren(messageMap4);



        HashMap hashMap = new HashMap();
        hashMap.put(user_id,user_id);
        reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);

        HashMap hashMap2 = new HashMap();
        hashMap2.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference2.child(user_id).updateChildren(hashMap);


     sendFirstMsg(intentData);




    }

    private void sendFirstMsg(String mChatUser){

        String mCurrentUserId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;


        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUserId).child(mChatUser).push();

        String push_id = user_message_push.getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", "");
        messageMap.put("seen", false);
        messageMap.put("type", "special");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);
        messageMap.put("to", mChatUser);

        Map<String, Object> messageUserMap = new HashMap<String, Object>();
        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);



        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

        mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
        mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}