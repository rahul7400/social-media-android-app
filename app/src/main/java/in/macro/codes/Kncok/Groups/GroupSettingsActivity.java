package in.macro.codes.Kncok.Groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import in.macro.codes.Kncok.R;

public class GroupSettingsActivity extends AppCompatActivity {
    private CheckBox grp_post;
    private DatabaseReference mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        grp_post=(CheckBox)findViewById(R.id.grp_post);
        String group_name = getIntent().getStringExtra("group_name");
        assert group_name != null;
        mGroup = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_name);
        DatabaseReference mGroups = FirebaseDatabase.getInstance().getReference().child("Groups").child(group_name);
        mGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mode = Objects.requireNonNull(dataSnapshot.child("grp_mode").getValue()).toString();


                if (mode.equals("admin")){
                    if (dataSnapshot.child("Admin").hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                        grp_post.setVisibility(View.VISIBLE);
                    } else {
                        grp_post.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        if(preferences.contains("checked") && preferences.getBoolean("checked", false)) {
            grp_post.setChecked(true);
        }else {
            grp_post.setChecked(false);

        }



        grp_post.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    mGroup.child("grp_mode").setValue("admin");
                    editor.putBoolean("checked", true);
                    editor.apply();

                }else{
                    mGroup.child("grp_mode").setValue("user");
                    editor.putBoolean("checked", false);
                    editor.apply();
                }
            }
        });
    }
}
