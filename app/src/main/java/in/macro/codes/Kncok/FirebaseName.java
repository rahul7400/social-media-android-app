package in.macro.codes.Kncok;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FirebaseName extends Application {
    public String id;
    public Users users;
    public DatabaseReference mUser;
    public FirebaseName(String id){
        this.id=id;
    }

   public String getUserName(){

        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 users = dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return users.getName();
   }

    public String getUserProfile(){

        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return users.getThumb_image();
    }
}
