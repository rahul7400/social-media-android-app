package in.macro.codes.Kncok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.macro.codes.Kncok.QRCode.qrcodeMainActivity;

public class FriendSearchActivity extends AppCompatActivity {

    private EditText d_text;
    private ViewPager viewPager;
    String text;
    private List<Users>mNameList = new ArrayList<>();
    private RecyclerView friendView;
    private FriendSearchAdapter mAdapter;
    private Button searchBtn;
    private LinearLayout linearQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);


        linearQr = (LinearLayout) findViewById(R.id.linearQr);
        d_text = (EditText) findViewById(R.id.frd_search);
        friendView = (RecyclerView) findViewById(R.id.friends_list);
        searchBtn = (Button) findViewById(R.id.seachBtn);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = d_text.getText().toString();
                searchFriend();
            }
        });


        linearQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendSearchActivity.this, qrcodeMainActivity.class);
                startActivity(intent);
            }
        });
        mAdapter = new FriendSearchAdapter(FriendSearchActivity.this,mNameList);
        friendView.setHasFixedSize(true);
        friendView.setLayoutManager(new LinearLayoutManager(FriendSearchActivity.this));
        friendView.setAdapter(mAdapter);

    }


    private void searchFriend(){
        if (text !=null){
            mNameList.clear();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        reference2.child(Objects.requireNonNull(snapshot.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                Users users = dataSnapshot2.getValue(Users.class);
                                String name = Objects.requireNonNull(dataSnapshot2.child("name").getValue()).toString();
                                String email = Objects.requireNonNull(dataSnapshot2.child("email").getValue()).toString();



                                if (text.equalsIgnoreCase(name) || text.equalsIgnoreCase(email)){
                                    mNameList.add(users);
                                    mAdapter.notifyDataSetChanged();
                                }
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
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing,R.anim.profile_exit);
    }
}
