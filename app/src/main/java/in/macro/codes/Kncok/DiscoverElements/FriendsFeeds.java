package in.macro.codes.Kncok.DiscoverElements;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import in.macro.codes.Kncok.ChatActivity;
import in.macro.codes.Kncok.Gallery.GalleryActivity;
import in.macro.codes.Kncok.ImageUploadFinal;
import in.macro.codes.Kncok.Messages;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.SharePost.EditPost;
import in.macro.codes.Kncok.Story.Story;

import static android.app.Activity.RESULT_OK;

public class FriendsFeeds extends Fragment {
    private FloatingActionButton addPost;
    private View mView;

    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int TOTAL_ITEMS_TO_LOAD = 2000;
    private int mCurrentPage = 1;

    private List<Post> mPost = new ArrayList<>();
    private FriendFeedAdapter mAdapter;
    private RecyclerView mRecyclerView;
    List<String> followingUsers = new ArrayList<>();
    public FriendsFeeds() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friends_feeds, container, false);
        // Inflate the layout for this fragment
        addPost = (FloatingActionButton) mView.findViewById(R.id.Addpost);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.Feedpost);
        mAdapter = new FriendFeedAdapter(getContext(),mPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        CheckFriends();
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), 1);



            }
        });
        return mView;
    }

    private void CheckFriends(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                   followingUsers.add(snapshot.getKey());
               }
               getPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts");
        Query messageQuery = reference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        mPost.clear();
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Post post = dataSnapshot.getValue(Post.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                for (String id : followingUsers){
                    assert post != null;
                    if (id.equals(post.getFrom())){
                        mPost.add(post);
                        mAdapter.notifyDataSetChanged();

                    }
                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
           if (resultCode == RESULT_OK){

               assert data != null;
               Uri img = data.getData();
               Intent intent =new Intent(getContext(), EditPost.class);
               intent.putExtra("user_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
               assert img != null;
               intent.putExtra("uri",img.toString());
               startActivity(intent);
           }
        }
    }
}