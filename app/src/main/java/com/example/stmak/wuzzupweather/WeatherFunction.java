package com.example.stmak.wuzzupweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class WeatherFunction {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "9579639d305a18621ede1c2a0ff57d0f";

    static String setWeatherIcon(int actualId, String dt){
        int id = actualId / 100;
        String icon = "";

        // get current time from dt
        Date date = new java.util.Date(Long.parseLong(dt)*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone(TimeZone.getDefault().toString()));
        int nowHour = Integer.parseInt(sdf.format(date));

        if(actualId == 800){
            if(nowHour>=7 && nowHour<21) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = context.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = context.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = context.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = context.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = context.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public interface AsyncResponse {

        void processFinish(String output, String output2, String output3, String output4,
                           String[] output5, String output6, String output7, String output8,
                           String[] output9, String output10, String output11, String output12,
                           String[] output13, String output14
        );
    }

    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        AsyncResponse delegate; //Call back interface

        placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse; //Assigning call back interface through constructor
        }

        @Override
        protected JSONObject doInBackground(String... city) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(city[0]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }
            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null){
                    JSONObject main = json.getJSONArray("list").getJSONObject(0);

                    long unixSeconds = Long.parseLong(main.getString("dt"));
                    Date date = new java.util.Date(unixSeconds*1000L);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone(TimeZone.getDefault().toString()));

                    int nowHour = Integer.parseInt(sdf.format(date));
                    int count = 0;

                    for(int i = nowHour; i < 39; i = i + 3){
                        count++;
                    }

                    int arrtod = 0;
                    for(int i = nowHour; i < 24; i = i + 3){
                        arrtod++;
                    }

                    // Arrays for forecast
                    String[] arrToday = getArr(json, arrtod);
                    String[] arrTomorrow = getArr(json, arrtod+8);
                    String[] arrAfterTomorrow = getArr(json, arrtod+16);

                    JSONObject mainTomorrow = json.getJSONArray("list").getJSONObject(count);
                    JSONObject mainAfterTomorrow = json.getJSONArray("list").getJSONObject(count + 8);

                    String dateNow = main.getString("dt_txt");
                    String[] arr = dateNow.split("[ ]");
                    dateNow = arr[0];

                    String dateTomorrow = mainTomorrow.getString("dt_txt");
                    arr = dateTomorrow.split("[ ]");
                    dateTomorrow = arr[0];

                    String dateAfterTomorrow = mainAfterTomorrow.getString("dt_txt");
                    arr = dateAfterTomorrow.split("[ ]");
                    dateAfterTomorrow = arr[0];

                    String city = json.getJSONObject("city").getString("name");
                    String country = json.getJSONObject("city").getString("country");
                    @SuppressLint("DefaultLocale") String temperatureNow = String.format("%.0f", main.getJSONObject("main").getDouble("temp"));
                    @SuppressLint("DefaultLocale") String temperatureTomorrow = String.format("%.0f", mainTomorrow.getJSONObject("main").getDouble("temp"));
                    @SuppressLint("DefaultLocale") String temperatureAfterTomorrow = String.format("%.0f", mainAfterTomorrow.getJSONObject("main").getDouble("temp"));

//                    String description = details.getString("description").toUpperCase(Locale.US);
//                    String humidity = main.getString("humidity") + "%";
//                    String pressure = main.getString("pressure") + " hPa";
//
                    String iconTextToday = setWeatherIcon(main.getJSONArray("weather").getJSONObject(0).getInt("id"),
                            main.getString("dt"));

                    String iconTextTomorrow = setWeatherIcon(mainTomorrow.getJSONArray("weather").getJSONObject(0).getInt("id"),
                            mainTomorrow.getString("dt"));

                    String iconTextAfterTomorrow = setWeatherIcon(mainAfterTomorrow.getJSONArray("weather").getJSONObject(0).getInt("id"),
                            mainAfterTomorrow.getString("dt"));

                    delegate.processFinish(
                            city,
                            country,
                            temperatureNow,
                            iconTextToday,
                            arrToday,
                            dateNow,
                            temperatureTomorrow,
                            iconTextTomorrow,
                            arrTomorrow,
                            dateTomorrow,
                            temperatureAfterTomorrow,
                            iconTextAfterTomorrow,
                            arrAfterTomorrow,
                            dateAfterTomorrow
                    );
                }
            } catch (JSONException e) {
                Log.e("JSON", "Cannot process JSON results", e);
            }
        }
    }

    static String[] getArr(JSONObject json, int num) throws JSONException {
        String[] arr;

        arr = new String[num];

        int temp = 0;

        if(num > 8){
            temp = num - 8;
            arr = new String[8];
        }

        int i = 0;

        for(;temp < num; temp++){
            JSONObject main = json.getJSONArray("list").getJSONObject(temp);
            String iconText = setWeatherIcon(main.getJSONArray("weather").getJSONObject(0).getInt("id"),
                    main.getString("dt"));
            @SuppressLint("DefaultLocale") String temperature = String.format("%.0f", main.getJSONObject("main").getDouble("temp"));
            String date = main.getString("dt_txt");
            String[] tempArr = date.split("[ :]");
            arr[i] = tempArr[1] + ":00;" + iconText + ";" + temperature + context.getString(R.string.temperature_gradus);
            i++;
        }
        return arr;
    }

    static JSONObject getWeatherJSON(String city){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, city));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
            String tmp;
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
