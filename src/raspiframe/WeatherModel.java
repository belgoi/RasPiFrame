/**
 * MIT License
 *
 * Copyright (c) 2016 David Hinchliffe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package raspiframe;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import raspiframe.utilities.Setup;
import raspiframe.weather.ForecastData;
import raspiframe.weather.CurrentConditions;
import raspiframe.weather.IWeather;
import raspiframe.weather.WeatherUndergroundAPI;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * Model to collect and pass on the weather data to the Controller. 
 * It starts a timer task to periodically get the forecast and the current conditions
 * from the weather API.  The properties are bound to the appropriate text fields 
 * in the controller.  
 */

public class WeatherModel
{
    private Map<LocalDate,ForecastData> forecast=new HashMap<>();
    private CurrentConditions currently=new CurrentConditions();
    private IWeather weather;
    private Map<String,String> weatherData;
    private StringProperty day1LabelString=new SimpleStringProperty();
    private StringProperty day2LabelString=new SimpleStringProperty();
    private StringProperty day3LabelString=new SimpleStringProperty();
    private ObjectProperty<Image> day0IconImage=new SimpleObjectProperty<>();
    private ObjectProperty<Image> day1IconImage=new SimpleObjectProperty<>();
    private ObjectProperty<Image> day2IconImage=new SimpleObjectProperty<>();
    private ObjectProperty<Image> day3IconImage=new SimpleObjectProperty<>();
    private StringProperty day0HighString=new SimpleStringProperty();
    private StringProperty day1HighString=new SimpleStringProperty();
    private StringProperty day2HighString=new SimpleStringProperty();
    private StringProperty day3HighString=new SimpleStringProperty();
    private StringProperty day0LowString=new SimpleStringProperty();
    private StringProperty day1LowString=new SimpleStringProperty();
    private StringProperty day2LowString=new SimpleStringProperty();
    private StringProperty day3LowString=new SimpleStringProperty();
    
    private StringProperty temperature=new SimpleStringProperty();
    private StringProperty windSpeed=new SimpleStringProperty();
    private StringProperty windDir=new SimpleStringProperty();
    private StringProperty relHumidity=new SimpleStringProperty();
    private ObjectProperty currentIcon=new SimpleObjectProperty();
    private StringProperty feelsLike=new SimpleStringProperty();
    public WeatherModel()
    {
        //instantiate the weather object with the weather API key and pass the location   
        weather = new WeatherUndergroundAPI(Setup.weatherApiKey());
        weather.setLocation(Setup.weatherLocation());
       // weatherData=new HashMap<>();
        long i=Setup.updateWeatherInterval();
        startWeatherUpdate();
    }
    public ObjectProperty<Image> day0Icon()
    {
        return day0IconImage;
    }
    public StringProperty day0High()
    {
        return day0HighString;
    }
    public StringProperty day0Low()
    {
        return day0LowString;
    }
    public ObjectProperty<Image> day1Icon()
    {
        return day1IconImage;
    }
    public StringProperty day1High()
    {
        return day1HighString;
    }
    public StringProperty day1Low()
    {
        return day1LowString;
    }
    public StringProperty day1Label()
    {
        return day1LabelString;
    }
    public ObjectProperty<Image> day2Icon()
    {
        return day2IconImage;
    }
    public StringProperty day2High()
    {
        return day2HighString;
    }
    public StringProperty day2Low()
    {
        return day2LowString;
    }
    public StringProperty day2Label()
    {
        return day2LabelString;
    }
    public ObjectProperty<Image> day3Icon()
    {
        return day3IconImage;
    }
    public StringProperty day3High()
    {
        return day3HighString;
    }
    public StringProperty day3Low()
    {
        return day3LowString;
    }
    public StringProperty day3Label()
    {
        return day3LabelString;
    }
    public StringProperty currentTemp()
    {
        return temperature;
    }
    public StringProperty windMph()
    {
        return windSpeed;
    }
    public StringProperty windDirection()
    {
        return windDir;
    }
    public StringProperty relativeHumidity()
    {
        return relHumidity;
    }
    public ObjectProperty<Image> currentIcon()
    {
        return currentIcon;
    }
    public StringProperty feelsLike()
    {
        return feelsLike; 
    }
    //set the values for the current conditions. The properties are bound to the controller
    private void updateCurrentConditions(CurrentConditions currently)
    {
        if (currently !=null)
        {
            feelsLike.set(currently.getFeelsLike());
            relHumidity.set(currently.getRelativeHumidity());
            windSpeed.set(currently.getWindSpeed());
            temperature.set(currently.getCurrentTemp());
            currentIcon.set(currently.getWeatherIcon());
        }
    }
   //Sets the values for forecast weather data.  The properties are bound to the controller
    private void updateForecast(Map<LocalDate,ForecastData>forecast)
    {
        LocalDate today =LocalDate.now();
        LocalDate day1=today.plusDays(1);
        LocalDate day2=today.plusDays(2);
        LocalDate day3=today.plusDays(3);

        if(forecast !=null)
        {
            for(Map.Entry<LocalDate,ForecastData> entry:forecast.entrySet())
            {
                if (entry.getKey().isEqual(today))
                {
                    day0HighString.set(entry.getValue().getExpectedHighTempFahrenheit());
                    day0LowString.set(entry.getValue().getExpectedLowTempFahrenheit());
                    day0IconImage.set(entry.getValue().getWeatherIcon());                
                }
                else if(entry.getKey().isEqual(day1))
                {
                    day1LabelString.set(entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT,Locale.US));
                    day1HighString.set(entry.getValue().getExpectedHighTempFahrenheit());
                    day1LowString.set(entry.getValue().getExpectedLowTempFahrenheit());
                    day1IconImage.set(entry.getValue().getWeatherIcon());               
                }
                else if (entry.getKey().isEqual(day2))
                {
                    day2LabelString.set(entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT,Locale.US));
                    day2HighString.set(entry.getValue().getExpectedHighTempFahrenheit());
                    day2LowString.set(entry.getValue().getExpectedLowTempFahrenheit());            
                    day2IconImage.set(entry.getValue().getWeatherIcon());                
                }
                else if (entry.getKey().isEqual(day3))
                {
                    day3LabelString.set(entry.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT,Locale.US));
                    day3HighString.set(entry.getValue().getExpectedHighTempFahrenheit());
                    day3LowString.set(entry.getValue().getExpectedLowTempFahrenheit());             
                    day3IconImage.set(entry.getValue().getWeatherIcon());                
                }
            }
        }
    }
    //starts the timer task to make the call to the weather API.  The timer fires according to the 
    //interval defined in the setup file
    private void startWeatherUpdate()
        {
           TimerTask task=new TimerTask()
           {
                @Override
                public void run()
                {
                    weather.refreshWeather();
                    forecast=weather.getForecast();
                    currently=weather.getCurrentConditions();
                    updateForecast(weather.getForecast());
                    updateCurrentConditions(weather.getCurrentConditions());
                }
            };
            int startMin;
            LocalTime time=LocalTime.now();
            startMin=time.getMinute();        
            Timer timer=new Timer();
            //1000*60*interval converts milliseconds to minutes 
             timer.scheduleAtFixedRate(task, startMin,1000 * 60 * Setup.updateWeatherInterval());
        }
}
