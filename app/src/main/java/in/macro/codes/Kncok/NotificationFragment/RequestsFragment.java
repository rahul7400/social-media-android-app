package in.macro.codes.Kncok.NotificationFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import in.macro.codes.Kncok.NotificationFragment.RequestAdapter;
import in.macro.codes.Kncok.NotificationFragment.RequestModel;
import in.macro.codes.Kncok.R;


public class RequestsFragment extends Fragment {

    private RecyclerView request_view;
    private View mMianView;
    private List<RequestModel> list = new ArrayList<>();
    private String mCurrentuser;
    private RequestAdapter requestAdapter;


    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private static final int TOTAL_ITEMS_TO_LOAD = 2000;
    private int mCurrentPage = 1;

    public RequestsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMianView = inflater.inflate(R.layout.fragment_requests, container, false);
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        request_view = (RecyclerView) mMianView.findViewById(R.id.request_list);
        requestAdapter = new RequestAdapter(getContext(), list, mCurrentuser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        request_view.setLayoutManager(linearLayoutManager);
        request_view.setAdapter(requestAdapter);

       getNotifications();
        return mMianView;
    }

    private void getNotifications() {
        DatabaseReference mFriend_req = FirebaseDatabase.getInstance().getReference().child("Notify").child(mCurrentuser);
        Query messageQuery = mFriend_req.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                RequestModel requestModel = dataSnapshot.getValue(RequestModel.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }


                assert requestModel != null;
                if ("follower".equals(requestModel.getType())){

                    list.add(requestModel);
                    requestAdapter.notifyDataSetChanged();

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

}