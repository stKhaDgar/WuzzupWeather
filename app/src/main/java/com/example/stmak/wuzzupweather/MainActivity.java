package com.example.stmak.wuzzupweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView text_gradus;
    private ProgressBar loadBar;

    // Change City
    private ImageView changeIcon;
    private AutoCompleteTextView changeCityEdit;
    private TextView errorText;
    private Button buttonAccept;
    private String[] Cities;

    // Weather members
    private TextView currentTemperatureField;
    private TextView currentCountryField;
    private TextView temp_now;
    private TextView temp_tomorrow;
    private TextView temp_after_tomorrow;

    // Animations
    private Animation animationRotationCenter;
    private Animation animTemperature;
    private Animation animCurrentCountry;
    private Animation animProgressBarStart;
    private ImageView iconDayTaime;

    // Save last city
    final String SAVED_TEXT = "";
    SharedPreferences sPrefCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        addListener();

        // First layout (layout1) full Screen
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHigh = size.y;

        LinearLayout layout = findViewById(R.id.layout1);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = screenHigh;

        // change bg/icon from time
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeformat = new SimpleDateFormat("HH");
        int currentTime = Integer.parseInt(timeformat.format(c.getTime()));
        changesFromCurrentTime(currentTime);

        setActiveACTextView(changeCityEdit, false);
        changeCityEdit.clearFocus();
        loadText();

        animationsFromStart();
    }

    // Animations from start App
    public void animationsFromStart () {
        Animation animIconBack = AnimationUtils.loadAnimation(this, R.anim.slide_from_left_anim);
        Animation animIconChange = AnimationUtils.loadAnimation(this, R.anim.slide_from_right_anim);
        Animation animCurrentCity = AnimationUtils.loadAnimation(this, R.anim.current_city_anim);
        animCurrentCountry = AnimationUtils.loadAnimation(this, R.anim.current_country_anim);
        Animation animIconDayTime = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom_anim);

        findViewById(R.id.arrow_back_icon).startAnimation(animIconBack);
        changeIcon.startAnimation(animIconChange);
        changeCityEdit.startAnimation(animCurrentCity);
        currentCountryField.startAnimation(animCurrentCountry);
        iconDayTaime.startAnimation(animIconDayTime);
    }

    // Change from time
    public void changesFromCurrentTime(int currentTime){
        ConstraintLayout mainLayout = findViewById(R.id.main_layout);
        TextView text_now = findViewById(R.id.text_now);
        iconDayTaime = findViewById(R.id.icon_day_time);
        temp_now = findViewById(R.id.temp_now);
        loadBar = findViewById(R.id.load);
        loadBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#99ffffff"), android.graphics.PorterDuff.Mode.MULTIPLY);
        if(currentTime >= 5 && currentTime < 12){
            mainLayout.setBackground(getDrawable(R.drawable.morning_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.morning_icon));
            text_now.setTextColor(Color.parseColor("#ea607e"));
            temp_now.setTextColor(Color.parseColor("#ea607e"));
        }
        else if(currentTime >= 12 && currentTime < 21 ){
            mainLayout.setBackground(getDrawable(R.drawable.day_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.day_icon));
            text_now.setTextColor(Color.parseColor("#46cbf7"));
            temp_now.setTextColor(Color.parseColor("#46cbf7"));
        }
        else if((currentTime >= 21 && currentTime <= 24) || (currentTime >= 0 && currentTime < 5)){
            mainLayout.setBackground(getDrawable(R.drawable.night_background));
            iconDayTaime.setBackground(getDrawable(R.drawable.night_icon));
            text_now.setTextColor(Color.parseColor("#b056b8"));
            temp_now.setTextColor(Color.parseColor("#b056b8"));
        }
    }

    public void addListener(){
        ImageView arrowBack = findViewById(R.id.arrow_back_icon);
        arrowBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        // Change city
        Cities = getResources().getStringArray(R.array.city_array);
        errorText = findViewById(R.id.text_error);
        changeCityEdit = findViewById(R.id.Edit_change_city);
        text_gradus = findViewById(R.id.text_gradus);
        buttonAccept = findViewById(R.id.button_accept_change);

        List<String> cityList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.city_array)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, cityList);
        changeCityEdit.setAdapter(adapter);

        changeIcon = findViewById(R.id.change_icon);
        changeIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonAccept.setVisibility(View.VISIBLE);
                        currentCountryField.clearAnimation();
                        currentCountryField.setVisibility(View.INVISIBLE);
                        currentTemperatureField.clearAnimation();
                        currentTemperatureField.setVisibility(View.INVISIBLE);
                        text_gradus.clearAnimation();
                        text_gradus.setVisibility(View.INVISIBLE);
                        changeIcon.setClickable(false);
                        loadBar.setVisibility(View.INVISIBLE);
                        changeIcon.startAnimation(animationRotationCenter);

                        setActiveACTextView(changeCityEdit, true);
                        changeCityEdit.setSelectAllOnFocus(true);
                        changeCityEdit.clearFocus();
                        changeCityEdit.requestFocus();

                        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputMethodManager != null) {
                            inputMethodManager.toggleSoftInputFromWindow(changeCityEdit.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                        }
                    }
                }
        );

        changeCityEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        errorText.setVisibility(View.INVISIBLE);
                        changeCityEdit.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorNormal));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        buttonAccept.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String city = changeCityEdit.getText().toString();
                        if(checkCity(city)){
                            View view = MainActivity.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                            changeIcon.setClickable(true);
                            changeIcon.clearAnimation();
                            buttonAccept.setVisibility(View.INVISIBLE);

                            // set noActive to changeCityEdit
                            setActiveACTextView(changeCityEdit, false);

                            errorText.setVisibility(View.INVISIBLE);

                            loadBar.startAnimation(animProgressBarStart);
                            loadBar.setVisibility(View.VISIBLE);

                            // Change
                            changeCity(changeCityEdit.getText().toString());
                        }
                        else{
                            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.error_et);
                            changeCityEdit.startAnimation(anim);
                            errorText.setVisibility(View.VISIBLE);
                            changeCityEdit.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorError));
                        }

                    }
                }
        );
    }

    public void setActiveACTextView(AutoCompleteTextView view, boolean bool) {
        if(bool) {
            view.setClickable(true);
            view.setBackground(getDrawable(R.drawable.item_transparent_border_city));
            view.setEnabled(true);
            view.setCursorVisible(true);
            view.setSelectAllOnFocus(true);
            view.setDropDownHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        else {
            view.setClickable(false);
            view.setDropDownHeight(0);
            view.setEnabled(false);
            view.setCursorVisible(false);
            view.setSelectAllOnFocus(false);
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // Shared Preferences
    private void saveText() {
        sPrefCity = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPrefCity.edit();
        ed.putString(SAVED_TEXT, changeCityEdit.getText().toString() + "; " + currentCountryField.getText().toString());
        ed.apply();
    }
    private void loadText() {
        sPrefCity = getPreferences(MODE_PRIVATE);
        String savedTextCity= sPrefCity.getString(SAVED_TEXT, "");
        if(savedTextCity.length() == 0){
            changeCity("Kiev");
            changeCityEdit.setText(R.string.default_city);
            currentCountryField.setText(R.string.default_country);
        } else {
            String[] ct = savedTextCity.trim().split("[;]");

            changeCity(ct[0]);
            changeCityEdit.setText(ct[0]);
            currentCountryField.setText(ct[1]);
        }
    }

    // TODO: DO something with that функция чтобы проверять города по dataBase
    public boolean checkCity (String city){
        for (String City : Cities) {
            if (City.equals(city))
                return true;
        }
        return false;
    }

    public void changeCity(String city){
        // start animation
        animationRotationCenter = AnimationUtils.loadAnimation(this, R.anim.rotate_change_icon);
        animTemperature = AnimationUtils.loadAnimation(this, R.anim.alpha_temperature_anim);
        animProgressBarStart = AnimationUtils.loadAnimation(this, R.anim.progress_bar_start_anim);
        loadBar.startAnimation(animProgressBarStart);

        currentTemperatureField = findViewById(R.id.current_temperature);
        currentCountryField = findViewById(R.id.current_country);
        temp_tomorrow = findViewById(R.id.temp_tomorrow);
        temp_after_tomorrow = findViewById(R.id.temp_after_tomorrow);
        WeatherFunction.placeIdTask asyncTask;
        asyncTask = new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            public void processFinish(String city, String country, String weather_temperature_now, String weather_temperature_tomorrow,
                                      String weather_temperature_after_tomorrow, String date) {
                currentTemperatureField.setText(weather_temperature_now);
                temp_now.setText(weather_temperature_now + getString(R.string.temperature_gradus));
                temp_tomorrow.setText(weather_temperature_tomorrow + getString(R.string.temperature_gradus));
                temp_after_tomorrow.setText(weather_temperature_after_tomorrow + getString(R.string.temperature_gradus));
                changeCityEdit.setText(city);
                currentCountryField.setText(country);
                changeIcon.setClickable(true);
                loadBar.setVisibility(View.INVISIBLE);
                currentCountryField.setVisibility(View.VISIBLE);
                currentTemperatureField.setVisibility(View.VISIBLE);
                text_gradus.setVisibility(View.VISIBLE);

                TextView txt_date = findViewById(R.id.date_txt);
                txt_date.setText(date);

                saveText();

                currentCountryField.startAnimation(animCurrentCountry);
                currentTemperatureField.startAnimation(animTemperature); text_gradus.startAnimation(animTemperature);
            }
        });

        asyncTask.execute(city);
    }

    // TODO: finished this idea
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
