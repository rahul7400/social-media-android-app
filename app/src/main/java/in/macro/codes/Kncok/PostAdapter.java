package in.macro.codes.Kncok;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.macro.codes.Kncok.Story.Story;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {
    private List<String>mPostList = new ArrayList<>();
    private String user_id;
    private Context mContext;
    PostAdapter(String user_id, List<String> mPostId,Context mContext) {
        this.mPostList=mPostId;
        this.user_id=user_id;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(user_id).child(mPostList.get(position));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Story story = dataSnapshot.getValue(Story.class);
                assert story != null;
                RequestOptions myOptions = new RequestOptions()
                        .override(180, 200);
                myOptions.placeholder(R.drawable.default_send_image);
                Glide.with(mContext)
                        .asBitmap()
                        .apply(myOptions)
                        .fitCenter()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .load(story.getImageurl())
                        .into(holder.postPreview);

                if (story.getType().equals("video")){
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_send_image);

                    Glide.with(mContext)
                            .load(story.getImageurl())
                            .apply(requestOptions)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .thumbnail(Glide.with(mContext).load(story.getImageurl()))
                            .into(holder.postPreview);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private ImageView postPreview;
        private TextView like_count;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            postPreview = (ImageView) itemView.findViewById(R.id.postPreview);
            like_count = (TextView) itemView.findViewById(R.id.like_count);
;        }
    }
}
