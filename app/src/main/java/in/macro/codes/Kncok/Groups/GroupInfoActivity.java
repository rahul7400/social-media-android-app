package in.macro.codes.Kncok.Groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.macro.codes.Kncok.Conv;
import in.macro.codes.Kncok.R;

public class GroupInfoActivity extends AppCompatActivity {

    private RecyclerView mGroupUsersList;
    private DatabaseReference mUserList,mUsers;
    private String group_name;
    private List<String> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mGroupUsersList=(RecyclerView)findViewById(R.id.gusers_list);
        group_name=getIntent().getStringExtra("group_name");

        mUserList= FirebaseDatabase.getInstance().getReference().child("Users").child("Groups");
        mUsers= FirebaseDatabase.getInstance().getReference().child("Users");


    }

}
