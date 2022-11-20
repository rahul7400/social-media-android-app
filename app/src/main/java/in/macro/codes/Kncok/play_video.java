package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khizar1556.mkvideoplayer.MKPlayer;


public class play_video extends AppCompatActivity {

    private DatabaseReference mUsersDatabase;
    MKPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        final String path =getIntent().getStringExtra("path");
        final String url =getIntent().getStringExtra("video_url");
        String uid =getIntent().getStringExtra("uid");
        assert uid != null;
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


        if (url == null){
            player = new MKPlayer(play_video.this);
            player.play(path);

            player.setPlayerCallbacks(new MKPlayer.playerCallbacks() {
                @Override
                public void onNextClick() {
                    //It is the method for next song.It is called when you pressed the next icon
                    //Do according to your requirement
                }
                @Override
                public void onPreviousClick() {
                    //It is the method for previous song.It is called when you pressed the previous icon
                    //Do according to your requirement
                }
            });
        }else{
            MKPlayer player = new MKPlayer(play_video.this);
            player.play(url);

            player.setPlayerCallbacks(new MKPlayer.playerCallbacks() {
                @Override
                public void onNextClick() {
                    //It is the method for next song.It is called when you pressed the next icon
                    //Do according to your requirement
                }
                @Override
                public void onPreviousClick() {
                    //It is the method for previous song.It is called when you pressed the previous icon
                    //Do according to your requirement
                }
            });
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
