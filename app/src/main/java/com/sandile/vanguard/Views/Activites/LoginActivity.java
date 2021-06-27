package com.sandile.vanguard.Views.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandile.vanguard.Phone.Keyboard;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;
import com.sandile.vanguard.Views.Fragmants.MapFragment;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login, btn_register;
    private TextInputLayout et_email, et_password;
    private TextView tv_forgotPassword, tv_vanguard;
    private ProgressBar pb_login;

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializePallets();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {//What happens when user clicks on..
        switch (v.getId()) {//Sign up button
            case R.id.login_btn_register://register
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_btn_login://Login button
                LoginLogic();
                break;
            case R.id.login_tv_forgotPassword://Forgot password button
                forgotPassword();
                break;
            case R.id.login_tv_vanguard://Open map
                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, new MapFragment()).commit();
                break;
        }

    }

    @Override
    public void onStart() {//check if users is signed in
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //once done you can do anything e.g updateUI
        //updateUI(currentUser);
    }

    private void LoginLogic() {
        Keyboard.hideKeyboard(this);
        pb_login.setVisibility(View.VISIBLE);

        if(areInputsValid()){
            final String tempEmail = et_email.getEditText().getText().toString().trim();
            final String tempPassword = et_password.getEditText().getText().toString().trim();

            mAuth.signInWithEmailAndPassword(tempEmail, tempPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pb_login.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if(user.isEmailVerified()){
                            UserDetail.currentUserId = user.getUid();
                            getUserDetailsFirebase();
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else{
                            pb_login.setVisibility(View.VISIBLE);
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pb_login.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        new SnackTwo().redSnack(LoginActivity.this, "Verification email will arrive within 1 minute. Please verify your email!");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pb_login.setVisibility(View.GONE);
                                    new SnackTwo().redSnack(LoginActivity.this, e.getMessage());
                                }
                            });
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pb_login.setVisibility(View.GONE);
                    new SnackTwo().redSnack(LoginActivity.this, e.getMessage());
                }
            });
        }
        else
            pb_login.setVisibility(View.GONE);

    }


    private void forgotPassword() {

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Enter email you registered with");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pb_login.setVisibility(View.VISIBLE);
                if(isEmailValid(input.getText().toString())){

                    sendPasswordResetEmail(input.getText().toString());
                }
                else
                    pb_login.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void sendPasswordResetEmail(String email){//With snackbar
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pb_login.setVisibility(View.GONE);
                    new SnackTwo().greenSnack(LoginActivity.this, "Reset email will arrive within 1 minute.");
                }
                else if(task.isComplete()){
                    pb_login.setVisibility(View.GONE);
                    new SnackTwo().redSnack(LoginActivity.this, "Hmmm, not sure what happened. \nCheck your emails.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb_login.setVisibility(View.GONE);
                new SnackTwo().redSnack(LoginActivity.this, e.getMessage());
            }
        });
    }

    private boolean isEmailValid(String email){
        email.trim();

        if(email.isEmpty()){
            new SnackTwo().redSnack(LoginActivity.this, "Field empty\nEnter you email address");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            new SnackTwo().redSnack(LoginActivity.this, "Entered email is not valid");
            return false;
        }
        return true;
    }

    public void getUserDetailsFirebase(){

        firebaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserDetail.currentUserId);

        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    UserDetail userDetail = new UserDetail();

                    userDetail.setEmail(snapshot.child("email").getValue().toString());
                    userDetail.setFavouriteLandmark(snapshot.child("favouriteLandmark").getValue().toString());
                    userDetail.setPreferredLandmarkType(snapshot.child("preferredLandmarkType").getValue().toString());
                    userDetail.setIsMetric(Boolean.parseBoolean(snapshot.child("isMetric").getValue().toString()));

                    userDetail.setUserSessionDetails(userDetail);
                }
                else{
                    new SnackTwo().redSnack(LoginActivity.this, "Could not get your details");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new SnackTwo().redSnack(LoginActivity.this, error.getMessage());
            }
        });
    }

    private Boolean areInputsValid(){//!!! UserDetail.getPreferredLandmarkType is used as holder
        final String email = et_email.getEditText().getText().toString().trim();
        final String password = et_password.getEditText().getText().toString().trim();

        if(email.isEmpty()){
            et_email.setError("Enter your email!");
            et_email.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            new SnackTwo().greenSnack(this, email);
            et_email.setError("Your email address is not valid!");
            et_email.requestFocus();
            return false;
        }

        if(password.isEmpty()){
            et_password.setError("Enter your password!");
            et_password.requestFocus();
            return false;
        }

        if(password.length() < 6){
            et_password.setError("Min password length is 6!");
            et_password.requestFocus();
            return false;
        }

        return true;
    }














    private void initializePallets(){
        //Temp
        tv_vanguard = findViewById(R.id.login_tv_vanguard);
        tv_vanguard.setOnClickListener(this);

        tv_forgotPassword = findViewById(R.id.login_tv_forgotPassword);
        tv_forgotPassword.setOnClickListener(this);

        btn_login = findViewById(R.id.login_btn_login);
        btn_login.setOnClickListener(this);

        btn_register = findViewById(R.id.login_btn_register);
        btn_register.setOnClickListener(this);

        et_email = findViewById(R.id.login_et_email);
        et_email.setOnClickListener(this);

        et_password= findViewById(R.id.login_et_password);
        et_password.setOnClickListener(this);

        pb_login = findViewById(R.id.login_pb_login);
    }
}


