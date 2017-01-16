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
package raspiframe.utilities;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.time.LocalTime;
import java.time.DateTimeException;
import java.time.LocalDate;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 *  Reads the config file -> config.json and defines the system static variables used through the app
 *  a setup .json file must present with the config variables.  This allows for a simple way to configure certain variables
 *  rather than hard coding them.  A sample file is below:
 *             {
 *	"image_directory": "/home/pi/RasPiFrame/Photos",
 *	"display_time": 5.0,
 *	"fadein": 1500.0,
 *	"fadeout": 2500.0,
 *	"time_to_sleep":"13:09",
 *	"time_to_wake":"13:11",
 *              "weather_api_key":"your key",
 *              "update_weather_interval":15
 *              }
 *  copy this into a plain text file substituting your own values. image directory can be any directory. You 
 *  can even setup a directory so that images are downloaded from a cloud server and be automatically inserted 
 *  into the slideshow. 
 * 
 *  Make sure the location to the file is changed in PhotoFrameModel's getSetupFilePath()
 *  
 */
public final class Setup
{
    private final static StringProperty IMAGEDIRECTORY=new SimpleStringProperty() ;
    private final static DoubleProperty DISPLAYTIME=new SimpleDoubleProperty();
    private final static DoubleProperty FADEINLENGTH=new SimpleDoubleProperty();
    private final static DoubleProperty FADEOUTLENGTH=new SimpleDoubleProperty();
    private final static BooleanProperty PRESERVE_ASPECT_RATIO = new SimpleBooleanProperty();
    private final static StringProperty LOCATION = new SimpleStringProperty();
    //private final static StringProperty TIME_TO_SLEEP = new SimpleStringProperty();
    //private final static StringProperty TIME_TO_WAKE=new SimpleStringProperty();
    private final static StringProperty OS=new SimpleStringProperty();
    private final static DoubleProperty SCREENWIDTH = new SimpleDoubleProperty();
    private final static DoubleProperty SCREENHEIGHT = new SimpleDoubleProperty();
    private final static StringProperty WEATHERAPIKEY = new SimpleStringProperty();
    private final static LongProperty UPDATEWEATHERINTERVAL = new SimpleLongProperty();
    private  static LocalTime TIME_TO_SLEEP= LocalTime.of(0,0);
    private  static LocalTime TIME_TO_WAKE=LocalTime.of(0, 0);
    private static BooleanProperty IS_ASLEEP = new SimpleBooleanProperty();

