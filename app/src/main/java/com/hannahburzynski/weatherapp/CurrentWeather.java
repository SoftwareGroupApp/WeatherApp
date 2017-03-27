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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getTime() {
        return time;
    }

    // Get the formatted time from a UNIX time string value
    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getCity()));

        // Date time is in milliseconds
        Date dateTime = new     Date(getTime() * 1000);
        // Convert Date to String
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setTime(long time) {
        this.time = time;
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
}
