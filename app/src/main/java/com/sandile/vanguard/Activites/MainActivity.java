package com.sandile.vanguard.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new SnackTwo().greenSnack(MainActivity.this, "Welcome!");


    }
}