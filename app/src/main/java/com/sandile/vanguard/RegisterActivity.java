package com.sandile.vanguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pd.chocobar.ChocoBar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private Switch switch_measurementSystem;
    private EditText et_email, et_confirmPassword, et_password, et_favouriteLandmark, et_preferredLandmarkType;
    private ProgressBar pb_registering;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initializing pallets
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
        //initializing pallets



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//Sign up button
            case R.id.register_btn_register://register
                if(isInputValid()){
                    pb_registering.setVisibility(View.VISIBLE);
                }
                //startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void registerNewUser(String email, String password, Activity activity){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            new ChocoBarMan().greenSnack(activity, "Welcome!");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //updateUI(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            new ChocoBarMan().redSnack(activity, "Authentication failed.");
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
        return true;
    }
}