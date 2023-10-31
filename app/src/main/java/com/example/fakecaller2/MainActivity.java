package com.example.fakecaller2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    private static final int REQUEST_CALL = 1;
    private final String[] TIMES = {"Now", "1", "2", "5", "10", "20", "30"};
    private int delay;
    private Spinner timeSpinner;
    private TextView scheduledTimeText;
    private String scheduledTime;
    private Phone phone;
    public static int HOME_REQUEST = 1;
    private SharedPreferences preferences;
    private int phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> spinnerValues;

        scheduledTimeText = findViewById(R.id.reminder);
        timeSpinner = findViewById(R.id.schedule);

        // Sets the design for the Spinner object and the Spinner items
        spinnerValues = new ArrayAdapter<>(this, R.layout.spinner_design, TIMES);
        spinnerValues.setDropDownViewResource(R.layout.spinner_items_design);

        timeSpinner.setAdapter(spinnerValues);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        phoneNumber = preferences.getInt("number", 0);
    }

    /***
     * scheduleClicked runs when the 'Scheduled Call' button is clicked.
     * At the end of this method, the current time and value of the timeSpinner is retrieved, the
     * provided phone number is verified, and then the phone call is made after waiting the
     * necessary amount of time.
     **/
    public void scheduleClicked(View view) {
        getSpinnerValue();
        retrieveCurrentTime();

        try {
            delay *= 60000; // Converts from minutes to microseconds.

            if(verifyPhone()) {
                scheduledTimeText.setText("The call has been scheduled for " + scheduledTime); // Prints the scheduled time to the user.
                Handler handler = new Handler();
                handler.postDelayed(this::makePhoneCall, delay);
            }
        } catch (Exception ex) {
            Thread.currentThread().interrupt();
        }
    }  // ends scheduleClicked


    /***
     * The getSpinnerValue method retrieves the current value of timeSpinner which represents the
     * number of minutes the user has to wait before the phone call is made.
     **/
    public void getSpinnerValue() {
        if (timeSpinner.getSelectedItem().equals("Now")) {
            delay = 0; // Call will be made instantly
        }
        else {
            delay = Integer.parseInt(timeSpinner.getSelectedItem().toString());
        }
    }  // ends getSpinnerValue


    /***
     * retrieveCurrentTime obtains the current time from the phone and then adds the selected number
     * of minutes based on the delay variable.
     * This method formats the time in the 12-hour system. 
     **/
    public void retrieveCurrentTime() {
        try {
            Date date;
            Calendar currentTime;
            SimpleDateFormat timeFormat;

            currentTime = Calendar.getInstance();  // Obtains the current date and time.
            timeFormat = new SimpleDateFormat("hh:mm a");  // Specifies the required format.
            scheduledTime = timeFormat.format(currentTime.getTime());  // Converts the time in 12-hour format.

            date = timeFormat.parse(scheduledTime);
            currentTime.setTime(date);
            currentTime.add(Calendar.MINUTE, delay);  // Adds the delay to the current time.
            scheduledTime = timeFormat.format(currentTime.getTime());  // Converts the time in 12-hour format.


        } catch (ParseException ex) {  // Exception in case of any issues with extracting the necessary information from the scheduledTime variable.
            Thread.currentThread().interrupt();
        }
    }  // ends retrieveCurrentTime


    /***
     * verifyPhone method is responsible for checking if the phone number is valid. Returns a
     * boolean value depending on the validity, and alerts the user if necessary.
     * This method retrieves the provided phone number from the Settings page.
     **/
    public boolean verifyPhone() {
        if (String.valueOf(phoneNumber).equals("")) {
            Toast.makeText(MainActivity.this, "Enter a phone number in Settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }  // ends verifyPhone


    /***
     * makePhoneCall method creates the phone call to the user's mobile and displays the fake
     * incoming phone number.
     **/
    private void makePhoneCall() {
        phone = new Phone();  // Creates a new Phone object.
        phone.setNum(phoneNumber);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        else {
            String dial = "tel:" + phone.getNum();
            startActivity(new Intent(Intent.ACTION_CALL, (Uri.parse(dial))));
        }
    }  // ends makePhoneCall


    /***
     * The onRequestPermissionsResult method checks if the permissions are met, and then makes
     * the phone call if permission is granted.
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /***
     * The settingsClicked method solely runs when the Settings button is clicked. This method
     * closes the Home page activity and moves to the Settings page activity.
     **/
    public void settingsClicked(View view) {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivityForResult(intent, SettingsActivity.SETTINGS_REQUEST);
    }  // ends settingsClicked


    /***
     * onActivityResult method runs once the user moves to the Home page and retrieves the
     * value of "phone" to assign it as the phone number.
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SettingsActivity.SETTINGS_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String updated_num;
                    updated_num = data.getStringExtra("phone");  // Retrieves the value of phone_number
                    phone.setNum(Integer.parseInt(updated_num));  // Sets value in the phone class to updated_num
                }
            }
        }
    }  // ends onActivityResult

}  // ends MainActivity
