package com.hannahburzynski.weatherapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class is responsible for encapsulating the contents of weather data
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

    /**
     * Get the city assocaited with the api call
     * @return city the city in the current weather
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the city
     * @param city the city in current weather
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the time
     * @return time the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Set the time
     * @param time the time
     */
    public void setTime(long time) {
        // Date time is in milliseconds
        this.time = (time * 1000L);
    }

    /**
     * Gets the temperature
     * @return temperature the temperture
     */
    public int getTemperature() {
        return (int)Math.round(temperature);
    }

    /**
     * sets the temperature
     * @param temperature the temperature
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * returns the humidity
     * @return humidity the humidity
     */
    public double getHumidity() {
        return humidity;
    }

    /**
     * sets the humidity
     * @param humidity the humdidity
     */
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    /**
     * gets the pressure
     * @return pressure the pressure
     */
    public double getPressure() { return pressure; }

    /**
     * sets the pressure
     * @param pressure the pressure
     */
    public void setPressure(double pressure) { this.pressure = pressure; }

    /**
     * gets the lat
     * @return lat the lat
     */
    public double getLat() { return lat; }

    /**
     * sets the lat
     * @param lat the lat
     */
    public void setLat(double lat) { this.lat = lat; }

    /**
     * gets the lon
     * @return lon the lon
     */
    public double getLon() { return lon; }

    /**
     * sets the lon
     * @param lon the long
     */
    public void setLon(double lon) { this.lon = lon; }

    /**
     * get formatted time
     * @return String that contains the time for the city
     */
    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(MainActivity.locTimeZone));
        Date dateTime = new Date(getTime());
        // Convert Date to String
        String timeString = formatter.format(dateTime);
        return timeString;
    }

    /**
     * returns the formatted time according
     * @return a String of the time
     */
    public String getFormattedDate(){
        SimpleDateFormat formatDate = new SimpleDateFormat("EEE, MMM d, yyyy");
        formatDate.setTimeZone(TimeZone.getTimeZone((MainActivity.locTimeZone)));
        Date dateTime = new Date(getTime());
        // Convert Date to String
        String timeString = formatDate.format(dateTime);
        return timeString;

    }
}
