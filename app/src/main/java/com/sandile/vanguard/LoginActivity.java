package com.sandile.vanguard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, register;
    private TextView forgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing pallets
        forgotPassword = findViewById(R.id.login_tv_fogotPassword);
        forgotPassword.setOnClickListener(this);
        login = findViewById(R.id.login_btn_login);
        login.setOnClickListener(this);
        register = findViewById(R.id.login_btn_register);
        register.setOnClickListener(this);
        // initializing pallets
    }

    @Override
    public void onClick(View v) {//What happens when user clicks on..
        switch (v.getId()) {//Sign up button
            case R.id.login_btn_register://register
                //startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.login_btn_login://Login button
                LoginLogic();
                break;
            case R.id.login_tv_fogotPassword://Forgot password button
                forgotPasswordLogic();
                break;
        }

    }

    private void forgotPasswordLogic() {
        ShowInputDialog();
    }

    private void ShowInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter email you registed with");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this, "Ok has been pressed!", Toast.LENGTH_LONG).show();
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

    private void LoginLogic() {
        // login logic
    }
}


