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

    public String getExpectedLowTempCelsius()
    {
        return expectedLowTempCelsius;
    }

    public void setExpectedLowTempCelsius(String expectedLowTempCelsius)
    {
        this.expectedLowTempCelsius = expectedLowTempCelsius;
    }

    public String getExpectedHighTempCelsius()
    {
        return expectedHighTempCelsius;
    }

    public void setExpectedHighTempCelsius(String expectedHighTempCelsius)
    {
        this.expectedHighTempCelsius = expectedHighTempCelsius;
    }

    public String getExpectedLowTempFahrenheit()
    {
        return expectedLowTempFahrenheit;
    }

    public void setExpectedLowTempFahrenheit(String expectedLowTemp)
    {
        this.expectedLowTempFahrenheit = expectedLowTemp;
    }

    public String getExpectedHighTempFahrenheit()
    {
        return expectedHighTempFahrenheit;
    }

    public void setExpectedHighTempFahrenheit(String expectedHighTemp)
    {
        this.expectedHighTempFahrenheit = expectedHighTemp;
    }

    public String getWeatherCondition()
    {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition)
    {
        this.weatherCondition = weatherCondition;
    }

    public String getForecastDay()
    {
        return forecastDay;
    }

    public void setForecastDay(String forecastDay)
    {
        this.forecastDay = forecastDay;
    }
    public Image getWeatherIcon()
    {
        weatherIcon=new WeatherIcon(this.weatherCondition,"day");
        return weatherIcon.getWeatherIcon();
    }
    
}
