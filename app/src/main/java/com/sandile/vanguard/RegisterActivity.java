package com.sandile.vanguard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button register;
    private Switch measurementSystem;
    private EditText email, confirmPassword, password, favouriteLandmarkLocation, preferredLandmarkType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //initializing pallets
        email = findViewById(R.id.register_et_email);
        confirmPassword = findViewById(R.id.register_et_password2);
        password = findViewById(R.id.register_et_password);
        favouriteLandmarkLocation = findViewById(R.id.register_et_favouriteLandmark);
        preferredLandmarkType = findViewById(R.id.register_et_preferredLandmarkType);

        measurementSystem = findViewById(R.id.register_switch_MeasurementSystem);
        measurementSystem.setOnClickListener(this);
        register = findViewById(R.id.register_btn_register);
        register.setOnClickListener(this);

        //initializing pallets
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//Sign up button
            case R.id.register_switch_MeasurementSystem://register
                measurementSystemLogic();
                break;
            case R.id.login_btn_register://register
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void measurementSystemLogic() {
        final String offText = "Metric";
        final String onText = "Imperial";

        if(measurementSystem.isChecked()){
            measurementSystem.setText(onText);
        }
        else{
            measurementSystem.setText(offText);
        }

    }

}