    //static iniatilizer to setup all of the values in the static class
    static
    {
        OS.set(System.getProperty("os.name"));
        if(System.getProperty("os.name").contains("Windows"))
            OS.set("Windows");
        else if (System.getProperty("os.name").equals("Linux"))
            OS.set("Linux");
        
        Rectangle2D screenDimensions=Screen.getPrimary().getVisualBounds();
        SCREENWIDTH.set(screenDimensions.getWidth());
        SCREENHEIGHT.set(screenDimensions.getHeight());
        //read the config file
        readJsonFile(getSetupFilePath() + "/config.json");
        LocalTime currentTime=LocalTime.now();
        if (currentTime.isAfter(timeToSleep()) && currentTime.isBefore(timeToWake()))
            IS_ASLEEP.set(false);
        else if(currentTime.isAfter(timeToWake()))
            IS_ASLEEP.set(true);
    }
        public static String getSetupFilePath()
        {
            String path=new String();
            if (os().equals("Linux"))
                path="/home/pi/RasPiFrame/config";
            else if (os().contains("Windows"))
                path="C:/RasPiFrame";

            return path;
        }
        public static final Long updateWeatherInterval()
        {
            return UPDATEWEATHERINTERVAL.get();
        }
        public static final String weatherApiKey()
        {
            return WEATHERAPIKEY.get();
        }
        public static final double screenWidth()
        {
            return SCREENWIDTH.get();
        }
        public static final double screenHeight()
        {
            return SCREENHEIGHT.get();
        }
         public static final String os()
         {
             return OS.get();
         }
         public static final String imageDirectory()
         {
             return IMAGEDIRECTORY.get();
         }
         public static final Double PauseDuration()
         {
             return DISPLAYTIME.get();
         }
         public static final Double fadeInDuration()
         {
             return FADEINLENGTH.get();
         }
         public static final Double fadeOutDuration()
         {
             return FADEOUTLENGTH.get();
         }
         public static final Boolean preserveAspectRatio()
         {
             return PRESERVE_ASPECT_RATIO.get();
         }
         public static final String weatherLocation()
         {            
             return LOCATION.get();
         }
         public static final LocalTime timeToWake()
         {
             return TIME_TO_WAKE;
         }
         public static final LocalTime timeToSleep()
         {
             return TIME_TO_SLEEP;
         }
         /*public static final boolean isAsleep()
         {
             return IS_ASLEEP.get();
         }
         public static final void setIsAsleep(boolean asleep)
         {
             IS_ASLEEP.set(asleep);
         }
*/
         private static LocalTime validateTime(String time)
         {
             //TODO: clean up 
                int hour=Integer.parseInt(time.substring(0,time.indexOf(":")));
                int min=Integer.parseInt(time.substring(time.indexOf(":")+1,time.length()));
                LocalTime convertTime;
                LocalTime parseTime;
               try 
               {
                    //convertTime=LocalTime.of(hour, min);
                   parseTime=LocalTime.parse(time);
               }
               catch(DateTimeException e)
               {
                   parseTime=LocalTime.of(0,0);
               }
                return parseTime;
         }
         private static void readJsonFile(String configFilePath)
         {
            JSONObject setupObject;
             try
            {
                JSONParser parser=new JSONParser();
                File configFile=new File(configFilePath);
                setupObject=(JSONObject)parser.parse(new FileReader(configFile));
                parseConfig(setupObject);
            }
            catch (IOException  | ParseException e)
            {
                System.err.println(e);
            }
         }
        /* private static void setSleep(LocalTime timeToSleep,LocalTime timeToWake)
         {
                LocalDate today=LocalDate.now();
                LocalDate tomorrow=today.plusDays(1);
                TIME_TO_SLEEP.atDate(today);
                
                if (timeToWake.isBefore(timeToSleep))
                    //Wake up is next day
                    TIME_TO_WAKE.atDate(tomorrow);
                else
                    //Wake up is same day
                    TIME_TO_WAKE.atDate(today);
         }*/
     private static void parseConfig(JSONObject setupObject)
     {
         //JSON object is stored as a HashMap but each value must be cast into the correct type
         IMAGEDIRECTORY.set((String)setupObject.get("image_directory"));
         DISPLAYTIME.set((Double)setupObject.get("display_time"));
         FADEINLENGTH.set((Double)setupObject.get("fadein"));
         FADEOUTLENGTH.set((Double)setupObject.get("fadeout"));
         PRESERVE_ASPECT_RATIO.set((Boolean)setupObject.get("preserve_aspect_ratio"));
         LOCATION.set((String)setupObject.get("location"));

         WEATHERAPIKEY.set((String)setupObject.get("weather_api_key"));
         UPDATEWEATHERINTERVAL.set((Long)setupObject.get("update_weather_interval"));
         //TODO: clean up
        //String sleepTime=((String)setupObject.get("time_to_sleep"));
         //String wakeupTime=((String)setupObject.get("time_to_wake"));
         //setSleep(validateTime(sleepTime),validateTime(wakeupTime));
             //validate time to sleep
            String time=((String)setupObject.get("time_to_sleep"));
         TIME_TO_SLEEP=(validateTime(time));
            //validate time to wake
            time=((String)setupObject.get("time_to_wake"));
         TIME_TO_WAKE=(validateTime(time));
     }

}
