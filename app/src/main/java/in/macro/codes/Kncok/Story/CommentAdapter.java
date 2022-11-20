package in.macro.codes.Kncok.Story;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.FirebaseName;
import in.macro.codes.Kncok.GetTimeAgo;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Users;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Viewholder> {

    private Context mContext;
    private List<Comments> mComList = new ArrayList<>();
    public CommentAdapter(StoryActivity storyActivity, List<Comments> mComList) {

        this.mComList = mComList;
        this.mContext = storyActivity;
    }

    public CommentAdapter(Context mContext, List<Comments> mComList) {
        this.mComList = mComList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Comments comments = mComList.get(position);

        holder.comment.setText(comments.getComment());

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        String lastSeenTime = getTimeAgo.getTimeAgo(comments.getTime(), mContext);
        holder.time.setText("• "+lastSeenTime);


        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(comments.getUserId());
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();
                holder.username.setText(name);
                Picasso.with(mContext).load(image).placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar).into(holder.profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mComList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private TextView username,comment ,time;
        private CircleImageView profile;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            username =(TextView) itemView.findViewById(R.id.cUser);
            comment =(TextView) itemView.findViewById(R.id.cText);
            profile = (CircleImageView) itemView.findViewById(R.id.cProfile);
            time =(TextView) itemView.findViewById(R.id.cTime);
        }
    }


}
