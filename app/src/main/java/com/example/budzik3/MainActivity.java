package com.example.budzik3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
//    SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
//    SharedPreferences.Editor editor = sharedPreferences.edit();
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    int mHour;
    int mMinute;
    ConstraintLayout layout;
    Switch previousView = null;
    Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Button button = (Button) findViewById(R.id.button);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        createNotificationChannel();

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String hourString, minuteString;

                        if(Integer.toString(hour).length()==1){
                            hourString = "0"+Integer.toString(hour);
                        }
                        else{
                            hourString = Integer.toString(hour);
                        }
                        if(Integer.toString(minute).length()==1){
                            minuteString = "0"+Integer.toString(minute);
                        }
                        else{
                            minuteString = Integer.toString(minute);
                        }

                        Switch switch1 = new Switch(MainActivity.this);
                        switch1.setText(hourString + ":" + minuteString);
                        switch1.setTextColor(Color.parseColor("#9E9898"));
                        switch1.setTextSize(34);
                        switch1.setWidth(1000);
                        switch1.setHeight(200);
                        switch1.setOnCheckedChangeListener(MainActivity.this::onCheckedChanged);
                        switch1.setOnLongClickListener(MainActivity.this::onLongClick);

                        // Assign a unique id to the new switch view
                        switch1.setId(View.generateViewId());

                        ConstraintLayout layout = findViewById(R.id.layout);
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );

//                        editor.putString("name",String.valueOf(switch1.getText()));
//                        editor.apply();
                        layout.addView(switch1, params);

                        // Update the constraints for the new view
                        if (previousView == null) {
                            params.topToTop = layout.getId();
                        } else {
                            params.topToBottom = previousView.getId();
                        }
                        params.leftToLeft = layout.getId();
                        params.rightToRight = layout.getId();

                        layout.updateViewLayout(switch1, params);

                        previousView = switch1;
                    }
                }, mHour, mMinute, true);

                timePickerDialog.show();
            }
        });
    }

        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b) {


                long time;
                CharSequence charSequence = compoundButton.getText();
                String[] time1 = charSequence.toString().split(":");

                compoundButton.setTextColor(Color.parseColor("#000000"));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time1[0]));
                calendar.set(Calendar.MINUTE, Integer.valueOf(time1[1]));
                Toast.makeText(MainActivity.this, time1[0]+":"+ time1[1], Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 2000, pendingIntent);



            }
            else{
                compoundButton.setTextColor(Color.parseColor("#9E9898"));
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

                // Stop the ringtone
                AlarmReceiver.stopRingtone();
            }
        }
    private void createNotificationChannel(){
        CharSequence name = "ReminderChannel";
        String description = "desc";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("notify", name,importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.cancelAll();

    }
    public boolean onLongClick(View v) {

        // Create a new AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setMessage("Do you want to delete?")
                .setTitle("Delete");


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ViewGroup parent = (ViewGroup) v.getParent();
                parent.removeView(v);
                dialog.dismiss();
                try {
                    alarmManager.cancel(pendingIntent);
                    AlarmReceiver.stopRingtone();
                }
                catch (Exception e){

                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });


// Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();



        return true;
    }


}