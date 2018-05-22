package com.example.stmak.wuzzupweather;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // set background/icon from time
    private ConstraintLayout mainLayout;
    private ImageView iconDayTaime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // change bg/icon from time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat timeformat = new SimpleDateFormat("HH");
        Integer currentTime = Integer.parseInt(timeformat.format(c.getTime()));
        mainLayout = (ConstraintLayout)findViewById(R.id.main_layout);
        iconDayTaime = (ImageView)findViewById(R.id.icon_day_time);
        if(currentTime >= 5 && currentTime < 12){
            mainLayout.setBackground(getDrawable(R.drawable.morning_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.morning_icon));
        }
        else if(currentTime >= 12 && currentTime < 21 ){
            mainLayout.setBackground(getDrawable(R.drawable.day_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.day_icon));
        }
        else if(currentTime >= 21 && currentTime < 5){
            mainLayout.setBackground(getDrawable(R.drawable.night_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.night_icon));
        }
    }
}
