package in.macro.codes.Kncok.Groups;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.R;

public class GroupSuggestAdapter extends RecyclerView.Adapter<GroupSuggestAdapter.Viewholder> {

    private List<Groups> gList =new ArrayList<>();
    private Context mContext;

    public GroupSuggestAdapter(List<Groups> gList, Context mContext) {
        this.gList = gList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupSuggestAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupSuggestAdapter.Viewholder holder, int position) {

        Groups groups = gList.get(position);
        holder.gname.setText(groups.getGname());
        holder.gcategory.setText(groups.getCategory());
        holder.gView.setText("View Group");
        Picasso.with(mContext).load(groups.getGimage()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(holder.gthumb_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(mContext).load(groups.getGimage()).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);

            }
        });

        holder.gView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, GroupProfileActivity.class);
                intent.putExtra("gname",groups.getGname());
                ActivityOptions activityOptions= ActivityOptions.makeCustomAnimation(mContext,R.anim.rg,R.anim.nothing);
                mContext.startActivity(intent,activityOptions.toBundle());
            }
        });


    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemCount() {
        return gList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    public class Viewholder extends RecyclerView.ViewHolder{
        public TextView gname, gcategory;
        public CircleImageView gthumb_image;
        public Button gView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            gname = (TextView) itemView.findViewById(R.id.sugg_name);
            gcategory = (TextView) itemView.findViewById(R.id.sugg_city);
            gthumb_image = (CircleImageView) itemView.findViewById(R.id.sugg_profile);
            gView = (Button) itemView.findViewById(R.id.sugg_add);
        }
    }
}
