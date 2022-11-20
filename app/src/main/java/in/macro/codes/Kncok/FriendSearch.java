package in.macro.codes.Kncok;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FriendSearch extends Fragment {
    private RecyclerView mfriend_search;
    private View mView;
    private FriendSearchAdapter mAdapter;
    private String text;
    private List<Users> getUser = new ArrayList<>();
    public FriendSearch() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend_search, container, false);
        assert getArguments() != null;
        text = getArguments().getString("text");

        mfriend_search=(RecyclerView) mView.findViewById(R.id.frd_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mfriend_search.setHasFixedSize(true);
        mfriend_search.setLayoutManager(linearLayoutManager);
        mAdapter = new FriendSearchAdapter(getContext(),getUser);
        mfriend_search.setAdapter(mAdapter);

        if (text!=null){
            Search(text);
        }

        return mView;
    }


    private void Search(String text){
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    assert users != null;
                    if (text.equalsIgnoreCase(users.getName()) || text.equalsIgnoreCase(users.getEmail())){
                        getUser.add(users);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
