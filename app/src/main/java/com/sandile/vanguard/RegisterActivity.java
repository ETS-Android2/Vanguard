package com.sandile.vanguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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
        mAuth = FirebaseAuth.getInstance();

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
                registerNewUser();
                break;
        }
    }

    private void registerNewUser() {
        pb_registering.setVisibility(View.VISIBLE);
        if(isInputValid()){
            mAuth.createUserWithEmailAndPassword(userDetail.getEmail(), userDetail.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pb_registering.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                userDetail.setEmail(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pb_registering.setVisibility(View.GONE);
                    new SnackTwo().redSnack(RegisterActivity.this, e.getMessage());
                }
            });
        }
        else
            pb_registering.setVisibility(View.GONE);
    }

    private void saveUserToFirebase(UserDetail userDetail){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userDetail.getId())
                .setValue(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pb_registering.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    new SnackTwo().greenSnack(RegisterActivity.this, "Account created successfully!");
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackTwo().redSnack(RegisterActivity.this, e.getMessage());
            }
        });
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
        if(preferredLandmarkType.isEmpty()){
            et_preferredLandmarkType.setError("Enter your preferred landmark type!");
            et_preferredLandmarkType.requestFocus();
            return false;
        }
        if(favouriteLandmark.isEmpty()){
            et_favouriteLandmark.setError("Enter your favourite landmark!");
            et_favouriteLandmark.requestFocus();
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

        //is empty end, logic validation next
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Your email address is not valid!");
            et_email.requestFocus();
            return false;
        }

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