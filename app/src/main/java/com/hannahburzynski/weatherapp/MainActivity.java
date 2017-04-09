package com.hannahburzynski.weatherapp;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather currentWeather;

    // UI
    private TextView timeTextView;
    private TextView temperatureTextView;
    private TextView humidityValueTextView;
    private TextView precipitationValueTextView;
    private TextView locationTextView;
    private TextView latTextView;
    private TextView longTextVew;
    private TextView dateTextView;
    private String query;
    public static String locTimeZone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getCityByLoc();
        locTimeZone = localTimeZone();
        onStart();
    }

    protected void onStart() {
        super.onStart();
        onResume();
    }


    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);

        // Initialize UI
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        humidityValueTextView = (TextView) findViewById(R.id.humidityValueTextView);
        precipitationValueTextView = (TextView) findViewById(R.id.precipitationValueTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        latTextView = (TextView) findViewById(R.id.latTextVew);
        longTextVew = (TextView) findViewById(R.id.longTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);


        // URL parameters
        final String APP_ID = "ddd38c0b3a7b986b4f6c036e8c9081bc";
        //by city
        // query = getCityByName();
        //by location
      //  query = getCityByLoc();
      //  LocationManager locationManager = (LocationManager)
      //          getSystemService(Context.LOCATION_SERVICE);
        String forecastURL = "http://api.openweathermap.org/data/2.5/weather?" + query + "&appid=" + APP_ID;

        // Check if network is available before making request
        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            // Build a request that the client will send to the server
            Request request = new Request.Builder().url(forecastURL).build();
            // Put request in a call object
            Call call = client.newCall(request);
            // Place call on the queue to be executed in a background thread
            // Callbacks are used to communicate between the background thread and main thread
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Store response from call
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            // Store requested data in current weather object
                            currentWeather = getCurrentDetails(jsonData);
                            // Update the UI elements
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                      catch (JSONException e) {
                          Log.e(TAG, "Exception caught: ", e);
                      }
                }
            });
        }
        else {
            Toast.makeText(this, R.string.network_unavailable_message,Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        // Update UI elements with data from current weather object
        timeTextView.setText(/*"At " + */"At " + currentWeather.getFormattedTime() + " the weather is");
        temperatureTextView.setText(currentWeather.getTemperature() + "");
        humidityValueTextView.setText(currentWeather.getHumidity() + "%");
        precipitationValueTextView.setText(currentWeather.getPressure() + "");
        locationTextView.setText(currentWeather.getCity());
        latTextView.setText(currentWeather.getLat() + "");
        longTextVew.setText(currentWeather.getLon() + "");
        dateTextView.setText(currentWeather.getFormattedDate());
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        CurrentWeather currentWeather = new CurrentWeather();
        // Exception is handled for whoever calls this method
        // Parse json data and assign to current weather object
        // Currently is a json object embedded in the original json object
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject main = forecast.getJSONObject("main");
        JSONObject coord = forecast.getJSONObject("coord");
        currentWeather.setHumidity(main.getDouble("humidity"));
        currentWeather.setTime(forecast.getLong("dt"));
        currentWeather.setPressure(main.getLong("pressure"));
        currentWeather.setTemperature((main.getDouble("temp")*9/5 - 459.67));
        currentWeather.setCity(forecast.getString("name"));
        currentWeather.setLat(coord.getDouble("lat"));
        currentWeather.setLon(coord.getDouble("lon"));


        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        boolean isAvailable = false;

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Determine if network info is present and active, need permission to access network state
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // Check if network is present and connected
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        // Show error dialog
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private String getCityByName(){
        String cityName =  "St. John's, CA";
        return "q=" + cityName;
    }

    private String getCityByLoc(){
        double locLat = 30.570851;
        double locLong = -97.653652;
        return "lat=" + locLat + "&lon=" + locLong;
    }

    private String localTimeZone(){
        //this gets the local time zone from the device but just ru
        String timeZoneText =  TimeZone.getDefault() + "";
        int tzLeft = timeZoneText.indexOf("id=") + 4;
        int txRight = timeZoneText.indexOf(",offset") - 1;
        return  "\" + timeZoneText.substring(tzLeft, txRight) + \"";
    }
}