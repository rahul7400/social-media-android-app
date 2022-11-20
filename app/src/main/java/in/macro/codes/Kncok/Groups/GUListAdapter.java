package in.macro.codes.Kncok.Groups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.macro.codes.Kncok.ProfileActivity;
import in.macro.codes.Kncok.R;
import in.macro.codes.Kncok.Story.AddStoryActivity;
import in.macro.codes.Kncok.Users;

import static java.security.AccessController.getContext;

public class GUListAdapter extends RecyclerView.Adapter<GUListAdapter.Viewholder> {

    private Context mContext;
    private String gname;
    private  ArrayList<String> userList;
    public GUListAdapter(Context context, ArrayList<String> userList,String gname) {
        this.mContext = context;
        this.userList = userList;
        this.gname=gname;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.grp_user_list_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(position));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                    holder.gname.setText("You");
                }else {
                    holder.gname.setText(users.getName());
                }

                holder.gstatus.setText(users.getStatus());
                Picasso.with(mContext).load(users.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(holder.gthumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(users.getThumb_image()).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);

                    }
                });

                DatabaseReference mGrp = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname).child("Admin");
                mGrp.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(users.getUid())){
                            holder.admin_batch.setVisibility(View.VISIBLE);
                        }else{
                            holder.admin_batch.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (!users.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app
                                    .AlertDialog.Builder(mContext);
                            View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.group_asklist,null );
                            builder.setView(viewInflated);
                            final Button make_admin =(Button)viewInflated.findViewById(R.id.make_admin);
                            final Button add_friend =(Button)viewInflated.findViewById(R.id.add_friend);
                            final Button view_profile =(Button)viewInflated.findViewById(R.id.view_pro);
                            final Button kick_user =(Button)viewInflated.findViewById(R.id.kick_user);
                            final AlertDialog dialog = builder.create();
                            DatabaseReference  mGroup = FirebaseDatabase.getInstance().getReference().child("Groups").child(gname);
                            mGroup.keepSynced(true);
                            mGroup.child("Admin").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        make_admin.setVisibility(View.VISIBLE);
                                        kick_user.setVisibility(View.VISIBLE);
                                    }else{
                                        make_admin.setVisibility(View.GONE);
                                        kick_user.setVisibility(View.GONE);
                                    }

                                    if (dataSnapshot.hasChild(users.getUid())){
                                        make_admin.setText("Dismiss Admin");
                                        make_admin.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                DatabaseReference  mGroup23= FirebaseDatabase.getInstance().getReference().child("Groups").child(gname);
                                                mGroup23.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.child("owner").equals(users.getUid())){
                                                            MDToast mdToast = MDToast.makeText(mContext, "Group owner cannot be removed",MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                                            mdToast.show();
                                                        }else{
                                                            mGroup.child("Admin").child(users.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    holder.admin_batch.setVisibility(View.GONE);
                                                                    make_admin.setText("Make Admin");
                                                                    MDToast mdToast = MDToast.makeText(mContext, "Admin dismissed",MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
                                                                    mdToast.show();

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                    }else {
                                        make_admin.setText("Make Admin");
                                        make_admin.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                final Map<String, Object> messageMap = new HashMap<String, Object>();
                                                messageMap.put(users.getUid(), users.getUid());
                                                mGroup.child("Admin").updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                        MDToast mdToast = MDToast.makeText(mContext, "Admin appointed",MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                                        mdToast.show();

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                            kick_user.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    mGroup.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("owner").equals(users.getUid())){
                                                MDToast mdToast = MDToast.makeText(mContext, "Group owner cannot be removed",MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                                mdToast.show();
                                            }else{
                                                if (dataSnapshot.child("Admin").hasChild(users.getUid()) && dataSnapshot.child("Users").hasChild(users.getUid())){

                                                    mGroup.child("Admin").child(users.getUid()).removeValue();
                                                    mGroup.child("Users").child(users.getUid()).removeValue();
                                                }else {
                                                    mGroup.child("Users").child(users.getUid()).removeValue();

                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                            view_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(mContext, ProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("user_id",users.getUid());
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(R.anim.rg, R.anim.no_anim);
                                    dialog.dismiss();

                                }
                            });


                            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.CustomAnimations;
                            dialog.show();
                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public long getItemId(int position) {

        return position;
    }



    @Override
    public int getItemViewType(int position) {

        return position;
    }


    public class Viewholder extends  RecyclerView.ViewHolder{
        public TextView gname, gstatus;
        public CircleImageView gthumb_image;
        public Button admin_batch;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            gname = (TextView) itemView.findViewById(R.id.guser_single_name);
            gstatus = (TextView) itemView.findViewById(R.id.guser_single_status);
            gthumb_image = (CircleImageView) itemView.findViewById(R.id.guser_single_image);
            admin_batch = (Button) itemView.findViewById(R.id.admin_batch);

        }
    }
}
