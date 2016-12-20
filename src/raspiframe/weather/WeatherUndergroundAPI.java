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
package raspiframe.weather;
import java.util.Map;
import java.time.LocalDate;
import raspiframe.utilities.Setup;
import java.time.LocalTime;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * The API for the WeatherUnderground web API. Implements the IWeather interface to ensure a common type
 * is used for the weather API classes.  Provides the methods buildGetWeatherRequestURI,setLocation,refreshWeather,
 * getForecast,getCurrentConditions,getSunrise, and getSunset
 */
public class WeatherUndergroundAPI implements IWeather
{
    private final String HOST="api.wunderground.com";
    private final String key;
    private String state;
    private String city;
    private Map<LocalDate,ForecastData> forecast;
    private CurrentConditions currentConditions;

    public WeatherUndergroundAPI(String key)
    {
        this.key=key;
    }
    
    //used only within the class so access is limited to private
    //builds the URI used to retrieve the weather from web.
    //returns a string with the URI get request
    private String buildGetWeatherRequestUri()
    {
        if (Setup.weatherLocation().equals(""))
            throw new IllegalArgumentException("Location hasn't been set");
                
        return "/api/" + key + "/forecast/conditions/astronomy/q/"+state+"/"+city+".json";
    }
    
    //sets the location to retrieve the weather data for
    @Override
    public void setLocation(String location)
    {
        this.state=(location.contains(",") ? location.substring(location.indexOf(",") +1):"");
        this.city=(location.contains(",") ? location.substring(0,location.indexOf(",")):"");
    }
   
    //refreshes the weather data by calling the get request to the URI. It then passes the 
    //httpEntity to the parseWeatherUnderground object's getForecast method and getCurrentConditions
    //to set the weather conditions properties
    @Override
    public void refreshWeather()
    {

        httpConnection connection=new httpConnection(HOST,buildGetWeatherRequestUri());
        ParseWeatherUnderground parse=new ParseWeatherUnderground();
        forecast=parse.getForecast(connection.getEntity());
        currentConditions=parse.getCurrentConditions(connection.getEntity());
    }
    
    //Returns the hashmap with the forecast data
    @Override
    public Map<LocalDate,ForecastData>getForecast()
    {
        return forecast;
    }
    
    //Returns an object with the current conditions
    @Override
    public CurrentConditions getCurrentConditions()
    {
        return currentConditions;
    }
    
    //Returns a LocalTime object with the time of the sunset
    @Override
    public LocalTime getSunset()
    {
        return currentConditions.getSunset(); 
    }
    
    //Returns a LocalTime object with the time of the sunrise
    @Override
    public LocalTime getSunrise()
    {
        return currentConditions.getSunrise();
    }
    
}
