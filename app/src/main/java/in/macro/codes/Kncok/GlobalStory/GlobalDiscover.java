package in.macro.codes.Kncok.GlobalStory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.macro.codes.Kncok.CenterZoomLayoutManager;
import in.macro.codes.Kncok.GlobalStory.Adapters.GlobalDiscoverAdapter;
import in.macro.codes.Kncok.GlobalStory.Adapters.GlobalPopularAdapter;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Story.Story;


public class GlobalDiscover extends Fragment {

    private RecyclerView recyclerView1,recyclerView2,recyclerView3;
    View mView;
    private List<String> AllHashTags = new ArrayList<>();
    private List<Story> mGlobalStory = new ArrayList<>();
    private TextView temp;
    private GlobalDiscoverAdapter mAdapter,mAdapter2;
    private GlobalPopularAdapter mAdapter3;


    public GlobalDiscover() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_global_discover, container, false);
        recyclerView1 = (RecyclerView) mView.findViewById(R.id.recyclerview1);
        recyclerView2 = (RecyclerView) mView.findViewById(R.id.recyclerview2);
        recyclerView3 = (RecyclerView) mView.findViewById(R.id.recyclerview3);
        temp = (TextView) mView.findViewById(R.id.temp);
        mAdapter = new GlobalDiscoverAdapter(getContext(),AllHashTags,mGlobalStory);
        mAdapter2 = new GlobalDiscoverAdapter(getContext(),AllHashTags,mGlobalStory);
        mAdapter3 = new GlobalPopularAdapter(getContext(),AllHashTags,mGlobalStory);

        getGlobalStory();
       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
       // CenterZoomLayoutManager linearLayoutManager = new CenterZoomLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView1.setLayoutManager(linearLayoutManager);
        recyclerView1.setAdapter(mAdapter);


        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        // CenterZoomLayoutManager linearLayoutManager = new CenterZoomLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        recyclerView2.setAdapter(mAdapter2);

        // CenterZoomLayoutManager linearLayoutManager = new CenterZoomLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);


        GridLayoutManager gridLayoutManager =new GridLayoutManager(getContext(),3);
        recyclerView3.setLayoutManager(gridLayoutManager);
        recyclerView3.setAdapter(mAdapter3);



        return mView;
    }

    private void getGlobalStory(){

        String[] memes = {"memes","funny","dank","dank _indian_memes","indianmemes"};
        DatabaseReference mGlobal = FirebaseDatabase.getInstance().getReference().child("Global_Story");
        mGlobal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AllHashTags.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Story story = snapshot.getValue(Story.class);
                    mGlobalStory.add(story);
                    assert story != null;
                    mAdapter.notifyDataSetChanged();
                    mAdapter2.notifyDataSetChanged();
                    mAdapter3.notifyDataSetChanged();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}