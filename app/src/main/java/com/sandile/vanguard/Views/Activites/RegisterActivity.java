package com.sandile.vanguard.Views.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.model.PlaceType;
import com.sandile.vanguard.Phone.Keyboard;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.opencensus.internal.StringUtils;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private Button btn_register;
    private Switch switch_measurementSystem;
    private EditText et_email, et_confirmPassword, et_password, et_favouriteLandmark, et_preferredLandmarkType;
    private ProgressBar pb_registering;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializePallets();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_register:
                registerNewUser();
                break;
        }
    }

    private void registerNewUser() {//  1
        Keyboard.hideKeyboard(this);
        pb_registering.setVisibility(View.VISIBLE);
        Pair<Boolean, UserDetail> pair = isInputValid();
        if(pair.first){
            String tempPassword = et_password.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(pair.second.getEmail(), tempPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Testing password in onComplete:", tempPassword);

                            if(task.isSuccessful()){
                                UserDetail.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                saveUserToFirebase(pair.second);
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

    private void saveUserToFirebase(UserDetail userDetail){//   2
        FirebaseDatabase.getInstance().getReference("users")
                .child(userDetail.currentUserId)
                .setValue(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
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

    private Pair<Boolean, UserDetail> isInputValid(){
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
            return new Pair(false, null);
        }
        if(preferredLandmarkType.isEmpty()){
            et_preferredLandmarkType.setError("Enter your preferred landmark type!");
            et_preferredLandmarkType.requestFocus();
            return new Pair(false, null);
        }
        if(favouriteLandmark.isEmpty()){
            et_favouriteLandmark.setError("Enter your favourite landmark!");
            et_favouriteLandmark.requestFocus();
            return new Pair(false, null);
        }
        if(password.isEmpty()){
            et_password.setError("Enter your password!");
            et_password.requestFocus();
            return new Pair(false, null);
        }

        if(confirmPassword.isEmpty()){
            et_confirmPassword.setError("Confirm your password!");
            et_confirmPassword.requestFocus();
            return new Pair(false, null);
        }

        //is empty end, logic validation next
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Your email address is not valid!");
            et_email.requestFocus();
            return new Pair(false, null);
        }

        if(password.length() < 6){
            et_password.setError("Min password length is 6!");
            et_password.requestFocus();
            return new Pair(false, null);
        }

        if(!confirmPassword.equals(password)){
            et_confirmPassword.setError("Your passwords do not match!");
            et_confirmPassword.requestFocus();
            return new Pair(false, null);
        }

        UserDetail userDetailReturn = new UserDetail(email, favouriteLandmark, preferredLandmarkType, isMetric);
        return new Pair(true, userDetailReturn);
    }

    private void initializePallets(){
        mAuth = FirebaseAuth.getInstance();

        et_email = findViewById(R.id.register_et_email);
        et_confirmPassword = findViewById(R.id.register_et_password2);
        et_password = findViewById(R.id.register_et_password);
        et_favouriteLandmark = findViewById(R.id.register_et_favouriteLandmark);

        et_preferredLandmarkType = findViewById(R.id.register_et_preferredLandmarkType);
        et_preferredLandmarkType.setFocusable(false);
        et_preferredLandmarkType.setCursorVisible(false);
        et_preferredLandmarkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeTypeListDialog();
            }
        });

        pb_registering = findViewById(R.id.register_pb_registering);
        switch_measurementSystem = findViewById(R.id.register_switch_MeasurementSystem);
        switch_measurementSystem.setOnClickListener(this);
        btn_register = findViewById(R.id.register_btn_register);
        btn_register.setOnClickListener(this);
    }

    private void placeTypeListDialog(){
        ListView placeTypeListView = new ListView(this);

        List<String> placeTypeData = new ArrayList<String>();

        for(int i = 0; i < Arrays.stream(PlaceType.values()).count(); i++){
            String s1 = PlaceType.values()[i].toString();
            String replaceString =s1.replace('_',' ');

            replaceString = replaceString.substring(0,1).toUpperCase() + replaceString.substring(1).toLowerCase();

            placeTypeData.add(replaceString);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placeTypeData);
        placeTypeListView.setAdapter(arrayAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(placeTypeListView);
        builder.create();
        AlertDialog dialog =  builder.show();
        dialog.show();

        placeTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//When user clicks on item
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_preferredLandmarkType.setText(PlaceType.values()[position].toString());
                dialog.dismiss();
            }
        });

    }

}