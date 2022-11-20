package in.macro.codes.Kncok;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.Viewholder> {

    private Context mContext;
    private  List<Users>userlist ;

    public FriendSearchAdapter(Context mContext, List<Users> userlist) {
        this.mContext = mContext;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        Users c = userlist.get(position);
        holder.mDisplayname.setText(c.getName());
        holder.mStatus.setText(c.getStatus());

        Picasso.with(holder.mProfile.getContext()).load(c.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(holder.mProfile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(holder.mProfile.getContext()).load(c.getThumb_image()).placeholder(R.drawable.default_avatar).into(holder.mProfile);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("user_id", c.getUid());
                ActivityOptions activityOptions =ActivityOptions.makeCustomAnimation(mContext,R.anim.profile_enter,R.anim.nothing);
                mContext.startActivity(profileIntent,activityOptions.toBundle());

            }
        });
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }
    @Override
    public long getItemId(int position) {

        return position;
    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }


    public class Viewholder extends RecyclerView.ViewHolder{

        public TextView mDisplayname,mStatus;
        public CircleImageView mProfile;

    public Viewholder(@NonNull View itemView) {
        super(itemView);

        mDisplayname=(TextView)itemView.findViewById(R.id.user_single_name);
        mStatus=(TextView)itemView.findViewById(R.id.user_single_status);
        mProfile=(CircleImageView) itemView.findViewById(R.id.user_single_image);

    }
}
}
