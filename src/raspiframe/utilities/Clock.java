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
        //combine the hour in 12 hour format & the minute and return the string
        return   (hour > 12) ? hour-12 +":" + minute :hour + ":" + minute;  
    }
    public String getDate()
    {
        String Month=LocalDate.now().getMonth().toString();
        int Day=LocalDate.now().getDayOfMonth();
        //return in format December 18
        return formatDate(Month + " " + Day);
    }
    private String formatDate(String dateString)
    {
        //since date is returned by the LocalDate object in all caps
        //it needs to be formatted so that only the first letter is capitalized
        String lowerCaseDate=dateString.toLowerCase();
        char[] firstLetter=dateString.toCharArray();
        String fl=Character.toString(firstLetter[0]);
        //get only the first letter since it already upper case, and convert rest of string to lower case
        return Character.toString(firstLetter[0]) + lowerCaseDate.substring(1);
    }
}
