package com.tuxskar.caluma;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void logOut(View view) {
        Toast.makeText(this.getApplicationContext(), "Logging out", Toast.LENGTH_SHORT).show();
        // TODO: clean information:
        //         in shared preferences: subjects, user, token, password, etc
        //         Start Login activity closing all the back stack
    }
}
