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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.image.Image;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import raspiframe.utilities.Setup;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * WeatherIcon class maps the external icon files to the weather conditions retrieved by the 
 * call to the API. provides the methods getWeatherIcon, setWeatherIcon, parseIcons,& loadIconMap
 */
public class WeatherIcon
{
    private Map<String,List<String>> weatherIconMap;
    private Image weatherIcon;
    private static final int DAY=0;
    private static final int NIGHT=1;
    //the constructor calls the loadIconMap method and sets the weather icon property to the
    //given weather condition and day cycle
    public WeatherIcon(String condition,String dayCycle)
    {
        try 
        {
            this.weatherIconMap=new HashMap<>();
           // String imgUri=Paths.get(Setup.getSetupFilePath()+"/icons/wsymbol_0999_unknown.png").toUri().toString();
          //  weatherIcon=new Image(imgUri,64,64,true,false);
            loadIconMap();
            setWeatherIcon(condition,dayCycle);
        }
        catch(InvalidPathException e)
        {
            System.err.println(e);
        }
    }
    
    public Image getWeatherIcon()
    {
       return weatherIcon;
    }
    //only used within the class, therefore access is limited to private
    //parses the JSONObject retrieved from the file mapping the icon files and builds
    //the weatherIconMap hashmap
    private void parseIcons(JSONObject setupObject)
    {
            JSONObject iconMap = (JSONObject)setupObject.get("icons");
            for (Object key:iconMap.keySet())
            {
                JSONArray value=(JSONArray)iconMap.get((String)key);
                JSONObject values=(JSONObject)value.get(0);
                List<String> icons=new ArrayList<>();
                icons.add((String)values.get("day")); //day icon at index 0
                icons.add((String)values.get("night")); //night icon at index 1
                weatherIconMap.put((String)key, icons);
            }
    }
    //only used within the class, therefore access is limited to private
    //Loads the icon map file and passes the JSON object to the parseIcons method
    private void loadIconMap()
    {
        JSONObject setupObject;
        try
        {
            JSONParser parser=new JSONParser();
            File iconMapFile=new File(Setup.getSetupFilePath()+"/icons/iconMap/weatherIconMap.json");
            setupObject=(JSONObject)parser.parse(new FileReader(iconMapFile));
            parseIcons(setupObject);
        }
        catch (IOException | ParseException e)
        {
           System.err.println(e);
        }
    }
    //only used within the class, therefore access is limited to private
    //sets the weatherIcon property according to the weather condition and day cycle
    private void setWeatherIcon(String condition,String dayCycle)
    {
        try
        {
            if (weatherIconMap.containsKey(condition))
            {
                List<String> icons=weatherIconMap.get(condition);
                String icon=dayCycle.equals("day")?icons.get(DAY):icons.get(NIGHT);
                String imgUri=Paths.get(Setup.getSetupFilePath()+"/icons/"+icon+".png").toUri().toString();
                weatherIcon=new Image(imgUri,64,64,true,false);
            }
            else
            {
                //catches the case if condition is unknown
                String imgUri=Paths.get(Setup.getSetupFilePath()+"/icons/wsymbol_0999_unknown.png").toUri().toString();
                weatherIcon=new Image(imgUri,64,64,true,false);
            }
        }
        catch(InvalidPathException e)
        {
            System.err.println(e);
        }
    }
}
