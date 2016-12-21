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
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * Clock class setups the clock and date for the model.  
 * provides the methods getTime,getDate, 
 */
public class Clock
{
    public String getTime()
    {
        LocalTime now=LocalTime.now();
        //formats the time in 12 hour format
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("h:mm");
        //returns the time in format 1:12
        return now.format(formatter);
    }
    public String getDate()
    {
        LocalDate now=LocalDate.now();
        int day=now.getDayOfMonth();
        //formats the month to the short form, ie Jan,Feb,Mar,etc
        String shortMonth=now.format(DateTimeFormatter.ofPattern("MMM"));
        //formates the day of week to the long form, ie Monday,Tuesday,etc
        String dayOfWeek=now.format(DateTimeFormatter.ofPattern("EEEE"));
        //return in format Sunday, Dec 18
        return dayOfWeek +", " +shortMonth + " "+day;
    }
}
