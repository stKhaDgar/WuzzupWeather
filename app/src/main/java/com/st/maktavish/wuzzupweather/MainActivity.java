package com.st.maktavish.wuzzupweather;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
import android.widget.ListView;
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
    private Typeface weatherFont;

    // Change City
    private ImageView changeIcon;
    private AutoCompleteTextView changeCityEdit;
    private TextView errorText;
    private Button buttonAccept;
    private String[] Cities;

    // Weather members
    private TextView currentTemperatureField;
    private TextView currentCountryField;
    private TextView temp_now, temp_tomorrow, temp_after_tomorrow, tv_date_now, tv_date_tomorrow, tv_date_after_tomorrow;
    private LinearLayout today_list, tomorrow_list, aftertomorrow_list;
    private boolean isBigToday, isBigTomorrow, isBigAfterTomorrow;
    private int heightTL, countToday, heightTomL, countTomorrow, heightAfterTomL, countAfterTomorrow;

    // Icons weather
    private TextView weatherIconToday, weatherIconTomorrow, weatherIconAfterTomorrow;

    // Animations
    private Animation animationRotationCenter;
    private Animation animTemperature;
    private Animation animCurrentCountry;
    private Animation animProgressBarStart;
    private ImageView iconDayTaime;
    private final int DURATION = 800;

    // Save last city
    final String SAVED_TEXT = "";
    SharedPreferences sPrefCity;

    private TextView text_now, text_after_tomorrow, text_tomorrow;

    // List
    private ListView lvToday, lvTomorrow, lvAfterTomorrow;

    private NestedScrollView sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        addListener();
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");
        WeatherFunction.context = this;

        // First layout (layout1) full Screen
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHigh = size.y;
        LinearLayout layout = findViewById(R.id.layout1);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = screenHigh;

        // change bg/icon from time
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH");
        int currentTime = Integer.parseInt(timeFormat.format(c.getTime()));
        changesFromCurrentTime(currentTime);

        setActiveACTextView(changeCityEdit, false);
        changeCityEdit.clearFocus();
        loadText();

        animationsFromStart();

        findViewById(R.id.list_view_today).setEnabled(false);
        findViewById(R.id.list_view_tomorrow).setEnabled(false);
        findViewById(R.id.list_view_day_after_tomorrow).setEnabled(false);
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
        // Icons
        weatherIconToday = findViewById(R.id.icon_now);

        ConstraintLayout mainLayout = findViewById(R.id.main_layout);
        text_now = findViewById(R.id.text_now);
        text_tomorrow = findViewById(R.id.text_tomorrow);
        text_after_tomorrow = findViewById(R.id.text_day_after_tomorrow);
        iconDayTaime = findViewById(R.id.icon_day_time);
        temp_now = findViewById(R.id.temp_now);
        loadBar = findViewById(R.id.load);
        loadBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#99ffffff"), android.graphics.PorterDuff.Mode.MULTIPLY);
        if(currentTime >= 5 && currentTime < 12){
            mainLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.morning_background));
            iconDayTaime.setBackground(ContextCompat.getDrawable(this, R.drawable.morning_icon));
            text_now.setTextColor(Color.parseColor("#ea607e"));
            temp_now.setTextColor(Color.parseColor("#ea607e"));
            weatherIconToday.setTextColor(Color.parseColor("#ea607e"));
        }
        else if(currentTime >= 12 && currentTime < 21 ){
            mainLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.day_background));
            iconDayTaime.setBackground(ContextCompat.getDrawable(this, R.drawable.day_icon));
            text_now.setTextColor(Color.parseColor("#46cbf7"));
            temp_now.setTextColor(Color.parseColor("#46cbf7"));
            weatherIconToday.setTextColor(Color.parseColor("#46cbf7"));
        }
        else if((currentTime >= 21 && currentTime <= 24) || (currentTime >= 0 && currentTime < 5)){
            mainLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.night_background));
            iconDayTaime.setBackground(ContextCompat.getDrawable(this, R.drawable.night_icon));
            text_now.setTextColor(Color.parseColor("#b056b8"));
            temp_now.setTextColor(Color.parseColor("#b056b8"));
            weatherIconToday.setTextColor(Color.parseColor("#b056b8"));
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

                        if(isBigToday) {
                            hideElements(false, tv_date_now, weatherIconToday, temp_now, text_now);
                            today_list.getLayoutParams().height = 0;
                            today_list.requestLayout();
                            isBigToday = false;
                        }
                        if(isBigTomorrow) {
                            hideElements(false, tv_date_tomorrow, weatherIconTomorrow, temp_tomorrow, text_tomorrow);
                            tomorrow_list.getLayoutParams().height = 0;
                            tomorrow_list.requestLayout();
                            isBigTomorrow = false;
                        }
                        if(isBigAfterTomorrow){
                            hideElements(false, tv_date_after_tomorrow, weatherIconAfterTomorrow, temp_after_tomorrow, text_after_tomorrow);
                            aftertomorrow_list.getLayoutParams().height = 0;
                            aftertomorrow_list.requestLayout();
                            isBigAfterTomorrow = false;
                        }

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            changeCityEdit.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorSmallTransparent));
                        } else {
                            changeCityEdit.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorTransparent));
                        }
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
                            changeCityEdit.setText(changeCityEdit.getText().toString());
                            changeCity(changeCityEdit.getText().toString());
                        }
                        else{
                            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.error_et);
                            changeCityEdit.startAnimation(anim);
                            errorText.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                changeCityEdit.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorError));
                            } else {
                                changeCityEdit.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorError));
                            }
                        }

                    }
                }
        );
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
        tv_date_now = findViewById(R.id.date_now);
        tv_date_tomorrow = findViewById(R.id.date_tomorrow);
        tv_date_after_tomorrow = findViewById(R.id.date_after_tomorrow);

        weatherIconTomorrow = findViewById(R.id.icon_tomorrow);
        weatherIconAfterTomorrow = findViewById(R.id.icon_after_tomorrow);
        weatherIconToday.setTypeface(weatherFont);
        weatherIconTomorrow.setTypeface(weatherFont);
        weatherIconAfterTomorrow.setTypeface(weatherFont);

        WeatherFunction.placeIdTask asyncTask;
        asyncTask = new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            @SuppressLint("SetTextI18n")
            public void processFinish(String city, String country,
                                      String weather_temperature_now, String iconToday, String[] arrToday, String date_now,
                                      String weather_temperature_tomorrow, String iconTomorrow, String[] arrTomorrow, String date_tomorrow,
                                      String weather_temperature_after_tomorrow, String iconAfterTomorrow, String[] arrAfterTomorrow, String date_after_tomorrow) {
                currentTemperatureField.setText(weather_temperature_now);
                temp_now.setText(weather_temperature_now + getString(R.string.temperature_gradus));
                tv_date_now.setText(date_now);
                temp_tomorrow.setText(weather_temperature_tomorrow + getString(R.string.temperature_gradus));
                tv_date_tomorrow.setText(date_tomorrow);
                temp_after_tomorrow.setText(weather_temperature_after_tomorrow + getString(R.string.temperature_gradus));
                tv_date_after_tomorrow.setText(date_after_tomorrow);
                changeCityEdit.setText(city);
                currentCountryField.setText(country);
                changeIcon.setClickable(true);
                loadBar.setVisibility(View.INVISIBLE);
                currentCountryField.setVisibility(View.VISIBLE);
                currentTemperatureField.setVisibility(View.VISIBLE);
                text_gradus.setVisibility(View.VISIBLE);

                // Icons
                weatherIconToday.setText(iconToday);
                weatherIconTomorrow.setText(iconTomorrow);
                weatherIconAfterTomorrow.setText(iconAfterTomorrow);

                // List today
                lvToday = findViewById(R.id.list_view_today);
                lvToday.setAdapter(new MyListAdapter(MainActivity.this, arrToday));
                clickTodayList();

                // List tomorrow
                lvTomorrow = findViewById(R.id.list_view_tomorrow);
                lvTomorrow.setAdapter(new MyListAdapter(MainActivity.this, arrTomorrow));
                clickTomorrowList();

                // List tomorrow
                lvAfterTomorrow = findViewById(R.id.list_view_day_after_tomorrow);
                lvAfterTomorrow.setAdapter(new MyListAdapter(MainActivity.this, arrAfterTomorrow));
                clickAfterTomorrowList();

                lvToday.setVisibility(View.VISIBLE);
                lvTomorrow.setVisibility(View.VISIBLE);
                lvAfterTomorrow.setVisibility(View.VISIBLE);

                saveText();

                currentCountryField.startAnimation(animCurrentCountry);
                if(!currentTemperatureField.getText().toString().equals("")){
                    currentTemperatureField.startAnimation(animTemperature);
                }
                text_gradus.startAnimation(animTemperature);
            }
        });

        asyncTask.execute(city);
    }

    public boolean checkCity (String city){
        for (String City : Cities) {
            if (City.equals(city))
                return true;
        }
        return false;
    }

    public void setActiveACTextView(AutoCompleteTextView view, boolean bool) {
        if(bool) {
            view.setClickable(true);
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_change_city_drawable));
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
        if(savedTextCity != null && savedTextCity.length() == 0){
            changeCity("Kiev");
            changeCityEdit.setText(R.string.default_city);
            currentCountryField.setText(R.string.default_country);
        } else if (savedTextCity != null) {
            String[] ct = savedTextCity.trim().split("[;]");

            changeCity(ct[0]);
            changeCityEdit.setText(ct[0]);
            currentCountryField.setText(ct[1]);
        }
    }

    // animation items forecast
    public void clickTodayList() {
        LinearLayout today_layout = findViewById(R.id.today_layout);
        today_list = findViewById(R.id.today_list);
        today_list.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heightTL = today_list.getMeasuredHeight();
        isBigToday = false;

        countToday = lvToday.getCount();
        if(countToday > 0){
            today_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!isBigToday){

                                hideElements(true, tv_date_now, weatherIconToday, temp_now, text_now);

                                sc = findViewById(R.id.nestedScrollView);

                                v.setClickable(false);
                                ValueAnimator va = ValueAnimator.ofInt(today_list.getHeight(), heightTL * countToday);

                                va.setDuration(DURATION);

                                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        final Integer value = (Integer) animation.getAnimatedValue();
                                        today_list.getLayoutParams().height = value;
                                        today_list.requestLayout();

                                        sc.post(new Runnable() {
                                            public void run() {
                                                sc.scrollTo(0, sc.getScrollY() + value/10); // these are your x and y coordinates
                                            }
                                        });
                                    }
                                });
                                va.start();
                                isBigToday = true;

                                if(isBigTomorrow) {

                                    hideElements(false, tv_date_tomorrow, weatherIconTomorrow, temp_tomorrow, text_tomorrow);

                                    ValueAnimator va1 = ValueAnimator.ofInt(tomorrow_list.getHeight(), 0);
                                    va1.setDuration(DURATION);
                                    va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            tomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                            tomorrow_list.requestLayout();
                                        }
                                    });
                                    va1.start();
                                    isBigTomorrow = false;
                                }
                                if(isBigAfterTomorrow){

                                    hideElements(false, tv_date_after_tomorrow, weatherIconAfterTomorrow, temp_after_tomorrow, text_after_tomorrow);

                                    ValueAnimator va2 = ValueAnimator.ofInt(aftertomorrow_list.getHeight(), 0);
                                    va2.setDuration(DURATION);
                                    va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            aftertomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                            aftertomorrow_list.requestLayout();
                                        }
                                    });
                                    va2.start();
                                    isBigAfterTomorrow = false;
                                }
                            }
                            else{

                                hideElements(false, tv_date_now, weatherIconToday, temp_now, text_now);

                                ValueAnimator va = ValueAnimator.ofInt(today_list.getHeight(), 0);
                                va.setDuration(DURATION);
                                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        today_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        today_list.requestLayout();
                                    }
                                });
                                va.start();
                                isBigToday = false;
                            }
                            v.setClickable(true);
                        }
                    }
            );
        }

    }
    public void clickTomorrowList() {
        LinearLayout tomorrow_layout = findViewById(R.id.tomorrow_layout);
        tomorrow_list = findViewById(R.id.tomorrow_list);
        tomorrow_list.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heightTomL = tomorrow_list.getMeasuredHeight();
        isBigTomorrow = false;

        countTomorrow = lvTomorrow.getCount();

        tomorrow_layout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isBigTomorrow){

                            hideElements(true, tv_date_tomorrow, weatherIconTomorrow, temp_tomorrow, text_tomorrow);

                            sc = findViewById(R.id.nestedScrollView);

                            v.setClickable(false);
                            ValueAnimator va = ValueAnimator.ofInt(tomorrow_list.getHeight(), heightTomL * countTomorrow);

                            va.setDuration(DURATION);

                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    tomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                    tomorrow_list.requestLayout();
                                    sc.post(new Runnable() {
                                        public void run() {
                                            sc.scrollTo(0, sc.getBottom()); // these are your x and y coordinates
                                        }
                                    });
                                }
                            });
                            va.start();
                            isBigTomorrow = true;

                            if(isBigToday) {

                                hideElements(false, tv_date_now, weatherIconToday, temp_now, text_now);

                                ValueAnimator va1 = ValueAnimator.ofInt(today_list.getHeight(), 0);
                                va1.setDuration(DURATION);
                                va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        today_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        today_list.requestLayout();
                                    }
                                });
                                va1.start();
                                isBigToday = false;
                            }
                            if(isBigAfterTomorrow){

                                hideElements(false, tv_date_after_tomorrow, weatherIconAfterTomorrow, temp_after_tomorrow, text_after_tomorrow);

                                ValueAnimator va2 = ValueAnimator.ofInt(aftertomorrow_list.getHeight(), 0);
                                va2.setDuration(DURATION);
                                va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        aftertomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        aftertomorrow_list.requestLayout();
                                    }
                                });
                                va2.start();
                                isBigAfterTomorrow = false;
                            }
                        }
                        else{

                            hideElements(false, tv_date_tomorrow, weatherIconTomorrow, temp_tomorrow, text_tomorrow);

                            ValueAnimator va = ValueAnimator.ofInt(tomorrow_list.getHeight(), 0);
                            va.setDuration(DURATION);
                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    tomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                    tomorrow_list.requestLayout();
                                }
                            });
                            va.start();
                            isBigTomorrow = false;
                        }
                        v.setClickable(true);
                    }
                }
        );
    }
    public void clickAfterTomorrowList() {
        LinearLayout aftertomorrow_layout = findViewById(R.id.after_tomorrow_layout);
        aftertomorrow_list = findViewById(R.id.day_after_tomorrow_list);
        aftertomorrow_list.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heightAfterTomL = aftertomorrow_list.getMeasuredHeight();
        isBigAfterTomorrow = false;

        countAfterTomorrow = lvAfterTomorrow.getCount();

        aftertomorrow_layout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isBigAfterTomorrow){

                            hideElements(true, tv_date_after_tomorrow, weatherIconAfterTomorrow, temp_after_tomorrow, text_after_tomorrow);

                            sc = findViewById(R.id.nestedScrollView);

                            v.setClickable(false);
                            ValueAnimator va = ValueAnimator.ofInt(tomorrow_list.getHeight(), heightAfterTomL * countAfterTomorrow);

                            va.setDuration(DURATION);

                            if(isBigTomorrow) {
                                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        aftertomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        aftertomorrow_list.requestLayout();
                                    }
                                });

                            }
                            else {
                                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        final Integer value = (Integer) animation.getAnimatedValue();
                                        aftertomorrow_list.getLayoutParams().height = value;
                                        aftertomorrow_list.requestLayout();

                                        sc.post(new Runnable() {
                                            public void run() {
                                                sc.scrollTo(0, sc.getScrollY() + value/10); // these are your x and y coordinates
                                            }
                                        });
                                    }
                                });
                            }


                            va.start();
                            isBigAfterTomorrow = true;

                            if(isBigToday) {

                                hideElements(false, tv_date_now, weatherIconToday, temp_now, text_now);

                                ValueAnimator va1 = ValueAnimator.ofInt(today_list.getHeight(), 0);
                                va1.setDuration(DURATION);
                                va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        today_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        today_list.requestLayout();
                                    }
                                });
                                va1.start();
                                isBigToday = false;
                            }
                            if(isBigTomorrow){

                                hideElements(false, tv_date_tomorrow, weatherIconTomorrow, temp_tomorrow, text_tomorrow);

                                ValueAnimator va2 = ValueAnimator.ofInt(tomorrow_list.getHeight(), 0);
                                va2.setDuration(DURATION);
                                va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        tomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                        tomorrow_list.requestLayout();
                                    }
                                });
                                va2.start();
                                isBigTomorrow = false;
                            }
                        }
                        else{

                            hideElements(false, tv_date_after_tomorrow, weatherIconAfterTomorrow, temp_after_tomorrow, text_after_tomorrow);

                            ValueAnimator va = ValueAnimator.ofInt(aftertomorrow_list.getHeight(), 0);
                            va.setDuration(DURATION);
                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    aftertomorrow_list.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                                    aftertomorrow_list.requestLayout();
                                }
                            });
                            va.start();
                            isBigAfterTomorrow = false;
                        }
                        v.setClickable(true);
                    }
                }
        );
    }

    public void hideElements(boolean bool, final TextView date, final TextView icon, final TextView temp, final TextView text){

        LinearLayout layout = findViewById(R.id.today_layout);
        final float scale = this.getResources().getDisplayMetrics().density;

        if(bool){
            text.setGravity(Gravity.CENTER);
            text.setPadding(layout.getWidth()/2 - text.getWidth()/2,text.getPaddingTop(),text.getPaddingRight(),text.getPaddingBottom());

            ValueAnimator va = ValueAnimator.ofFloat(date.getAlpha(), 0.f);
            va.setDuration(DURATION);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    date.setAlpha((Float) animation.getAnimatedValue());
                    icon.setAlpha((Float) animation.getAnimatedValue());
                    temp.setAlpha((Float) animation.getAnimatedValue());
                    date.requestLayout();
                    icon.requestLayout();
                    temp.requestLayout();
                }
            });
            va.start();
        }
        else {
            text.setGravity(Gravity.START);
            text.setPadding((int) (4 * scale + 0.5f),text.getPaddingTop(),text.getPaddingRight(),text.getPaddingBottom());

            ValueAnimator va = ValueAnimator.ofFloat(date.getAlpha(), 1.f);
            va.setDuration(DURATION);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    date.setAlpha((Float) animation.getAnimatedValue());
                    icon.setAlpha((Float) animation.getAnimatedValue());
                    temp.setAlpha((Float) animation.getAnimatedValue());
                    date.requestLayout();
                    icon.requestLayout();
                    temp.requestLayout();
                }
            });
            va.start();
        }


    }
}
