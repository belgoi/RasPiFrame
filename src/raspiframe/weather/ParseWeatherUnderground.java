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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.LocalDateTime;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * Parses the weatherunderground JSON object in the httpEntity string passed into it from the weather API.
 * Provides the methods getCurrentConditions, and getForecast 
 */
public class ParseWeatherUnderground
{
    String httpEntity;
    public ParseWeatherUnderground(String httpEntity)
    {
        this.httpEntity=httpEntity;
    }
    public AstronomicalConditions getAstronomicalConditions()
    {
        AstronomicalConditions astronomicalConditions=new AstronomicalConditions();
        if (!httpEntity.isEmpty())
        {
            try
            {
                JSONParser parser=new JSONParser();
                JSONObject weatherData=(JSONObject)parser.parse(httpEntity);
                //parses the JSON object sun phase to get sunrise and sunset
                JSONObject astronomy =(JSONObject)weatherData.get("sun_phase");
                JSONObject sunset = (JSONObject)astronomy.get("sunset");
                JSONObject sunrise=(JSONObject)astronomy.get("sunrise");
               // LocalTime sunsetTime=LocalTime.of(Integer.parseInt((String)sunset.get("hour")), Integer.parseInt((String)sunset.get("minute")));
              //  LocalTime sunriseTime=LocalTime.of(Integer.parseInt((String)sunrise.get("hour")),Integer.parseInt((String)sunrise.get("minute")));
                astronomy=(JSONObject)weatherData.get("moon_phase");
                astronomicalConditions.setMoonPhase((String)astronomy.get("phaseofMoon"));
                astronomicalConditions.setSunset(LocalTime.of(Integer.parseInt((String)sunset.get("hour")), Integer.parseInt((String)sunset.get("minute"))));
                astronomicalConditions.setSunrise(LocalTime.of(Integer.parseInt((String)sunrise.get("hour")),Integer.parseInt((String)sunrise.get("minute"))));
            }
            catch(ParseException e)
            {
                System.err.println("Problem parsing astronomical conditions");
                System.err.println(e);
            }
        }
        return astronomicalConditions;
    }
    //getCurrentConditions takes the JSON httpEntity string and parses out the 
    //fields for current conditions and the sunset and sunrise times 
    //returns the object CurrentConditions with the current conditions
    public CurrentConditions getCurrentConditions()
    {
        CurrentConditions conditions=new CurrentConditions();
        if (!httpEntity.isEmpty())
        {
            try
            {
                JSONParser parser=new JSONParser();
                JSONObject weatherData=(JSONObject)parser.parse(httpEntity);
                //parses the JSON object current observation to get current conditions
                JSONObject currently=(JSONObject)weatherData.get("current_observation");
                
                //get and set the wind conditions
                String windSpeed=((Double)currently.get("wind_mph")).toString();
                String windString=(String)currently.get("wind_string");
                String windDirection=formatDirection((String)currently.get("wind_dir"));       
               //build the wind string to display on the overlay
                if (windString.equals("Calm"))
                    conditions.setWindSpeed("0 mph");
                else
                {
                    //if wind gusts are 0 mph then the API returns a long rather than a string resulting in a cast exception
                    try
                    {
                        String windGusts=(String)currently.get("wind_gust_mph");                
                        conditions.setWindSpeed(windSpeed.equals(windGusts)?windSpeed + " mph":windSpeed +" - " + windGusts + " mph " + windDirection);
                    }
                    //if wind gusts are 0, the API returns a long resulting in a cast exception
                    //unlike before, there isn't any easy way to catch it and avoid using the try catch
                    catch(ClassCastException e)
                    {
                        conditions.setWindSpeed(windSpeed + " mph");
                    }
                }
                
                Double feelsLike=Double.parseDouble((String)currently.get("feelslike_f"));                    
                conditions.setFeelsLike(Integer.toString(feelsLike.intValue())+ "\u00b0");
                conditions.setRelativeHumidity((String)currently.get("relative_humidity"));
                Double temp=(Double)currently.get("temp_f");
                conditions.setCurrentTemp(Integer.toString(temp.intValue())+"\u00b0");
                conditions.setWeatherCondition((String)currently.get("weather"));
                LocalDateTime now=LocalDateTime.now();                       
                String time=now.format(DateTimeFormatter.ofPattern("h:mm a"));
                String date=now.format(DateTimeFormatter.ofPattern("EEEE"));
                conditions.setLastUpdated("last updated at " + time);
            }
            catch(ParseException | ClassCastException e)
            {
                System.err.println("ParseWeatherUnderground: getCurrentConditions: " + e);
            }
        }

        return conditions;
    }
    //getForecast takes the JSON httpEntity and parses out the current day forecast and the forecast
    //for the next 3 days.returns a hashmap with the date for the forecast as the key, and the forecast 
    //for the day as the value
    public Map<LocalDate,ForecastData> getForecast()
    {
        Map<LocalDate,ForecastData> conditions=new HashMap<>();

        try
        {
            JSONParser parser=new JSONParser();
            JSONObject weatherData=(JSONObject)parser.parse(httpEntity);
            JSONObject forecast=(JSONObject)weatherData.get("forecast");
            
            JSONObject txtForecast=(JSONObject)forecast.get("txt_forecast");
            JSONArray forecastdayArray=(JSONArray)txtForecast.get("forecastday");
            JSONObject forecastdayObject;
            JSONObject simpleForecast=(JSONObject)forecast.get("simpleforecast");
            JSONArray daily =(JSONArray)simpleForecast.get("forecastday");
            JSONObject periodForecast;
            for ( int i=0;i<daily.size();i++)
            {
                ForecastData forecastData=new ForecastData();
                periodForecast=(JSONObject)daily.get(i);
                
                JSONObject highArray = (JSONObject)periodForecast.get("high");
                    forecastData.setExpectedHighTempFahrenheit((highArray.get("fahrenheit")).toString()+"\u00b0");
                    forecastData.setExpectedHighTempCelsius((highArray.get("celsius")).toString()+"\u00b0");
                JSONObject lowArray = (JSONObject)periodForecast.get("low");
                    forecastData.setExpectedLowTempFahrenheit((lowArray.get("fahrenheit")).toString()+"\u00b0");
                    forecastData.setExpectedLowTempCelsius((lowArray.get("celsius")).toString()+"\u00b0");
                JSONObject dateArray=(JSONObject)periodForecast.get("date");
                        String day = dateArray.get("day").toString();
                        String month=dateArray.get("month").toString();
                        String year=dateArray.get("year").toString();
                        LocalDate period=LocalDate.of(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));  

                    forecastData.setWeatherCondition(periodForecast.get("conditions").toString());
                    forecastdayObject=(JSONObject)forecastdayArray.get(i);
                    forecastData.setPrecipChanceDay(forecastdayObject.get("pop").toString()+"%");
                    forecastdayObject=(JSONObject)forecastdayArray.get(i+1);
                    forecastData.setPrecipChanceNight(forecastdayObject.get("pop").toString()+"%");
                    conditions.put(period, forecastData);
            }
        }
        catch(ParseException | ClassCastException e)
        {
            System.err.println("ParseWeatherUnderground: getForecast: " + e);
        }
        return conditions;
    }
    public String formatDirection(String longDirection)
    {
        String shortDirection=longDirection;
        shortDirection =shortDirection.contains("North")?shortDirection.replace("North","N"):shortDirection;
        shortDirection=shortDirection.contains("South")?shortDirection.replace("South","S"):shortDirection;
        shortDirection=shortDirection.contains("East")?shortDirection.replace("East","E"):shortDirection;
        shortDirection=shortDirection.contains("West")?shortDirection.replace("West","W"):shortDirection;
        return shortDirection;
    }
    public String determineWind(String windDirection, String windSpeed, String windGusts,String windString)
    {
        String wind;
        if (windString.equals("Calm"))
            wind="0 mph";
        else
            wind=windSpeed.equals(windGusts)?windSpeed + " mph": windSpeed + " - " + windGusts + " mph " + formatDirection(windDirection);
        return wind;
           
    }
            
    
}
