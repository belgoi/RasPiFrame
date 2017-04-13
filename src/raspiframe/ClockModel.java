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
package raspiframe;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class ClockModel
{
    private Timer timer;
    private final StringProperty timeString=new SimpleStringProperty();
    private final StringProperty dateString = new SimpleStringProperty();
    private ClockTask clockTask;
    
    public ClockModel(){
        timer=new Timer();
        scheduleClockUpdate();
        
    }
     public StringProperty dateProperty()
        {
           // Clock clock=new Clock();
           // dateString.set(getDate());
            return dateString;
        }
        public StringProperty timeProperty()
        {
            return timeString;
        }
        public String getTime()
        {
            String time;
            try{
            LocalTime now=LocalTime.now();
            //formats the time in 12 hour format
            DateTimeFormatter formatter =DateTimeFormatter.ofPattern("h:mm");
            //returns the time in format 1:12
            time=now.format(formatter);
            }
            catch (DateTimeException e){
                System.err.println("Error formatting time " + e);
                time="";
            }
            return time;
    }
    public String getDate()
    {
        String date;
        try{
            LocalDate now=LocalDate.now();
            int day=now.getDayOfMonth();
            //formats the month to the short form, ie Jan,Feb,Mar,etc
            String shortMonth=now.format(DateTimeFormatter.ofPattern("MMM"));
            //formates the day of week to the long form, ie Monday,Tuesday,etc
            String dayOfWeek=now.format(DateTimeFormatter.ofPattern("EEEE"));
            //return in format Sunday, Dec 18
            date= dayOfWeek +", " +shortMonth + " "+day;
        }
        catch (DateTimeException e){
            System.err.println("Error formatting time " + e);
            date="";
        }
        return date;
        }
    private class ClockTask extends TimerTask
    {
        @Override
        public void run()
        {
            Thread.currentThread().setName("Clock");
            updateClock();
        }
    }
    
    private void updateClock(){
        timeString.set(getTime());
        dateString.set(getDate());
    }
    private void scheduleClockUpdate(){
        int oneSecond=1000;
        try{
            timer=new Timer("ClockUpdateTimer");
            clockTask=new ClockTask();
            //schedule update every 15 seconds
            timer.scheduleAtFixedRate(clockTask,0,1000);
        }
        catch(Exception e)
        {
            System.err.println("Clock thread has encountered an error");
            System.err.println(e);
        }
    }
}
