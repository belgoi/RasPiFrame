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
package raspiframe.sleep;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import raspiframe.utilities.Setup;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 *  Runs a separate thread to monitor the time and to turn on or off the backlight to the screen. 
 *  The script being ran issues the command to turn the backlight on or off on the  official Raspberry Pi display 
 *  from the Raspberry Pi Foundation. There are 2 scripts, turnBacklighton and turnBacklightoff.
 *  They run the commands: 
 *      sudo bash -c "echo 1 > /sys/class/backlight/rpi_backlight/bl_power" to turn off the backlight
 *      sudo bash -c "echo 0 > /sys/class/backlight/rpi_backlight/bl_power" to turn on the backlight
 *  If a different screen is used then different scripts will probably have to be used.  
 */
public class Sleep implements Observable
{
    public static boolean isAsleep; 
    private List<SleepListener> listeners;
    public Sleep()
    {
        listeners=new ArrayList<>();
    }
    //Overrides the 3 methods of the Observable interface.  Provides for the ability to add/remove
    //listeners and to push messages to all listeners. 
    @Override
    public void addListener(SleepListener object)
    {
        listeners.add(object);
    }
    @Override
    public void removeListener(SleepListener object)
    {
        if(listeners.contains(object))
            listeners.remove(object);
    }
    @Override
    public void onEvent(String eventMsg)
    {
        for(SleepListener listener:listeners)
        {
            listener.onAction(eventMsg);
        }
    }
    public void scheduleSleep(LocalTime time_to_sleep,LocalTime time_to_wake)
    {
        class PutToSleep implements Runnable
        {
            private final LocalTime timeToSleep;
            private final LocalTime timeToWake;
            private LocalDateTime wakeUpTime;
            private LocalDateTime sleepTime;
            private LocalDateTime lastSleep;
            LocalDateTime currentTime;
            PutToSleep(LocalTime timeToSleep,LocalTime timeToWake)
            {
                this.timeToSleep=timeToSleep;
                this.timeToWake=timeToWake;
                LocalDate yesterday = LocalDate.now().minusDays(1);
                //this.lastSleep=yesterday;
                this.lastSleep=LocalDateTime.of(yesterday, timeToSleep);
                setSleepSchedule();
                
            }


           private void setSleepSchedule()
           {
                LocalDate today=LocalDate.now();
                LocalDate yesterday = today.minusDays(1);
                LocalDate tomorrow=today.plusDays(1);
                //wakeup is normally scheduled for next day 
                if (timeToWake.isBefore(timeToSleep))
                {
                    if (LocalTime.now().isAfter(LocalTime.MAX) && LocalTime.now().isBefore(timeToWake))
                    {
                        //in case sleep gets interrupted, sleep can resume
                        this.sleepTime=timeToSleep.atDate(yesterday);
                        this.wakeUpTime=timeToWake.atDate(today);
                    }
                    else
                    {
                        //sleep went uninterrupted, normal scheduling can occur
                        this.sleepTime=timeToSleep.atDate(today);
                        this.wakeUpTime=timeToWake.atDate(tomorrow);
                    }
                }
                //Sleep and wake are normally scheduled the same day
                //timeToSleep->20:30 is before time to wake->22:30 occurs if sleep goes uninterrupted from previous day
                else if (lastSleep.toLocalDate().equals(yesterday))
                {
                    this.sleepTime=timeToSleep.atDate(today);
                    this.wakeUpTime=timeToWake.atDate(today);
                }
                //time to sleep is sleep is before time to wake, occurs when sleep gets interrupted and time is after midnight
                //making time to wake be before time to sleep
                else if (lastSleep.toLocalDate().equals(today))
                {
                    //if lastSleep occurred after midnight but before timeToWake i.e. sleep and wake happen same day
                    if(lastSleep.toLocalTime().isAfter(LocalTime.MAX) && LocalTime.now().isBefore(timeToWake))
                    {
                        //want to make sure that sleep for today is scheduled
                        this.sleepTime=timeToSleep.atDate(today);
                        //and the next wakeuptime would be the next day
                        this.wakeUpTime=timeToWake.atDate(tomorrow);
                    }
                    else
                    {
                        //normal schedule applies
                        this.sleepTime=timeToSleep.atDate(tomorrow);
                        this.wakeUpTime=timeToWake.atDate(tomorrow);
                    }
                }                   
            }
           @Override
            public void run()
            {
                while (true)
                {
                    currentTime=LocalDateTime.now();
                        if (currentTime.isAfter(this.sleepTime) && currentTime.isBefore(wakeUpTime)&&!isAsleep)
                        {
                            isAsleep=true;
                           // lastSleep=LocalDate.now();
                            lastSleep=LocalDateTime.now();
                            try
                            {                            
                                if(Setup.os().equals("Linux"))
                                {
                                    //path to linux script to turn off the backlight on the official
                                    //raspberry pi screen
                                    String scriptPath=new String("/home/pi/RasPiFrame/scripts/turnBacklightOff");
                                    Runtime rt = Runtime.getRuntime();
                                    //execute script
                                    Process proc=rt.exec(scriptPath);
                                }
                            }
                            catch (Exception e)
                            {
                                System.err.println(e);
                            }
                            onEvent("Sleeping");
                            System.out.println("going to sleep");
                        }
                        else if(currentTime.isAfter(wakeUpTime) && isAsleep)
                        {
                            isAsleep=false;
                            setSleepSchedule();
                            
                            try
                            {
                                if(Setup.os().equals("Linux"))
                                {
                                    //path to linux script to turn on the backlight on the official
                                    //raspberry pi screen
                                    String scriptPath=new String("/home/pi/RasPiFrame/scripts/turnBacklightOn");
                                    Runtime rt = Runtime.getRuntime();
                                    //execute script
                                    Process proc=rt.exec(scriptPath);
                                }
                            }
                            catch (Exception e)
                            {
                                System.err.println(e);
                            }
                            onEvent("WakingUp");
                            System.out.println("waking up");
                        }
                }   
        }
        }
        try
        {
            //start the thread 
            Thread t=new Thread(new PutToSleep(time_to_sleep,time_to_wake));
            t.setName("Sleep & wakeup thread");
            t.start();
        }
        catch (Exception e)
        {
            System.err.println("Sleep thread encountered an exception");
            System.err.println(e);
        }
    }

    
}
