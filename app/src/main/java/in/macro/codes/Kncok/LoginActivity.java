package in.macro.codes.Kncok;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Objects;



public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mLoginEmail;
    private EditText mLoginPassword;

    private Button mLogin_btn,mSign_up;
    private Context mContext;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    AlertDialog dialog_verifying,profile_dialog;
    private ProgressDialog mRegProgress;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mLogin_btn=(Button)findViewById(R.id.lg_login);
        mSign_up=(Button)findViewById(R.id.lg_signup);
        mLoginEmail=(EditText)findViewById(R.id.lg_email);
        mLoginPassword=(EditText)findViewById(R.id.lg_pass);
        mContext = this;
        mRegProgress = new ProgressDialog(this);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=mLoginEmail.getText().toString();
                String password=mLoginPassword.getText().toString();

                mRegProgress.setTitle("Logging In User");
                mRegProgress.setMessage("Cleaning your account.");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                if (email.isEmpty() && password.isEmpty()){
                    MDToast mdToast = MDToast.makeText(LoginActivity.this, "You can't leave above fields empty",MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
                else {

                    loginUser(email,password);
                }



            }
        });

        mSign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                overridePendingTransition(R.anim.rg, R.anim.no_anim);
            }
        });





        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }



    private void loginUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    //Log.e("rg", "onComplete: Failed=" + Objects.requireNonNull(task.getException()).getMessage());

                    String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                mUserDatabase.child(current_user_id).child("device_token").setValue(Objects.requireNonNull(task.getResult()).getToken()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // dialog_verifying.cancel();
                                        //dialog_verifying = null;
                                        mRegProgress.dismiss();
                                        Intent mainIntent = new Intent(LoginActivity.this, FirstMainActivity.class);
                                        mainIntent.putExtra("id", current_user_id);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                });

                            } else {
                                Log.e("TAG", "exception=" + Objects.requireNonNull(task.getException()).toString());
                                MDToast mdToast = MDToast.makeText(Objects.requireNonNull(LoginActivity.this), "exception - " + task.getException().toString(), MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS);
                                mdToast.show();
                            }
                        }
                    });

                }


            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();

            FirebaseDatabase.getInstance().goOnline();

    }
}
