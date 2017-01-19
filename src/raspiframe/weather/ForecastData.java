package raspiframe.weather;
import javafx.scene.image.Image;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class ForecastData
{
    private String expectedLowTempFahrenheit;
    private String expectedHighTempFahrenheit;
    private String expectedLowTempCelsius;
    private String expectedHighTempCelsius;
    private String weatherCondition;
    private String forecastDay;

    private WeatherIcon weatherIcon;
    public ForecastData(){}
    //All of the setters are only available to the package to keep the 
    //data from being modified.
    public String getExpectedLowTempCelsius()
    {
        return expectedLowTempCelsius;
    }

     void setExpectedLowTempCelsius(String expectedLowTempCelsius)
    {
        this.expectedLowTempCelsius = expectedLowTempCelsius;
    }

    public String getExpectedHighTempCelsius()
    {
        return expectedHighTempCelsius;
    }

    void setExpectedHighTempCelsius(String expectedHighTempCelsius)
    {
        this.expectedHighTempCelsius = expectedHighTempCelsius;
    }

    public String getExpectedLowTempFahrenheit()
    {
        return expectedLowTempFahrenheit;
    }

    void setExpectedLowTempFahrenheit(String expectedLowTemp)
    {
        this.expectedLowTempFahrenheit = expectedLowTemp;
    }

    public String getExpectedHighTempFahrenheit()
    {
        return expectedHighTempFahrenheit;
    }

    void setExpectedHighTempFahrenheit(String expectedHighTemp)
    {
        this.expectedHighTempFahrenheit = expectedHighTemp;
    }

    public String getWeatherCondition()
    {
        return weatherCondition;
    }

    void setWeatherCondition(String weatherCondition)
    {
        this.weatherCondition = weatherCondition;
    }

    public String getForecastDay()
    {
        return forecastDay;
    }

    void setForecastDay(String forecastDay)
    {
        this.forecastDay = forecastDay;
    }
    public Image getWeatherIcon()
    {
        weatherIcon=new WeatherIcon(this.weatherCondition,"day");
        return weatherIcon.getWeatherIcon();
    }
    
}
