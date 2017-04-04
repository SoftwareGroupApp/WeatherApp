package com.hannahburzynski.weatherapp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by kyleratliff on 4/4/2017.
 */
public class CurrentWeatherTest {

    //This test ensures that the format of the time
    //is parsed correctly
    private CurrentWeather currentWeather;

    @Before
    public void initialize() throws Exception{
        currentWeather = new CurrentWeather();
        currentWeather.setCity("London");
        currentWeather.setHumidity(81.0);
        currentWeather.setPressure(1012.0);
        currentWeather.setTemperature(45);
        currentWeather.setTime(1485789600);
    }

    @Test
    public void getFormattedTime() throws Exception {
        Assert.assertNotNull(currentWeather);
        String expected = "3:20 PM";
        Assert.assertEquals(currentWeather.getFormattedTime(), expected);


    }

}