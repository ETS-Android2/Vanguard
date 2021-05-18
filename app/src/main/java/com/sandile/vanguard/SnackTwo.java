package com.sandile.vanguard;

import android.app.Activity;

import com.google.android.material.snackbar.Snackbar;
import com.pd.chocobar.ChocoBar;

public class SnackTwo {
    //https://github.com/Pradyuman7/ChocoBar

     public void greenSnack(Activity activity, String message){
        ChocoBar.builder().setActivity(activity)
            .setText(message)
            .setDuration(ChocoBar.LENGTH_SHORT)
            .green()  // in built green ChocoBar
            .show();
     }

     public void redSnack(Activity activity, String message){
         ChocoBar.builder().setActivity(activity)
                 .setText(message)
                 .setDuration(ChocoBar.LENGTH_INDEFINITE)
                 .setActionText(android.R.string.ok)
                 .red()   // in built red ChocoBar
                 .show();
     }

     public void blackSnack(Activity activity, String message){
         ChocoBar.builder().setActivity(activity)
                 .setActionText(message)
                 .setDuration(ChocoBar.LENGTH_INDEFINITE)
                 .setActionText(android.R.string.ok)
                 .black()
                 .show();

     }
}
