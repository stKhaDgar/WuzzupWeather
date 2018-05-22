package com.example.stmak.wuzzupweather;

import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // set background/icon from time
    private ConstraintLayout mainLayout;
    private ImageView iconDayTaime;

    // Weather members
    TextView currentTemperatureField;
    TextView currentCityField;
    TextView currentCountryField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // change bg/icon from time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat timeformat = new SimpleDateFormat("HH");
        Integer currentTime = 13; //Integer.parseInt(timeformat.format(c.getTime()));     <---- CHANGE IT
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
        else if((currentTime >= 21 && currentTime <= 24) || (currentTime >= 0 && currentTime < 5)){
            mainLayout.setBackground(getDrawable(R.drawable.night_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.night_icon));
        }

        currentTemperatureField = (TextView)findViewById(R.id.current_temperature);
        currentCityField = (TextView)findViewById(R.id.current_city);
        currentCountryField = (TextView)findViewById(R.id.current_country);

        WeatherFunction.placeIdTask asyncTask =new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            public void processFinish(String weather_temperature, String city, String country) {

                currentTemperatureField.setText(weather_temperature);
                currentCityField.setText(city);
                currentCountryField.setText(country);

            }
        });
        asyncTask.execute("48.45", "34.9833"); //  asyncTask.execute("Latitude", "Longitude")

    }
}
