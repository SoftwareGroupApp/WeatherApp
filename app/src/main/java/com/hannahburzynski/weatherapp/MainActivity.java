package com.hannahburzynski.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    private EditText searchBoxEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize UI
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        humidityValueTextView = (TextView) findViewById(R.id.humidityValueTextView);
        precipitationValueTextView = (TextView) findViewById(R.id.precipitationValueTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        searchBoxEditText = (EditText) findViewById(R.id.search_bar);

        // URL parameters
        final String APP_ID = "ddd38c0b3a7b986b4f6c036e8c9081bc";
        String query = "London,uk";
        String forecastURL = "http://api.openweathermap.org/data/2.5/weather?q=" + query + "&appid=" + APP_ID;

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
        timeTextView.setText("At " + currentWeather.getFormattedTime() + " the weather is");
        temperatureTextView.setText(currentWeather.getTemperature() + "");
        humidityValueTextView.setText(currentWeather.getHumidity() + "%");
        precipitationValueTextView.setText(currentWeather.getPressure() + "");
        locationTextView.setText(currentWeather.getCity());
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        CurrentWeather currentWeather = new CurrentWeather();
        // Exception is handled for whoever calls this method

        // Parse json data and assign to current weather object
        // Currently is a json object embedded in the original json object
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject main = forecast.getJSONObject("main");

        currentWeather.setHumidity(main.getDouble("humidity"));
        currentWeather.setTime(forecast.getLong("dt"));
        currentWeather.setPressure(main.getLong("pressure"));
        currentWeather.setTemperature((main.getDouble("temp")*9/5 - 459.67));
        currentWeather.setCity(forecast.getString("name"));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        if(selectedItemId == R.id.action_search){
            Toast.makeText(this, "Search clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
