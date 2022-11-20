package in.macro.codes.Kncok;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
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

import javax.xml.validation.Validator;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.Viewholder> {

    private List<String>suggestList = new ArrayList<>();
    private Context mContext;
    private String city;

    public SuggestionAdapter(List<String> suggestList,Context mContext) {
        this.suggestList = suggestList;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(suggestList.get(position));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;
                holder.mUsername.setText(users.getName());
                holder.mCity.setText(users.getCity());
                //holder.mProfile.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fall_down));
               // holder.mUsername.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fall_down));
               // holder.mCity.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fall_down));
                 holder.container.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.zoom_in));
                Picasso.with(holder.itemView.getContext()).load(users.getThumb_image()).placeholder(R.drawable.default_avatar).into(holder.mProfile);

                holder.add_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(mContext,ProfileActivity.class);
                        intent.putExtra("user_id",users.getUid());
                        ActivityOptions activityOptions= ActivityOptions.makeCustomAnimation(mContext,R.anim.profile_enter,R.anim.nothing);
                        mContext.startActivity(intent,activityOptions.toBundle());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });













    }

    @Override
    public int getItemCount() {
        return suggestList.size();
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

        public CircleImageView mProfile;
        public TextView mUsername,mCity;
        public Button add_friend;
        public LinearLayout container;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            mProfile =(CircleImageView) itemView.findViewById(R.id.sugg_profile);
            mUsername = (TextView) itemView.findViewById(R.id.sugg_name);
            mCity = (TextView) itemView.findViewById(R.id.sugg_city);
            add_friend=(Button) itemView.findViewById(R.id.sugg_add);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
