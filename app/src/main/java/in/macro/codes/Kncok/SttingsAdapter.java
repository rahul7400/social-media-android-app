package in.macro.codes.Kncok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SttingsAdapter extends RecyclerView.Adapter<SttingsAdapter.Viewholder> {

    private ArrayList<String>settingsList = new ArrayList<>();
    private Context mContext;
    public SttingsAdapter(ArrayList<String> settingsList , Context mContext) {
        this.settingsList=settingsList;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_layout,parent,false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String c = settingsList.get(position);
        holder.gname.setText(c);
        holder.gname.setTextColor(Color.parseColor("#000000"));
        holder.gstatus.setTextColor(Color.parseColor("#808080"));


        switch (position){

            case 0:
                holder.gstatus.setText("Privacy, security");
                Picasso.with(mContext).load(R.drawable.account4).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);
                break;
            case 1:
                holder.gstatus.setText("App Theme, animations");
                Picasso.with(mContext).load(R.drawable.app4).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);
                break;
            case 2:
                holder.gstatus.setText("Theme, wallpapers, chat history");
                Picasso.with(mContext).load(R.drawable.chat3).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);
                break;
            case 3:
                holder.gstatus.setText("FAQ, contact us, privacy policy");
                Picasso.with(mContext).load(R.drawable.help3).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);
                break;

            case 4:
                Picasso.with(mContext).load(R.drawable.logout3).placeholder(R.drawable.default_avatar).into(holder.gthumb_image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(mContext,StartActivity.class);
                        mContext.startActivity(intent);
                        ((Activity)mContext).finish();
                    }
                });
                holder.gstatus.setText("Log out from this device");
                break;



        }
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        public TextView gname, gstatus;
        public CircleImageView gthumb_image;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            gname = (TextView) itemView.findViewById(R.id.guser_single_name);
            gstatus = (TextView) itemView.findViewById(R.id.guser_single_status);
            gthumb_image = (CircleImageView) itemView.findViewById(R.id.guser_single_image);
        }
    }
}
