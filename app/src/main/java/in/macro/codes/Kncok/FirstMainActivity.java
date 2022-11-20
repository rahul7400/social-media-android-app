package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ZombieEventManager;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.Objects;

import in.macro.codes.Kncok.TabsAnimation.DepthTransformation;
import in.macro.codes.Kncok.TabsAnimation.HingeTransformation;
import in.macro.codes.Kncok.TabsAnimation.SlideUpTransformer;
import in.macro.codes.Kncok.TabsAnimation.ZoomOutTransformation;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class FirstMainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private PageAdapterMain mSectionsPagerAdapter;
    private String id;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "current_id" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_main);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        mViewPager = (ViewPager) findViewById(R.id.main_tabPager2);
        id = getIntent().getStringExtra("id");

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mSectionsPagerAdapter = new PageAdapterMain(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);
        //mViewPager.setCurrentItem(1);

        mViewPager.setPageTransformer(true,new ZoomOutTransformation());
//        if (mViewPager.getCurrentItem() == 0){
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }else{
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }

    }


    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }else{
            super.onBackPressed();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().goOnline();
        if(currentUser == null){
            Intent startIntent = new Intent(this, StartActivity.class);
            startActivity(startIntent);
            finish();

        } else {

            final DatabaseReference mUserDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
