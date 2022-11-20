package in.macro.codes.Kncok.Story;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.ToDoItem;
import in.macro.codes.Kncok.Users;


public class AddStoryFinal extends AppCompatActivity {
    private StorageTask storageTask;
    StorageReference storageReference,storageReference2;
    private Uri mImageUri,bg;
    DatabaseReference mUser;
    private final List<Users> userName = new ArrayList<>();
    private String path,videopath,path2;
    private TextView try_cap;
    private EditText caption;
    private CircleImageView mProfile,guser_single_image2;
    String myUrl="",type,filepath,myUrl2="";
    Button globalShare;
    Boolean isClicked =true;
    Boolean isClicked2 =true;
    Boolean IsClicked3 = true;

    private RecyclerView mConvList;
    private ImageView preview;
    private Button next,globalShare2,postSelect;
    private float mScaleFactor = 1.0f,x,y;
    private  StoryFinalAdapter mAdapter;

    private List<String> AllHashtags = new ArrayList<>();



    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;" +
            "AccountName=knockstore;" +
            "AccountKey=qXFg0eguyaDo06vxthfv/BZfkTXmjC1IGQftMj+nYJpSbRKvHgyCG3idORG4SCUh/oy5rZA1qfAWy9TZU+PSAQ==;" +
            "EndpointSuffix=core.windows.net";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story_final);
        preview =(ImageView) findViewById(R.id.preview);

        caption = (EditText) findViewById(R.id.caption);
        path= getIntent().getStringExtra("path");
        type= getIntent().getStringExtra("type");

        System.setProperty("javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        if (type.equals("image")){
            mImageUri = Uri.parse(getIntent().getStringExtra("uri"));
        }else{
            bg = Uri.parse(getIntent().getStringExtra("uri"));
            videopath = getIntent().getStringExtra("videopath");
            mImageUri = Uri.parse(getIntent().getStringExtra("video"));
            path2= getIntent().getStringExtra("path2");

        }

        Bundle b = getIntent().getExtras();
        assert b != null;
        mScaleFactor = b.getFloat("size");
        x = b.getFloat("x");
        y = b.getFloat("y");


        globalShare =(Button) findViewById(R.id.globaleShare);
        globalShare2 = (Button) findViewById(R.id.globaleShare2);
        postSelect = (Button) findViewById(R.id.pglobaleShare);
        next = (Button) findViewById(R.id.story_next);
        mProfile =(CircleImageView) findViewById(R.id.profile_story);
        try_cap = (TextView) findViewById(R.id.try_cap);
        guser_single_image2 = (CircleImageView) findViewById(R.id.guser_single_image2);
        storageReference = FirebaseStorage.getInstance().getReference("story");


        mConvList=(RecyclerView) findViewById(R.id.story_friends);
        mAdapter = new StoryFinalAdapter(AddStoryFinal.this,userName);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);
        mConvList.setAdapter(mAdapter);


        postSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsClicked3){
                    IsClicked3 = false ;
                    postSelect.setText("Select");
                    postSelect.setBackgroundResource(R.drawable.deselect);
                    postSelect.setTextColor(Color.parseColor("#808080"));
                }else{
                    IsClicked3 = true ;
                    postSelect.setText("Selected");
                    postSelect.setBackgroundResource(R.drawable.oround);
                    postSelect.setTextColor(Color.parseColor("#ff5521"));
                }
            }
        });
        globalShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked){
                    isClicked = false ;
                    globalShare.setText("Select");
                    globalShare.setBackgroundResource(R.drawable.deselect);
                    globalShare.setTextColor(Color.parseColor("#808080"));
                }else{
                    isClicked = true ;
                    globalShare.setText("Selected");
                    globalShare.setBackgroundResource(R.drawable.oround);
                    globalShare.setTextColor(Color.parseColor("#ff5521"));
                }
            }
        });


        globalShare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked2){
                    isClicked2 = false ;
                    globalShare2.setText("Select");
                    globalShare2.setBackgroundResource(R.drawable.deselect);
                    globalShare2.setTextColor(Color.parseColor("#808080"));
                }else{
                    isClicked2 = true ;
                    globalShare2.setText("Selected");
                    globalShare2.setBackgroundResource(R.drawable.oround);
                    globalShare2.setTextColor(Color.parseColor("#ff5521"));
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Background task = new Background(AddStoryFinal.this);
                task.execute();
            }
        },10);





        caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputText = caption.getText().toString();
                try_cap.setText(inputText);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
         mUser.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String profile = Objects.requireNonNull(dataSnapshot.child("thumb_image").getValue()).toString();
                 Picasso.with(AddStoryFinal.this).load(profile).networkPolicy(NetworkPolicy.OFFLINE)
                         .placeholder(R.drawable.default_avatar).into(mProfile, new Callback() {
                     @Override
                     public void onSuccess() {

                     }

                     @Override
                     public void onError() {
                         Picasso.with(AddStoryFinal.this).load(profile).placeholder(R.drawable.default_avatar).into(mProfile);

                     }
                 });

                 Picasso.with(AddStoryFinal.this).load(profile).networkPolicy(NetworkPolicy.OFFLINE)
                         .placeholder(R.drawable.default_avatar).into(guser_single_image2, new Callback() {
                     @Override
                     public void onSuccess() {

                     }

                     @Override
                     public void onError() {
                         Picasso.with(AddStoryFinal.this).load(profile).placeholder(R.drawable.default_avatar).into(guser_single_image2);

                     }
                 });


                 Glide.with(getApplicationContext()).load(mImageUri).into(preview);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Handler handler = new Handler();
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         publishStory();
                     }
                 },20);
             }
         });

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void publishStory(){
       // final ProgressDialog pd= new ProgressDialog(this);
       // pd.setMessage("Posting");
        //pd.show();

        if (mImageUri!=null){


                if (type.equals("image")){

                    uploadImage();
                    finish();

                }else if (type.equals("video")){
                    uploadVideo();
                    finish();
                }

        }else{
            MDToast mdToast = MDToast.makeText(this,"Nothing Selected",MDToast.TYPE_WARNING);
            mdToast.show();
        }
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) AddStoryFinal.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            hideKeyboard(AddStoryFinal.this);
        } else {
            super.onBackPressed();
        }
    }

    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    public static class Background extends AsyncTask<String,String,String> {
        private WeakReference<AddStoryFinal> activityWeakReference;
        Background(AddStoryFinal activity){
            activityWeakReference=new WeakReference<AddStoryFinal>(activity);
        }
        @Override
        protected String doInBackground(String... strings) {
            final AddStoryFinal activity=activityWeakReference.get();
            DatabaseReference mFriends = FirebaseDatabase.getInstance().getReference().child("Friends")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

            mFriends.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                        mUsers.child(Objects.requireNonNull(snapshot.getKey())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Users users =dataSnapshot.getValue(Users.class);
                                activity.userName.add(users);
                                activity.mAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }
    }

    private void uploadImage(){
        Uri imageUri = null;
        //or sync compress.

        if (isClicked && isClicked2 && IsClicked3 ){

            File thumb_filePath = new File(Objects.requireNonNull(path));
            Bitmap thumb_bitmap = new Compressor(this)
                    .compressToBitmap(thumb_filePath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 45, baos);
            final byte[] thumb_byte = baos.toByteArray();

            //Compress process is over-------------------------------------------------------------------

            final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ ".jpg");

            UploadTask uploadTask2 = imageRef.putBytes(thumb_byte);

            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloaduri= (Uri) task.getResult();
                                assert downloaduri != null;
                                myUrl = downloaduri.toString();
                                String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("story").child(myId);
                                String storyId = reference.push().getKey();
                                long timend = System.currentTimeMillis()*86400000; // ek din k liye





                                Toast.makeText(AddStoryFinal.this, "size- "+ AllHashtags.size(), Toast.LENGTH_SHORT).show();

                                HashMap<String,Object> hashMap =new HashMap<>();
                                hashMap.put("imageurl",myUrl);
                                hashMap.put("background",myUrl);
                                hashMap.put("timestart", ServerValue.TIMESTAMP);
                                hashMap.put("timend",timend);
                                hashMap.put("type",type);
                                hashMap.put("hashtags","default");
                                hashMap.put("x",x);
                                hashMap.put("y",y);
                                hashMap.put("size",mScaleFactor);
                                hashMap.put("caption",caption.getText().toString());
                                hashMap.put("storyId",storyId);
                                hashMap.put("userId",myId);


                                assert storyId != null;
                                reference.child(storyId).setValue(hashMap);
                                //pd.dismiss();
                                finish();



                                DatabaseReference global= FirebaseDatabase.getInstance().getReference("Global_Story");
                                String globalId = global.push().getKey();
                                assert globalId != null;
                                global.child(globalId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        /*int total = AllHashtags.size();
                                        for (int i=0;i<total;i++){

                                            global.child(globalId).child("hashtags")
                                                    .child(String.valueOf(i))
                                                    .setValue(AllHashtags.get(i));

                                        }*/
                                    }
                                });








                                DatabaseReference posts= FirebaseDatabase.getInstance().getReference("posts")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                String globalId2 = posts.push().getKey();
                                assert globalId2 != null;
                                posts.child(globalId2).setValue(hashMap);



                            }else{
                                Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress2 = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                }
            });
           /* storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {



                }
            });*/

        }else if (isClicked2 && IsClicked3){

            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl = downloaduri.toString();
                        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("story").child(myId);
                        String storyId = reference.push().getKey();
                        long timend = System.currentTimeMillis()*86400000; // ek din k liye
                        HashMap<String,Object> hashMap =new HashMap<>();
                        hashMap.put("imageurl",myUrl);
                        hashMap.put("background",myUrl);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timend",timend);
                        hashMap.put("type",type);
                        hashMap.put("x",x);
                        hashMap.put("y",y);
                        hashMap.put("size",mScaleFactor);
                        hashMap.put("caption",caption.getText().toString());
                        hashMap.put("storyId",storyId);
                        hashMap.put("userId",myId);

                        assert storyId != null;
                        reference.child(storyId).setValue(hashMap);
                        //pd.dismiss();

                        DatabaseReference posts= FirebaseDatabase.getInstance().getReference("posts")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String globalId2 = posts.push().getKey();
                        assert globalId2 != null;
                        posts.child(globalId2).setValue(hashMap);


                        finish();

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (isClicked2 && !isClicked && !IsClicked3){

            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }





            final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl = downloaduri.toString();
                        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("story").child(myId);
                        String storyId = reference.push().getKey();
                        long timend = System.currentTimeMillis()*86400000; // ek din k liye
                        HashMap<String,Object> hashMap =new HashMap<>();
                        hashMap.put("imageurl",myUrl);
                        hashMap.put("background",myUrl);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timend",timend);
                        hashMap.put("type",type);
                        hashMap.put("x",x);
                        hashMap.put("y",y);
                        hashMap.put("size",mScaleFactor);
                        hashMap.put("caption",caption.getText().toString());
                        hashMap.put("storyId",storyId);
                        hashMap.put("userId",myId);

                        assert storyId != null;
                        reference.child(storyId).setValue(hashMap);
                        //pd.dismiss();

                        finish();

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (isClicked && IsClicked3){
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));



            storageTask = imageRef.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl = downloaduri.toString();
                        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();



                            DatabaseReference global= FirebaseDatabase.getInstance().getReference("Global_Story");
                        String storyId = global.push().getKey();
                        long timend = System.currentTimeMillis()*86400000; // ek din k liye
                            String globalId = global.push().getKey();
                            HashMap<String,Object> hashMap2=new HashMap<>();
                            hashMap2.put("imageurl",myUrl);
                            hashMap2.put("background",myUrl);
                            hashMap2.put("timestart", ServerValue.TIMESTAMP);
                            hashMap2.put("timend",timend);
                            hashMap2.put("type",type);
                            hashMap2.put("x",x);
                            hashMap2.put("y",y);
                            hashMap2.put("size",mScaleFactor);
                            hashMap2.put("caption",caption.getText().toString());
                            hashMap2.put("storyId",storyId);
                            hashMap2.put("userId",myId);
                            assert globalId != null;

                            global.child(globalId).setValue(hashMap2);
                        DatabaseReference posts= FirebaseDatabase.getInstance().getReference("posts")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        String globalId2 = posts.push().getKey();
                        assert globalId2 != null;
                        posts.child(globalId2).setValue(hashMap2);
                        finish();




                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (isClicked && !isClicked2 && !IsClicked3){
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));



            storageTask = imageRef.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl = downloaduri.toString();
                        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();



                        DatabaseReference global= FirebaseDatabase.getInstance().getReference("Global_Story");
                        String storyId = global.push().getKey();
                        long timend = System.currentTimeMillis()*86400000; // ek din k liye
                        String globalId = global.push().getKey();
                        HashMap<String,Object> hashMap2=new HashMap<>();
                        hashMap2.put("imageurl",myUrl);
                        hashMap2.put("background",myUrl);
                        hashMap2.put("timestart", ServerValue.TIMESTAMP);
                        hashMap2.put("timend",timend);
                        hashMap2.put("type",type);
                        hashMap2.put("x",x);
                        hashMap2.put("y",y);
                        hashMap2.put("size",mScaleFactor);
                        hashMap2.put("caption",caption.getText().toString());
                        hashMap2.put("storyId",storyId);
                        hashMap2.put("userId",myId);
                        assert globalId != null;

                        global.child(globalId).setValue(hashMap2);
                        finish();




                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (IsClicked3 && !isClicked && !isClicked2){
            MDToast mdToast = MDToast.makeText(this,"You can't post without adding a story",MDToast.TYPE_ERROR,Toast.LENGTH_LONG);
            mdToast.show();
        }
          else{
            MDToast mdToast = MDToast.makeText(this,"Please select any one option to post.",MDToast.TYPE_ERROR,Toast.LENGTH_LONG);
            mdToast.show();
        }

    }



    private void uploadVideo(){


        if (isClicked2 && isClicked && IsClicked3){
            Uri imageUri =null;
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            final StorageReference imageRef2 = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef2.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl2 = downloaduri.toString();


                        String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("story").child(myId);
                        String storyId = reference.push().getKey();
                        long timend = System.currentTimeMillis()*86400000; // ek din k liye




                      /*  try{
                            // Retrieve storage account from connection-string
                            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                            // Create the blob client
                            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                            // Get a reference to a container
                            // The container name must be lower case
                            CloudBlobContainer container = blobClient.getContainerReference("knockstore");

                            // Create the container if it does not exist


                            // Create a permissions object
                            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

                            // Include public access in the permissions object
                            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

                            // Set the permissions on the container
                            container.uploadPermissions(containerPermissions);

                            // Create or overwrite the "myimage.jpg" blob with contents from a local file
                            CloudBlockBlob blob = container.getBlockBlobReference(myId+"/.mp4");
                            File source = new File(mImageUri.toString());
                            blob.upload(new FileInputStream(source), source.length());



                            String url = blob.getUri().toString();

                            HashMap<String,Object> hashMap =new HashMap<>();
                            hashMap.put("imageurl",url);
                            hashMap.put("timestart", ServerValue.TIMESTAMP);
                            hashMap.put("timend",timend);
                            hashMap.put("background",myUrl2);
                            hashMap.put("type",type);
                            hashMap.put("x",x);
                            hashMap.put("y",y);
                            hashMap.put("size",mScaleFactor);
                            hashMap.put("caption",caption.getText().toString());
                            hashMap.put("storyId",storyId);
                            hashMap.put("userId",myId);

                            assert storyId != null;
                            reference.child(storyId).setValue(hashMap);
                            //pd.dismiss();

                            if (isClicked){

                                DatabaseReference global= FirebaseDatabase.getInstance().getReference("Global_Story");
                                String globalId = global.push().getKey();
                                assert globalId != null;
                                global.child(globalId).setValue(hashMap);
                            }

                            DatabaseReference posts= FirebaseDatabase.getInstance().getReference("posts")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            String globalId = posts.push().getKey();
                            assert globalId != null;
                            posts.child(globalId).setValue(hashMap);


                        }
                        catch(Exception e){

                        }*/

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (isClicked2 && IsClicked3){
            Uri imageUri =null;
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            final StorageReference imageRef2 = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef2.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl2 = downloaduri.toString();

                        final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(mImageUri));


                        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp","demo.mp4");

                        storageTask = imageRef.putFile(mImageUri);
                        storageTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {


                                if (task.isSuccessful()){
                                    Uri downloaduri= (Uri) task.getResult();
                                    assert downloaduri != null;
                                    myUrl = downloaduri.toString();
                                    file.delete();
                                    String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("story").child(myId);
                                    String storyId = reference.push().getKey();
                                    long timend = System.currentTimeMillis()*86400000; // ek din k liye



                                    HashMap<String,Object> hashMap =new HashMap<>();
                                    hashMap.put("imageurl",myUrl);
                                    hashMap.put("timestart", ServerValue.TIMESTAMP);
                                    hashMap.put("timend",timend);
                                    hashMap.put("background",myUrl2);
                                    hashMap.put("type",type);
                                    hashMap.put("x",x);
                                    hashMap.put("y",y);
                                    hashMap.put("size",mScaleFactor);
                                    hashMap.put("caption",caption.getText().toString());
                                    hashMap.put("storyId",storyId);
                                    hashMap.put("userId",myId);

                                    assert storyId != null;
                                    reference.child(storyId).setValue(hashMap);
                                    //pd.dismiss();

                                        DatabaseReference posts= FirebaseDatabase.getInstance().getReference("posts")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        String postId = posts.push().getKey();
                                        assert postId != null;
                                        posts.child(postId).setValue(hashMap);


                                    finish();

                                }else{
                                    Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (isClicked && IsClicked3){

            Uri imageUri =null;
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            final StorageReference imageRef2 = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef2.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl2 = downloaduri.toString();

                        final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(mImageUri));


                        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp","demo.mp4");

                        storageTask = imageRef.putFile(mImageUri);
                        storageTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {


                                if (task.isSuccessful()){
                                    Uri downloaduri= (Uri) task.getResult();
                                    assert downloaduri != null;
                                    myUrl = downloaduri.toString();
                                    file.delete();
                                    String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                    long timend = System.currentTimeMillis()*86400000; // ek din k liye

                                    DatabaseReference global= FirebaseDatabase.getInstance().getReference("Global_Story");
                                        String globalId = global.push().getKey();

                                        HashMap<String,Object> hashMap2=new HashMap<>();
                                        hashMap2.put("imageurl",myUrl);
                                        hashMap2.put("background",myUrl2);
                                        hashMap2.put("timestart", ServerValue.TIMESTAMP);
                                        hashMap2.put("timend",timend);
                                        hashMap2.put("type",type);
                                        hashMap2.put("x",x);
                                        hashMap2.put("y",y);
                                        hashMap2.put("size",mScaleFactor);
                                        hashMap2.put("caption",caption.getText().toString());
                                        hashMap2.put("storyId",globalId);
                                        hashMap2.put("userId",myId);

                                        assert globalId != null;
                                        global.child(globalId).setValue(hashMap2);



                                        DatabaseReference posts= FirebaseDatabase.getInstance().getReference("posts")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        String postId = posts.push().getKey();
                                        posts.child(postId).setValue(hashMap2);




                                }else{
                                    Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (isClicked2){
            Uri imageUri =null;
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            final StorageReference imageRef2 = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef2.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl2 = downloaduri.toString();

                        final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(mImageUri));


                        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp","demo.mp4");

                        storageTask = imageRef.putFile(mImageUri);
                        storageTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {


                                if (task.isSuccessful()){
                                    Uri downloaduri= (Uri) task.getResult();
                                    assert downloaduri != null;
                                    myUrl = downloaduri.toString();
                                    file.delete();
                                    String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("story").child(myId);
                                    String storyId = reference.push().getKey();
                                    long timend = System.currentTimeMillis()*86400000; // ek din k liye



                                    HashMap<String,Object> hashMap =new HashMap<>();
                                    hashMap.put("imageurl",myUrl);
                                    hashMap.put("timestart", ServerValue.TIMESTAMP);
                                    hashMap.put("timend",timend);
                                    hashMap.put("background",myUrl2);
                                    hashMap.put("type",type);
                                    hashMap.put("x",x);
                                    hashMap.put("y",y);
                                    hashMap.put("size",mScaleFactor);
                                    hashMap.put("caption",caption.getText().toString());
                                    hashMap.put("storyId",storyId);
                                    hashMap.put("userId",myId);

                                    assert storyId != null;
                                    reference.child(storyId).setValue(hashMap);
                                    //pd.dismiss();

                                    finish();

                                }else{
                                    Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else  if (isClicked && !isClicked2 && !IsClicked3){
            Uri imageUri =null;
            try {
                Bitmap imageBitmap = SiliCompressor.with(AddStoryFinal.this).getCompressBitmap(path);
                imageUri =getImageUri(AddStoryFinal.this,imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
            final StorageReference imageRef2 = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(imageUri));
            storageTask = imageRef2.putFile(imageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageRef2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {


                    if (task.isSuccessful()){
                        Uri downloaduri= (Uri) task.getResult();
                        assert downloaduri != null;
                        myUrl2 = downloaduri.toString();

                        final StorageReference imageRef = storageReference.child(System.currentTimeMillis()+ "."+getFileExtension(mImageUri));


                        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"Knock Knock/"+"Temp","demo.mp4");

                        storageTask = imageRef.putFile(mImageUri);
                        storageTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return imageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {


                                if (task.isSuccessful()){
                                    Uri downloaduri= (Uri) task.getResult();
                                    assert downloaduri != null;
                                    myUrl = downloaduri.toString();
                                    file.delete();
                                    String myId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                    long timend = System.currentTimeMillis()*86400000; // ek din k liye

                                    DatabaseReference global= FirebaseDatabase.getInstance().getReference("Global_Story");
                                    String globalId = global.push().getKey();

                                    HashMap<String,Object> hashMap2=new HashMap<>();
                                    hashMap2.put("imageurl",myUrl);
                                    hashMap2.put("background",myUrl2);
                                    hashMap2.put("timestart", ServerValue.TIMESTAMP);
                                    hashMap2.put("timend",timend);
                                    hashMap2.put("type",type);
                                    hashMap2.put("x",x);
                                    hashMap2.put("y",y);
                                    hashMap2.put("size",mScaleFactor);
                                    hashMap2.put("caption",caption.getText().toString());
                                    hashMap2.put("storyId",globalId);
                                    hashMap2.put("userId",myId);

                                    assert globalId != null;
                                    global.child(globalId).setValue(hashMap2);


                                }else{
                                    Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(AddStoryFinal.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (IsClicked3 && !isClicked2 && !isClicked){
            MDToast mdToast = MDToast.makeText(this,"You can't post without adding a story",MDToast.TYPE_ERROR,Toast.LENGTH_LONG);
            mdToast.show();
        } else{

               MDToast mdToast = MDToast.makeText(this,"Please select any one option to post",MDToast.TYPE_ERROR,Toast.LENGTH_LONG);
               mdToast.show();
        }


    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
