package raspiframe.utilities;
import java.time.LocalTime;
import java.time.LocalDate;
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
        
        return   (hour > 12) ? hour-12 +":" + minute :hour + ":" + minute;  
    }
    public String getDate()
    {
        String Month=LocalDate.now().getMonth().toString();
        int Day=LocalDate.now().getDayOfMonth();
        return formatDate(Month + " " + Day);
    }
    private String formatDate(String dateString)
    {
        String lowerCaseDate=dateString.toLowerCase();
        char[] firstLetter=dateString.toCharArray();
        String fl=Character.toString(firstLetter[0]);
        return Character.toString(firstLetter[0]) + lowerCaseDate.substring(1);
    }
}
