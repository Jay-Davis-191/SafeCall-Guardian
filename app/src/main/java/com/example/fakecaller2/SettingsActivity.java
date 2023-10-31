package com.example.fakecaller2;

import androidx.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {

    public static int SETTINGS_REQUEST = 1;
    private EditText phoneInput;
    private SharedPreferences preferences;
    private int phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        phoneInput = findViewById(R.id.phone);  // Object that refers to the phone EditText widget.
    }


    /**
     * The homeClicked method runs once the 'Home' button is clicked.
     * This moves the app to the MainActivity.
     */
    public void homeClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);  // Transfers to the Home page.
    }

    /***
     * onActivityResult method runs once the user moves to the Settings page and retrieves the
     * value of "phone".
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.HOME_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String updated_num;
                    updated_num = data.getStringExtra("phone");
                    phoneNumber = Integer.parseInt(updated_num);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        phoneNumber = preferences.getInt("number", 0);
        phoneInput.setText(String.valueOf(phoneNumber));
    }


    @Override
    protected void onPause() {
        if (phoneInput.getText().toString().isEmpty() || phoneInput.getText().toString() == "") {
            preferences.edit().clear().putInt("number", 0).apply();
        }
        else {
            preferences.edit().clear().putInt("number", Integer.parseInt(phoneInput.getText().toString())).apply();
        }
        super.onPause();
    }
}  // ends SettingsActivity
