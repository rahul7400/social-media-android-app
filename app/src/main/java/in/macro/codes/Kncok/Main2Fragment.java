package in.macro.codes.Kncok;


import android.app.ActivityOptions;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.macro.codes.Kncok.Service.Sviewer;
import in.macro.codes.Kncok.Story.EditStoryActivity;
import in.macro.codes.Kncok.Story.Story;
import in.macro.codes.Kncok.Story.StoryAdapter;
import in.macro.codes.Kncok.TabsAnimation.DepthTransformation;




public class Main2Fragment extends Fragment {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DatabaseReference mUserRef;
    private TabLayout mTabLayout;
    public static final String MyPREFERENCES = "current_id" ;

    private static final String[] paths = {"Art","Business", "Buy & Sell", "Entertainment","Education","Gaming","Technology"};

    private CoordinatorLayout swipeElement;
    private String category;
    private AppBarLayout appBarLayout;
    private BottomNavigationView bottomNavigation;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private ArrayList<Story> storyList;
    private List<String> friendList;
    private MenuItem prevMenuItem;
    private TextView heading;
    private View mView;
    ArrayList<Users> usersList = new ArrayList<>();
    public Main2Fragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main2, container, false);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();


        mToolbar = (androidx.appcompat.widget.Toolbar)  mView.findViewById(R.id.main_page_toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbar);
        Objects.requireNonNull(  ((AppCompatActivity)getActivity()).getSupportActionBar()).setTitle("");


        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }else{

            Intent intent = new Intent(getContext(),StartActivity.class);
            startActivity(intent);
            getActivity().finish();
        }


        //Tabs
        mViewPager = (ViewPager)  mView.findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true,new DepthTransformation());
        bottomNavigation =  mView.findViewById(R.id.bottom_navigation);


        swipeElement = (CoordinatorLayout)  mView.findViewById(R.id.swipe_element);
        appBarLayout = (AppBarLayout)  mView.findViewById(R.id.appBarLayout);
        heading =(TextView)  mView.findViewById(R.id.heading);
        //  mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        //  mTabLayout.setupWithViewPager(mViewPager);
        recyclerView_story =(RecyclerView)  mView.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
       // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        CenterZoomLayoutManager centerZoomLayoutManager = new
                CenterZoomLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);


        recyclerView_story.setLayoutManager(centerZoomLayoutManager);
        storyList=new ArrayList<>();
        storyAdapter =  new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);

       /* AppBarLayout appBarLayout =(AppBarLayout)findViewById(R.id.appBarLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) appBarLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();*/





        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                appBarLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                        {//  Collapsed
                            mToolbar.setTitle("Knock Knock");
                        }
                        else
                        {//Expanded
                            mToolbar.setTitle("");

                        }
                    }
                });

            }
        });


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()) {
                    case R.id.navigation_chats:
                        mViewPager.setCurrentItem(0);
                        heading.setText("Messages");

                        break;


                    case R.id.navigation_groups:

                        mViewPager.setCurrentItem(1);
                        heading.setText("Groups");
                        break;


                    case R.id.navigation_notifications:
                        mViewPager.setCurrentItem(2);
                        heading.setText("Notifications");
                        break;


                    case R.id.navigation_settings:
                        mViewPager.setCurrentItem(3);
                        heading.setText("Settings");
                        break;



                }
                return false;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigation.getMenu().getItem(0).setChecked(false);

                bottomNavigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigation.getMenu().getItem(position);


                switch(prevMenuItem.getItemId()) {
                    case R.id.navigation_chats:
                        heading.setText("Messages");

                        break;


                    case R.id.navigation_groups:

                        heading.setText("Groups");
                        break;


                    case R.id.navigation_notifications:
                        heading.setText("Notifications");
                        break;


                    case R.id.navigation_settings:
                        heading.setText("Settings");
                        break;



                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


       /* for (int i = 0; i < mTabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(tab.getText());

                if (i == 0) {
                    tabTextView.setTextSize(15);
                    tabTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    tabTextView.setTextColor(Color.parseColor("#663399"));

                }else {
                    tabTextView.setTextSize(12);
                    tabTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    tabTextView.setTextColor(Color.parseColor("#ADC0BABA"));
                }

            }

        }*/
        /*mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                               @Override
                                               public void onTabSelected(TabLayout.Tab tab) {
                                                   ViewGroup vg = (ViewGroup) mTabLayout.getChildAt(0);
                                                   ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                                                   int tabChildsCount = vgTab.getChildCount();
                                                   for (int i = 0; i < tabChildsCount; i++) {
                                                       View tabViewChild = vgTab.getChildAt(i);
                                                       if (tabViewChild instanceof TextView) {
                                                           ((TextView) tabViewChild).setTextSize(15);
                                                           ((TextView) tabViewChild).setTypeface(Typeface.DEFAULT_BOLD);
                                                           ((TextView) tabViewChild).setTextColor(Color.parseColor("#663399"));
                                                       }
                                                   }
                                               }

                                               @Override
                                               public void onTabUnselected(TabLayout.Tab tab) {
                                                   ViewGroup vg = (ViewGroup) mTabLayout.getChildAt(0);
                                                   ViewGroup vgTab = (ViewGroup) vg.getChildAt(tab.getPosition());
                                                   int tabChildsCount = vgTab.getChildCount();
                                                   for (int i = 0; i < tabChildsCount; i++) {
                                                       View tabViewChild = vgTab.getChildAt(i);
                                                       if (tabViewChild instanceof TextView) {
                                                           ((TextView) tabViewChild).setTextSize(12);
                                                           ((TextView) tabViewChild).setTypeface(Typeface.DEFAULT_BOLD);
                                                           ((TextView) tabViewChild).setTextColor(Color.parseColor("#ADC0BABA"));

                                                       }
                                                   }
                                               }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/



        return mView;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().goOnline();
        if(currentUser == null){

            sendToStart();

        } else {

            mUserRef.child("online").setValue("true");
            readStory();
            currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()){
                        Log.d("TAG", "token=" + task.getResult().getToken());
                     //   MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "token - " + task.getResult().getToken(),MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                       // mdToast.show();
                    }else{
                        Log.e("TAG", "exception=" +task.getException().toString());
                        MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "exception - " +task.getException().toString(),MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                        mdToast.show();
                    }
                }
            });
        }




    }


    @Override
    public void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(Objects.requireNonNull(getContext()), StartActivity.class);
        startActivity(startIntent);
        Objects.requireNonNull(getActivity()).finish();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(Objects.requireNonNull(getContext()), SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(Objects.requireNonNull(getContext()), UsersActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId()==R.id.create_grp){
            createGroup();
        }

        if (item.getItemId() == R.id.app_settings_btn){


        }
        if (item.getItemId() == R.id.action_request){
            Intent settingsIntent = new Intent(Objects.requireNonNull(getContext()), FriendSearchActivity.class);
            ActivityOptions activityOptions =ActivityOptions.makeCustomAnimation(Objects.requireNonNull(getContext()),R.anim.profile_enter,R.anim.nothing);
            startActivity(settingsIntent,activityOptions.toBundle());
        }

        return true;
    }
    private void createGroup() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View viewInflated = LayoutInflater.from(Objects.requireNonNull(getContext())).inflate(R.layout.groupname_input,null );
        builder.setView(viewInflated);
        final EditText input = (EditText) viewInflated.findViewById(R.id.grp_name);
        final Button confirm =(Button)viewInflated.findViewById(R.id.grp_confirm);
        final Button dismiss =(Button)viewInflated.findViewById(R.id.grp_dismiss);
        final TextView error =(TextView)viewInflated.findViewById(R.id.gerror);



        Button art = (Button) viewInflated.findViewById(R.id.art2);
        Button buy = (Button) viewInflated.findViewById(R.id.buy2);
        Button entertainment = (Button) viewInflated.findViewById(R.id.entertainment2);
        Button technology = (Button) viewInflated.findViewById(R.id.technology2);
        Button business = (Button) viewInflated.findViewById(R.id.business2);
        Button education = (Button) viewInflated.findViewById(R.id.education2);
        Button gaming = (Button) viewInflated.findViewById(R.id.gaming2);
        final AlertDialog dialog = builder.create();
        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                art.setBackgroundResource(R.drawable.selected_bg);
                art.setTextColor(getResources().getColor(R.color.colorAccent));
                category= art.getText().toString();


            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy.setBackgroundResource(R.drawable.selected_bg);
                buy.setTextColor(getResources().getColor(R.color.colorAccent));
                category= buy.getText().toString();

            }
        });

        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entertainment.setBackgroundResource(R.drawable.selected_bg);
                entertainment.setTextColor(getResources().getColor(R.color.colorAccent));
                category= entertainment.getText().toString();
            }
        });
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                technology.setBackgroundResource(R.drawable.selected_bg);
                technology.setTextColor(getResources().getColor(R.color.colorAccent));
                category= technology.getText().toString();

            }
        });
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                business.setBackgroundResource(R.drawable.selected_bg);
                business.setTextColor(getResources().getColor(R.color.colorAccent));
                category= business.getText().toString();

            }
        });
        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                education.setBackgroundResource(R.drawable.selected_bg);
                education.setTextColor(getResources().getColor(R.color.colorAccent));
                category= education.getText().toString();

            }
        });

        gaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gaming.setBackgroundResource(R.drawable.selected_bg);
                gaming.setTextColor(getResources().getColor(R.color.colorAccent));
                category= gaming.getText().toString();

            }
        });





        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String grp_name =input.getText().toString();

                DatabaseReference grp_name_ck = FirebaseDatabase.getInstance()
                        .getReference("Groups");

                if (grp_name.equals("") ){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Enter a group name to continue");
                }else{
                    grp_name_ck.addListenerForSingleValueEvent(new ValueEventListener() {   //TODO Check group name existence
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(grp_name)){
                                error.setVisibility(View.VISIBLE);
                                error.setText("This name has already been taken");
                            }else {

                                upload_group(grp_name);
                                dialog.dismiss();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

















            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations;
        dialog.show();
    }

    private void readStory(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                friendList = new ArrayList<>();
                DatabaseReference friends = FirebaseDatabase.getInstance()
                        .getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("following");
                friends.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot :dataSnapshot.getChildren()){

                            if (!Objects.equals(snapshot.getKey(), FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                friendList.add(snapshot.getKey());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("story");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long timecurrent = System.currentTimeMillis();
                        storyList.clear();
                        storyList.add(new Story("",0,0,"",
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),"","",0f,0f,0f,""));

                        for(String id :friendList){
                            int countStory = 0;
                            Story story = null;
                            for(DataSnapshot snapshot:dataSnapshot.child(id).getChildren()){
                                story = snapshot.getValue(Story.class);
                                assert story != null;
                                if (timecurrent>story.getTimestart() && timecurrent<story.getTimend()){
                                    countStory++;
                                }

                                //Download Story service
                                String fullname ="";
                                if (story.getType().equals("image")){
                                    fullname = snapshot.getKey()+".jpg";
                                }else{
                                    fullname = snapshot.getKey()+".mp4";
                                }
                                File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp",fullname);

                                if (!file.exists()){

                               /* Intent intent = new Intent(activity.getContext(), Sviewer.class);
                                intent.putExtra("url" , story.getImageurl());
                                intent.putExtra("name" , snapshot.getKey());
                                intent.putExtra("type" , story.getType());
                                Objects.requireNonNull(activity.getActivity()).startService(intent);*/
                                }
                            }

                            if (countStory>0){
                                storyList.add(story);
                                Log.e("storySize", String.valueOf(storyList.size()));
                            }
                            storyAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        },4);

    }



    private void upload_group(final String grp_name) {


        StringBuilder sb = new StringBuilder();
        String ascString = null;
        long asciiInt;
        for (int i = 0; i < grp_name.length(); i++){
            sb.append((int) grp_name.charAt(i));
            char c = grp_name.charAt(i);
        }
        ascString = sb.toString();



        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(grp_name);


        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("gname", grp_name);
        userMap.put("gstatus", "Hello Guys! Using Knock Knock");
        userMap.put("gimage", "default");
        userMap.put("gthumb_image", "default");
        userMap.put("category", category);
        userMap.put("gid", ascString);
        userMap.put("grp_mode","user");
        userMap.put("gcover_image","default");
        userMap.put("owner", uid);
        mDatabase.setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MDToast mdToast = MDToast.makeText(Objects.requireNonNull(getContext()), "Your Awesome Group is created",MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                mdToast.show();

            }
        });



        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Groups").child(grp_name).child("Admin");
        HashMap<String, String> userMap2 = new HashMap<>();
        userMap2.put(uid, uid);
        mDatabase2.setValue(userMap2);

        DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Groups").child(grp_name).child("Users");
        HashMap<String, String> userMap3 = new HashMap<>();
        userMap3.put(uid, uid);
        mDatabase3.setValue(userMap3);

    }



    public static class Background extends AsyncTask<String,String,String>{
        private WeakReference<Main2Fragment> activityWeakReference;
        Background(Main2Fragment activity){
            activityWeakReference=new WeakReference<Main2Fragment>(activity);
        }
        @Override
        protected String doInBackground(String... strings) {




            return null;
        }
    }


}
