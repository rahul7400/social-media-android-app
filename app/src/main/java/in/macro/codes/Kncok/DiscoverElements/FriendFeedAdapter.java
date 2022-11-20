package in.macro.codes.Kncok.DiscoverElements;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.ChatActivity;
import in.macro.codes.Kncok.DoubleClickListener;
import in.macro.codes.Kncok.GetTimeAgo;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Story.Story;

public class FriendFeedAdapter extends RecyclerView.Adapter<FriendFeedAdapter.Viewholder> {
    private List<Post> mPost = new ArrayList<>();
    private Context mContext;
    public FriendFeedAdapter(Context friendsFeeds, List<Post> mPost) {
        this.mPost = mPost;
        this.mContext = friendsFeeds;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mainfeed_item_view,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Post post = mPost.get(position);

        if (post.getCaption().equals("")){
            holder.caption.setVisibility(View.GONE);
        }else{
            holder.caption.setText(post.getCaption());
        }




        Glide.with(mContext).load(post.getMessage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(post.getFrom());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String profile = dataSnapshot.child("thumb_image").getValue().toString();
                holder.username.setText(name);

                if(profile.equals("default")){
                    Glide.with(mContext).load(R.drawable.default_avatar).into(holder.profile);
                }else{
                    Glide.with(mContext).load(profile).into(holder.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance()
                .getReference().child("posts").child(post.getPush_id());

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("likes").hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                    holder.small_heart.setImageResource(R.drawable.ic_heart_border);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.imageView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {


            }

            @Override
            public void onDoubleClick(View v) {

                HashMap hashMap = new HashMap();
                hashMap.put(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                        ,FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference1.child("likes").updateChildren(hashMap);


                holder.main_heart.setVisibility(View.VISIBLE);
                Animation animation   =  AnimationUtils.loadAnimation(mContext, R.anim.zoom_in);
                animation.setDuration(200);
                holder.main_heart.setAnimation(animation);
                holder.main_heart.animate();
                animation.start();

                holder.small_heart.setImageResource(R.drawable.heart_on);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.main_heart.setVisibility(View.GONE);
                        Animation animation   =  AnimationUtils.loadAnimation(mContext, R.anim.zoom_out);
                        animation.setDuration(200);
                        holder.main_heart.setAnimation(animation);
                        holder.main_heart.animate();
                        animation.start();
                    }
                }, 400);
            }
        });

        holder.small_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.small_heart.setImageResource(R.drawable.ic_heart_border);
                reference1.child("likes").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .removeValue();
            }
        });


        GetTimeAgo getTimeAgo = new GetTimeAgo();
     // String lastSeenTime = GetTimeAgo.getTimeAgo(Long.parseLong(post.getTime()), mContext);
       // holder.time.setText(lastSeenTime);


    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private ImageView imageView,main_heart,small_heart;
        private ProgressBar progressBar;
        private CircleImageView profile;
        private TextView caption;
        private TextView username;
        private TextView time;



       public Viewholder(@NonNull View itemView) {
           super(itemView);
           imageView = itemView.findViewById(R.id.media_post);
           progressBar = itemView.findViewById(R.id.progressBar);
           profile = itemView.findViewById(R.id.profile_photo);
           caption = itemView.findViewById(R.id.caption_text);
           username = itemView.findViewById(R.id.user_name);
           time = itemView.findViewById(R.id.date_added);
           main_heart = (ImageView) itemView.findViewById(R.id.main_heart);
           small_heart = (ImageView) itemView.findViewById(R.id.heart_outline);

       }
   }
}
