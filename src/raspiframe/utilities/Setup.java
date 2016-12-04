package raspiframe.utilities;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 *  Reads the config file -> config.json and defines the system static variables used through the app
 */
public final class Setup
{
    private final static StringProperty IMAGEDIRECTORY=new SimpleStringProperty() ;
    private final static DoubleProperty DISPLAYTIME=new SimpleDoubleProperty();
    private final static DoubleProperty FADEINLENGTH=new SimpleDoubleProperty();
    private final static DoubleProperty FADEOUTLENGTH=new SimpleDoubleProperty();
    private final static StringProperty LOCATION = new SimpleStringProperty();
    private final static StringProperty TIME_TO_SLEEP = new SimpleStringProperty();
    private final static StringProperty TIME_TO_WAKE=new SimpleStringProperty();
    private final static StringProperty OS=new SimpleStringProperty();
    private final static DoubleProperty SCREENWIDTH = new SimpleDoubleProperty();
    private final static DoubleProperty SCREENHEIGHT = new SimpleDoubleProperty();
            
    public Setup(String configFilePath)
    {
        //read the config file
        readJsonFile(configFilePath);

        OS.set(System.getProperty("os.name"));
        if(System.getProperty("os.name").contains("Windows"))
            OS.set("Windows");
        else if (System.getProperty("os.name").equals("Linux"))
            OS.set("Linux");
        
        Rectangle2D screenDimensions=Screen.getPrimary().getVisualBounds();
        SCREENWIDTH.set(screenDimensions.getWidth());
        SCREENHEIGHT.set(screenDimensions.getHeight());
        
    }
        public static final double screenWidth()
        {
            return SCREENWIDTH.get();
        }
        public static final double screenHeight()
        {
            return SCREENHEIGHT.get();
        }
         public static final String OS()
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
         public static final String weatherLocation()
         {            
             return LOCATION.get();
         }
         public static final String timeToWake()
         {
             return TIME_TO_WAKE.get();
         }
         public static final String timeToSleep()
         {
             return TIME_TO_SLEEP.get();
         }

         private void readJsonFile(String configFilePath)
         {
            JSONObject setupObject=new JSONObject();
             try
            {
                JSONParser parser=new JSONParser();
                File configFile=new File(configFilePath);
                setupObject=(JSONObject)parser.parse(new FileReader(configFile));
                parseConfig(setupObject);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
         }
     private void parseConfig(JSONObject setupObject)
     {
         //JSON object is stored as a HashMap but each value must be cast into the correct type
         IMAGEDIRECTORY.set((String)setupObject.get("image_directory"));
         DISPLAYTIME.set((Double)setupObject.get("display_time"));
         FADEINLENGTH.set((Double)setupObject.get("fadein"));
         FADEOUTLENGTH.set((Double)setupObject.get("fadeout"));
         LOCATION.set((String)setupObject.get("location"));
         TIME_TO_SLEEP.set((String)setupObject.get("time_to_sleep"));
         TIME_TO_WAKE.set((String)setupObject.get("time_to_wake"));
     }

}
