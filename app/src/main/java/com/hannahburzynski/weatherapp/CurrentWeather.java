package com.hannahburzynski.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hannahburzynski on 2/6/17.
 */

public class CurrentWeather {

    private long time;
    private double temperature;
    private double humidity;
    private double pressure;
    private String city;
    private double lat;
    private double lon;
    public String getFormattedDate;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        // Date time is in milliseconds
        this.time = (time * 1000L);
    }

    public int getTemperature() {
        return (int)Math.round(temperature);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() { return pressure; }

    public void setPressure(double pressure) { this.pressure = pressure; }

    public double getLat() { return lat; }

    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }

    public void setLon(double lon) { this.lon = lon; }

    // Get the formatted time from a UNIX time string value
    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(MainActivity.locTimeZone));
        Date dateTime = new Date(getTime());
        // Convert Date to String
        String timeString = formatter.format(dateTime);
        return timeString;
    }

    public String getFormattedDate(){
        SimpleDateFormat formatDate = new SimpleDateFormat("EEE, MMM d, yyyy");
        formatDate.setTimeZone(TimeZone.getTimeZone((MainActivity.locTimeZone)));
        Date dateTime = new Date(getTime());
        // Convert Date to String
        String timeString = formatDate.format(dateTime);
        return timeString;

    }
}
