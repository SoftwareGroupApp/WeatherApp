package com.hannahburzynski.weatherapp;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



/**
 *This class contains the behavior, attributes, and UI elements associated with
 * the Main Activity of the application.
 *
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
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
    public double[] timeOffSets = {0,7,0,8,12,13,6,7,0,0,-1,-5,13,7,-4,9,10.75,7,5,5,3,6,15,8,10.5,
            1.5,1,6,13,0,6,0,14,13,13,8,15,13,8.5 };//offests adjusted from central time
    public long timeOffSetSelected;
    protected Location mLastLocation;
    public double locLat = 0;
    public double locLong = 0;


/////////////////////////////// o n C r e a t e ////////////////////////////////////////////

    /**
     * Calls the super constructor onCreate() and loads current city and
     * time details. See onCreate() for more details related to Android
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreate(android.os.Bundle)">https://developer.android.com/reference/android/app/Activity.html#onCreate(android.os.Bundle)</a>
     *@param savedInstanceState loads the previous instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        intializeUI();
    //    onStart();
    }

    /**
     *Initializes the contents of the menu.
     *  @see <a href="https://developer.android.com/reference/android/app/Activity.html#onCreateOptionsMenu(android.view.Menu)">https://developer.android.com/reference/android/app/Activity.html#onCreateOptionsMenu(android.view.Menu)</a>
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


/////////////////////////////// o n S t a r t //////////////////////////////////////////////
    /**
     * Resumes the application.
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onStart()">https://developer.android.com/reference/android/app/Activity.html#onStart()</a>
     */
    @Override
       protected void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        //    onResume();
        }

/////////////////////////////// o n O t h e r S t u f f //////////////////////////////////
    /**
     * stops the application by calling the super constructor and
     * disconnecting the Api Client
     */
    @Override
    protected void onStop(){
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Shows the user which items were selected on from the menu.
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onOptionsItemSelected(android.view.MenuItem)">https://developer.android.com/reference/android/app/Activity.html#onOptionsItemSelected(android.view.MenuItem)</a>
     * @param item
     * @return whether the item was selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        if(selectedItemId == R.id.action_search){
                return true;
            }
           return false;
        }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            locLat = mLastLocation.getLatitude();
            locLong = mLastLocation.getLongitude();
        }
        else {
            Toast.makeText(this, "No Location", Toast.LENGTH_LONG).show();
        }
        query = getCityByLoc();
        locTimeZone = localTimeZone();
        timeOffSetSelected = 0;
        getWeather(query);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Alerts user that the connection failed
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.OnConnectionFailedListener">https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.OnConnectionFailedListener</a>
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/common/ConnectionResult">https://developers.google.com/android/reference/com/google/android/gms/common/ConnectionResult</a>
     * @param connectionResult error code on failure to connect to google services
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        Toast.makeText(this, "Connect Failed", Toast.LENGTH_LONG).show();
    }

    /**
     * Causes program to be idle in background. Also disconnects from Google Services.
     * @see <a href="https://developer.android.com/reference/android/app/Activity.html#onPause()">https://developer.android.com/reference/android/app/Activity.html#onPause()</a>
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



/////////////////////////////// L i s t e n e r ////////////////////////////////////////////

    /**
     * This class is responsible for listening to user events to choosing which city to query.
     * @see <a href="https://developer.android.com/reference/android/widget/AdapterView.OnItemSelectedListener.html">https://developer.android.com/reference/android/widget/AdapterView.OnItemSelectedListener.html</a>
     */
    class OnCitySelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
            int itemPos = parent.getSelectedItemPosition();
            if(itemPos != 0) {
                String city2Get = "q=" + parent.getSelectedItem();
                int cityIndex = parent.getSelectedItemPosition();
                timeOffSetSelected = ((long) timeOffSets[cityIndex]) * 3600L;
                Toast.makeText(parent.getContext(), parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_SHORT).show();
                getWeather(city2Get);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // DO nothing
        }


    }

