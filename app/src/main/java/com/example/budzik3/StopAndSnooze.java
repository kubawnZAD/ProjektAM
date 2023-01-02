package com.example.budzik3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class StopAndSnooze extends AppCompatActivity {
    Calendar calendar = Calendar.getInstance();
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_and_snooze);
        Button StopAlarm = (Button)findViewById(R.id.Stop);
        Button SnoozeAlarm = (Button)findViewById(R.id.Snooze);
        StopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmReceiver.stopRingtone();

            }
        });
        SnoozeAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    AlarmReceiver.stopRingtone();
                    alarmManager.cancel(pendingIntent);
                    calendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR);
                    calendar.set(Calendar.MINUTE, Calendar.MINUTE + 5);
                    Intent intent = new Intent(getApplicationContext(), StopAndSnooze.class);
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 2000, pendingIntent);
                }
                catch (Exception e){

                }
            }
        });


    }
}