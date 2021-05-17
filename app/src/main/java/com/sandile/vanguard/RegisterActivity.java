package com.sandile.vanguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private Switch switch_measurementSystem;
    private EditText et_email, et_confirmPassword, et_password, et_favouriteLandmark, et_preferredLandmarkType;
    private ProgressBar pb_registering;

    private FirebaseAuth mAuth;

    private static UserDetail userDetail = new UserDetail().userDetail();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initializing
        mAuth = mAuth.getInstance();

        et_email = findViewById(R.id.register_et_email);
        et_confirmPassword = findViewById(R.id.register_et_password2);
        et_password = findViewById(R.id.register_et_password);
        et_favouriteLandmark = findViewById(R.id.register_et_favouriteLandmark);
        et_preferredLandmarkType = findViewById(R.id.register_et_preferredLandmarkType);

        pb_registering = findViewById(R.id.register_pb_registering);
        switch_measurementSystem = findViewById(R.id.register_switch_MeasurementSystem);
        switch_measurementSystem.setOnClickListener(this);
        btn_register = findViewById(R.id.register_btn_register);
        btn_register.setOnClickListener(this);
        //end initialize



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//Sign up button
            case R.id.register_btn_register://register
                if(isInputValid()){
                    pb_registering.setVisibility(View.VISIBLE);
                    registerNewUser(userDetail, this);
                    pb_registering.setVisibility(View.GONE);
                }
                //startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void registerNewUser(UserDetail userDetail, Activity activity){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(userDetail.getEmail(), userDetail.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {// Sign in success, update UI with the signed-in user's information
                            new ChocoManager().greenSnack(activity, "Welcome!");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        }
                        else {// If sign in fails, display a message to the user.
                            new ChocoManager().redSnack(activity, "Authentication failed.");
                        }

                    }
                });
    }

    private void createProfileInFirebase(){

    }

    private Boolean isInputValid(){
        final String email = et_email.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        final String confirmPassword = et_confirmPassword.getText().toString().trim();
        final String favouriteLandmark = et_favouriteLandmark.getText().toString().trim();
        final String preferredLandmarkType = et_preferredLandmarkType.getText().toString().trim();
        final Boolean isMetric = switch_measurementSystem.isChecked();

        Log.i("Measurement system", isMetric.toString());

        //is empty
        if(email.isEmpty()){
            et_email.setError("Enter your email address!");
            et_email.requestFocus();
            return false;
        }
        if(password.isEmpty()){
            et_password.setError("Enter your password!");
            et_password.requestFocus();
            return false;
        }

        if(confirmPassword.isEmpty()){
            et_confirmPassword.setError("Confirm your password!");
            et_confirmPassword.requestFocus();
            return false;
        }

        if(favouriteLandmark.isEmpty()){
            et_favouriteLandmark.setError("Enter your favourite landmark!");
            et_favouriteLandmark.requestFocus();
            return false;
        }

        if(preferredLandmarkType.isEmpty()){
            et_preferredLandmarkType.setError("Enter your preferred landmark type!");
            et_preferredLandmarkType.requestFocus();
            return false;
        }
        //is empty end, logic validation next
        if(password.length() < 8){
            et_password.setError("Min password length is 8!");
            et_password.requestFocus();
            return false;
        }

        if(!confirmPassword.equals(password)){
            et_confirmPassword.setError("Your passwords do not match!");
            et_confirmPassword.requestFocus();
            return  false;
        }

        userDetail.setEmail(email);
        userDetail.setPassword(password);
        userDetail.setFavouriteLandmark(favouriteLandmark);
        userDetail.setPreferredLandmarkType(preferredLandmarkType);
        userDetail.setIsMetric(isMetric);
        return true;
    }
}