package com.example.stmak.wuzzupweather;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class WeatherFunction {

    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "9579639d305a18621ede1c2a0ff57d0f";

    static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    public interface AsyncResponse {

        void processFinish(String output, String output2, String output3, String output4);
    }

    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        AsyncResponse delegate = null; //Call back interface

        placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interface through constructor
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

                    String now = main.getString("dt");

                    long unixSeconds = Long.parseLong(now);
                    Date date = new java.util.Date(unixSeconds*1000L);
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone(TimeZone.getDefault().toString()));
                    String formattedDate = sdf.format(date);

                    String city = json.getJSONObject("city").getString("name");
                    String country = json.getJSONObject("city").getString("country");
                    @SuppressLint("DefaultLocale") String temperatureNow = String.format("%.0f", main.getJSONObject("main").getDouble("temp"));

//                    String description = details.getString("description").toUpperCase(Locale.US);
//                    String humidity = main.getString("humidity") + "%";
//                    String pressure = main.getString("pressure") + " hPa";
//
//                    String iconText = setWeatherIcon(details.getInt("id"),
//                            json.getJSONObject("sys").getLong("sunrise") * 1000,
//                            json.getJSONObject("sys").getLong("sunset") * 1000);

                    delegate.processFinish( city, country, temperatureNow, formattedDate);

                }
            } catch (JSONException e) {
                Log.e("JSON", "Cannot process JSON results", e);
            }
        }
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
            String tmp="";
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
