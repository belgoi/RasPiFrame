package raspiframe.utilities;
import java.time.LocalTime;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class Clock
{
    public String getTime()
    {
        int hour=LocalTime.now().getHour();
        //format minute 
        String minute=(LocalTime.now().getMinute()<10)? "0"+LocalTime.now().getMinute():Integer.toString(LocalTime.now().getMinute());
        
        return   (hour > 12) ? hour-12 +":" + minute +" PM" :hour + ":" + minute +" AM";  
    }
}
