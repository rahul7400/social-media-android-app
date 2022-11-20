package in.macro.codes.Kncok;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.Groups.GroupProfilePageAdapter;
import in.macro.codes.Kncok.Story.Story;
import in.macro.codes.Kncok.TabsAnimation.DepthTransformation;
import in.macro.codes.Kncok.TabsAnimation.HingeTransformation;
import in.macro.codes.Kncok.TabsAnimation.SlideUpTransformer;
import in.macro.codes.Kncok.TabsAnimation.ZoomOutTransformation;
import io.grpc.Server;
import tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi;


public class ProfileActivity extends AppCompatActivity{


  private String user_id;
  private DatabaseReference mRootRef;
  
 
  private ProgressDialog mProgressDialog;
 
  private FirebaseUser mCurrent_user;
  private Button mProfileSendReqBtn, mDeclineBtn;
  private String mCurrent_state;
  private TextView mProfileStatus,mEmail;
  private ImageView mProfileImage,mCoverImage;
  private TextView mProfileName;
  private DatabaseReference mUsersDatabase;
  private DatabaseReference mFriendReqDatabase;
  private DatabaseReference mFriendDatabase;
  private DatabaseReference mNotificationDatabase;
  private RecyclerView mPostView;
  private List<String> mPostId = new ArrayList<>();
  private PostAdapter mAdapter;
  private View mView;
  String imageProfile;
  



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_profile);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

      getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
              WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    user_id = getIntent().getStringExtra("user_id");
    mRootRef = FirebaseDatabase.getInstance().getReference();
    imageProfile = getIntent().getStringExtra("profile");

    mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
    mDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);
    mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
    mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
    mCurrent_state = "not_friends";
    mRootRef = FirebaseDatabase.getInstance().getReference();
    mDeclineBtn.setVisibility(View.INVISIBLE);
    mDeclineBtn.setEnabled(false);
    mProgressDialog = new ProgressDialog(this);
    mProgressDialog.setTitle("Loading User Data");
    mProgressDialog.setMessage("Please wait while we load the user data.");
    mProgressDialog.setCanceledOnTouchOutside(false);
    mProgressDialog.show();
    mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
    mView = (View) findViewById(R.id.viewRa);

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    mPostView = (RecyclerView) findViewById(R.id.post);
    mAdapter = new PostAdapter(user_id, mPostId, ProfileActivity.this);
    mPostView.setHasFixedSize(true);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    gridLayoutManager.setReverseLayout(true);
    mPostView.setLayoutManager(gridLayoutManager);
    mPostView.setAdapter(mAdapter);



    mCoverImage = (ImageView) findViewById(R.id.cover_photo);
    mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
    mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
    mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
    mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
    mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

    mProfileImage = (ImageView) findViewById(R.id.profile_image10);
    mProfileName = (TextView) findViewById(R.id.profile_displayName);


    mProfileStatus = (TextView) findViewById(R.id.profile_status);
    mEmail = (TextView) findViewById(R.id.profile_email);
    mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


    mUsersDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {

        final String display_name = dataSnapshot.child("name").getValue().toString();
        String status = dataSnapshot.child("status").getValue().toString();
        final String image = dataSnapshot.child("thumb_image").getValue().toString();
        final String image2 = dataSnapshot.child("image").getValue().toString();
        String email = dataSnapshot.child("email").getValue().toString();
        final String cover_image = dataSnapshot.child("cover_image").getValue().toString();
        mProfileName.setText(display_name);
        mProfileStatus.setText(status);
        mEmail.setText(email);
        enableFollowFeature();



        mProfileImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(ProfileActivity.this, imageview_Activity.class);
            intent.putExtra("view_img", image2);

            intent.putExtra("img_ID", "default");
            intent.putExtra("filepath", "null");
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_in, R.anim.nothing);
          }
        });


        mCoverImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(ProfileActivity.this, imageview_Activity.class);
            intent.putExtra("view_img", cover_image);
            intent.putExtra("img_ID", "default");
            intent.putExtra("filepath", "null");
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_in, R.anim.nothing);
          }
        });

        if (!image.equals("default")) {

          //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

          Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                  .placeholder(R.drawable.default_avatar).into(mProfileImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

              Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

            }
          });


        }

        if (!cover_image.equals("default")) {
          Picasso.with(ProfileActivity.this).load(cover_image).networkPolicy(NetworkPolicy.OFFLINE)
                  .placeholder(R.drawable.default_send_image).into(mCoverImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

              Picasso.with(ProfileActivity.this).load(cover_image).placeholder(R.drawable.default_send_image).into(mCoverImage);

            }
          });

        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }

    });

    if (mCurrent_user.getUid().equals(user_id)) {

      mDeclineBtn.setEnabled(false);
      mDeclineBtn.setVisibility(View.INVISIBLE);

      mProfileSendReqBtn.setEnabled(false);
      mProfileSendReqBtn.setVisibility(View.INVISIBLE);
    }
    //--------------- FRIENDS LIST / REQUEST FEATURE -----



  }


  public void enableFollowFeature(){
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
    reference.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("following").hasChild(user_id)){
          mProfileSendReqBtn.setText("Unfollow");
          mCurrent_state = "unfollow";


        }
        else if (!dataSnapshot.child("following").hasChild(user_id) && dataSnapshot.child("followers").hasChild(user_id)){
          mProfileSendReqBtn.setText("Follow Back");
          mCurrent_state = "follow back";
        }
        else if (!dataSnapshot.child("following").hasChild(user_id) && !dataSnapshot.child("followers").hasChild(user_id)){
          mProfileSendReqBtn.setText("Follow");
          mCurrent_state = "start follow";
        }

        mProgressDialog.dismiss();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        mProfileSendReqBtn.setEnabled(false);

        if (mCurrent_state.equals("start follow") || mCurrent_state.equals("follow back")) {

          final Map<String, Object> messageMap = new HashMap<String, Object>();
          messageMap.put(mCurrent_user.getUid().toString(),mCurrent_user.getUid().toString());
          reference.child(user_id).child("followers").updateChildren(messageMap);


          final Map<String, Object> messageMap2 = new HashMap<String, Object>();
          messageMap2.put(user_id,user_id);
          reference.child(mCurrent_user.getUid()).child("following").updateChildren(messageMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              mProfileSendReqBtn.setEnabled(true);
            }
          });


          if (mCurrent_state.equals("start follow")){
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Notify");
            String push_id = reference1.push().getKey();
            HashMap hashMap = new HashMap();
            hashMap.put("follower", mCurrent_user.getUid());
            hashMap.put("type","follower");
            hashMap.put("timestamp", ServerValue.TIMESTAMP);
            reference1.child(user_id).child(push_id).updateChildren(hashMap);
          }




          mCurrent_state = "unfollow";
          mProfileSendReqBtn.setText("Unfollow");


        }else if (mCurrent_state.equals("unfollow")) {
          reference.child(user_id).child("followers").child(mCurrent_user.getUid().toString()).removeValue();
          reference.child(mCurrent_user.getUid()).child("following").child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              mProfileSendReqBtn.setEnabled(true);
            }
          });

          mCurrent_state = "start follow";
          mProfileSendReqBtn.setText("Follow");


        }
      }
    });

  }




  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.nothing,R.anim.profile_exit);
  }

  private void getPostData() {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(user_id);
    mPostId.clear();
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          mPostId.add(snapshot.getKey());
          mAdapter.notifyDataSetChanged();
        }
        if (mPostId.size() < 1) {
          mView.setVisibility(View.GONE);
        } else {
          mView.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }

}