/////////////////////////////// M E T H O D S /////////////////////////////////////////////

    /**
     * Get the a cities location via latitude and longitude
     * @return String that has the lat and long of a city
     */
    private String getCityByLoc(){
        if (locLat == 0)
        {
            locLat =  30.570851; //50.751244; //
            locLong = -97.653652; //37.618423; //
        }
        return "lat=" + locLat + "&lon=" + locLong;
    }

    /**
     * Gets the local Time Zone
     * @return a String that describes the time zone of the city
     */
    private String localTimeZone(){
        //this gets the local time zone from the device
        String timeZoneText =  TimeZone.getDefault() + "";
        int tzLeft = timeZoneText.indexOf("id=") + 4;
        int txRight = timeZoneText.indexOf(",mRawOffset") - 1;
        return timeZoneText.substring(tzLeft, txRight);
    }

    /**
     * Initializes the UI from the components from the main_activity.xml
     */
    protected void intializeUI(){
        // Initialize UI
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        humidityValueTextView = (TextView) findViewById(R.id.humidityValueTextView);
        precipitationValueTextView = (TextView) findViewById(R.id.precipitationValueTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        latTextView = (TextView) findViewById(R.id.latTextVew);
        longTextVew = (TextView) findViewById(R.id.longTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        Spinner spinner = (Spinner) findViewById(R.id.city_spinner);
        try{
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(800);
        }catch(java.lang.NoSuchFieldException e){
            e.printStackTrace();
        }
        catch(java.lang.IllegalAccessException e){
            e.printStackTrace();
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.city_array,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnCitySelectedListener());
    }

    /**
     * Updates the display according to the new Current Weather attributes.
     */
    private void updateDisplay() {
        // Update UI elements with data from current weather object
        timeTextView.setText(/*"At " + */"At " + currentWeather.getFormattedTime()
                + " the weather is");
        temperatureTextView.setText(currentWeather.getTemperature() + "");
        humidityValueTextView.setText(currentWeather.getHumidity() + "%");
        precipitationValueTextView.setText(String.format("%.2f", currentWeather.getPressure()));
        locationTextView.setText(currentWeather.getCity());
        latTextView.setText(currentWeather.getLat() + "");
        longTextVew.setText(currentWeather.getLon() + "");
        dateTextView.setText(currentWeather.getFormattedDate());
    }

    /**
     * Checks to see the status of the network
     * @return the state of the network, either it is on, or not
     */
    private boolean isNetworkAvailable() {
        boolean isAvailable = false;
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Determine if network info is present and active, need permission to access network state
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // Check if network is present and connected
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * Gets the current weather update from
     * @param query this query is part of the request the client requests from the server
     */
    protected void getWeather(String query){
        final String APP_ID = "ddd38c0b3a7b986b4f6c036e8c9081bc";
        String forecastURL = "http://api.openweathermap.org/data/2.5/weather?" +
                query + "&appid=" + APP_ID;
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
                public void onFailure(Call call, IOException e) { }

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

    /**
     * Displays an error message.
     */
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        // Show error dialog
        dialog.show(getFragmentManager(), "error_dialog");
    }

    /**
     * parses a string to get attributes
     * @param jsonData String that contains JSON query to parse attributes
     * @return an object CurrentWeather with the contents and attributes
     * @throws JSONException
     */
    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        CurrentWeather currentWeather = new CurrentWeather();
        // Exception is handled for whoever calls this method
        // Parse json data and assign to current weather object
        // Currently is a json object embedded in the original json object
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject main = forecast.getJSONObject("main");
        JSONObject coord = forecast.getJSONObject("coord");
        currentWeather.setHumidity(main.getDouble("humidity"));
        currentWeather.setTime(forecast.getLong("dt") + timeOffSetSelected);
        currentWeather.setPressure(main.getLong("pressure") *  0.0295299830714);
        currentWeather.setTemperature((main.getDouble("temp") * 9/5 - 459.67));
        currentWeather.setCity(forecast.getString("name"));
        currentWeather.setLat(coord.getDouble("lat"));
        currentWeather.setLon(coord.getDouble("lon"));
        timeOffSetSelected = 0; //resets the offset
        return currentWeather;
    }
}
