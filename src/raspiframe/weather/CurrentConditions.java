package raspiframe.weather;

import javafx.scene.image.Image;
import java.time.LocalTime;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class CurrentConditions
{
    private WeatherIcon weatherIcon;
    private String weatherCondition;
    private String windSpeed;
    private String feelsLike;
    private String relativeHumidity;
    private String currentTemp;
    private LocalTime sunrise;
    private LocalTime sunset;

    public String getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public String getFeelsLike()
    {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike)
    {
        this.feelsLike = feelsLike;
    }

    public String getRelativeHumidity()
    {
        return relativeHumidity;
    }

    public void setRelativeHumidity(String relativeHumidity)
    {
        this.relativeHumidity = relativeHumidity;
    }

    public String getCurrentTemp()
    {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp)
    {
        this.currentTemp = currentTemp;
    }
    public Image getWeatherIcon()
    {
       LocalTime now=LocalTime.now();
       
       //TODO: MOVE THIS LOGIC INTO THE WEATHER ICON CLASS
       if (now.isAfter(sunrise) && now.isBefore(sunset))
           weatherIcon=new WeatherIcon(this.weatherCondition,"day");
       else
           weatherIcon=new WeatherIcon(this.weatherCondition,"night");
       return weatherIcon.getWeatherIcon();
    }
    public String getWeatherCondition()
    {
        return weatherCondition;
    } 
    public void setWeatherCondition(String weatherCondition)
    {
        this.weatherCondition = weatherCondition;      
    }
    public LocalTime getSunrise()
    {
        return sunrise;
    }
    public void setSunrise(LocalTime sunrise)
    {
        this.sunrise=sunrise;
    }
    public LocalTime getSunset()
    {
        return sunset;
    }
    public void setSunset(LocalTime sunset)
    {
        this.sunset=sunset;
    }
    
}
