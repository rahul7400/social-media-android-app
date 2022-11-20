package in.macro.codes.Kncok.Groups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.R;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    public Context context;
    public List<Groups> groupsList;


    public GroupsAdapter(Context context, List<Groups> groupsList) {
        this.context = context;
        this.groupsList = groupsList;


    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.users_single_layout, parent, false);
        return new GroupViewHolder(v);
    }


    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView gname, gstatus;
        public CircleImageView gthumb_image;


        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);

            gname = (TextView) itemView.findViewById(R.id.user_single_name);
            gstatus = (TextView) itemView.findViewById(R.id.user_single_status);
            gthumb_image = (CircleImageView) itemView.findViewById(R.id.user_single_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder holder, int position) {
        final Groups groups = groupsList.get(position);
        holder.gname.setText(groups.getGname());



       DatabaseReference mMessageDatabase = FirebaseDatabase.getInstance().getReference()
               .child("Groups").child(groups.getGname());
        Query lastMessageQuery = mMessageDatabase.child("gmessages").limitToLast(1);

        lastMessageQuery.addChildEventListener(new ChildEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final String data = dataSnapshot.child("message").getValue().toString();
                final String data_type=dataSnapshot.child("type").getValue().toString();
                final String from =dataSnapshot.child("from").getValue().toString();


                DatabaseReference muser = FirebaseDatabase.getInstance().getReference().child("Users").child(from);
                muser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        //String nickname = name.substring(0, name.indexOf(' '));

                        if (data_type.equals("text")){

                            if (from.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                holder.gstatus.setText("You" +"\t"+ ":"+"\t" + data);
                            }else{
                                holder.gstatus.setText(name +"\t"+ ":"+"\t" + data);
                            }


                        } else if (data_type.equals("image")) {

                            if (from.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                holder.gstatus.setText("You" +"\t"+ ":"+"\t" + "Image File");
                            }else{
                                holder.gstatus.setText(name +"\t"+ ":"+"\t" + "Image File");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






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

        Picasso.with(context).load(groups.getGimage()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_avatar).into(holder.gthumb_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(groups.getGimage()).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gchatIntent = new Intent(holder.gthumb_image.getContext(), GroupChatActivty.class);
                gchatIntent.putExtra("group_name", groups.getGname());
                gchatIntent.putExtra("grp_mode", groups.getGrp_mode());
                holder.itemView.getContext().startActivity(gchatIntent);

            }
        });
    }


    @Override
    public long getItemId(int position) {

        return position;
    }



    @Override
    public int getItemViewType(int position) {

        return position;
    }


    @Override
    public int getItemCount() {
        return groupsList.size();
    }
}
