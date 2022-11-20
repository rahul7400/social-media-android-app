package in.macro.codes.Kncok.QRCode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import in.macro.codes.Kncok.R;

public class qrcodeMainActivity extends AppCompatActivity {
    private static final String TAG = "as";
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    Button scan,share;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_main);

        image = (ImageView) findViewById(R.id.image);
        scan = (Button) findViewById(R.id.scan);
        share = (Button) findViewById(R.id.saveQR);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(qrcodeMainActivity.this,ScanQRActivity.class);
                startActivity(intent);
            }
        });


        qrgEncoder = new QRGEncoder(
                FirebaseAuth.getInstance().getCurrentUser().getUid(), null,
                QRGContents.Type.TEXT,
                800);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
    }


}