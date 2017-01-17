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
import raspiframe.utilities.Listener;
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
    private List<Listener> listeners;
    public Sleep()
    {
        listeners=new ArrayList<>();
    }
    @Override
    public void registerListener(Listener object)
    {
        listeners.add(object);
    }
    @Override
    public void removeListener(Listener object)
    {
        if(listeners.contains(object))
            listeners.remove(object);
    }
    @Override
    public void onEvent()
    {
        for(Listener listener:listeners)
        {
            listener.onAction(isAsleep);
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
            private LocalDate lastSleep;
            PutToSleep(LocalTime timeToSleep,LocalTime timeToWake)
            {
                this.timeToSleep=timeToSleep;
                this.timeToWake=timeToWake;
                LocalDate yesterday = LocalDate.now().minusDays(1);
                this.lastSleep=yesterday;
                setSleepSchedule();
                
            }


           private void setSleepSchedule()
           {
                LocalDate today=LocalDate.now();
                LocalDate yesterday = today.minusDays(1);
                LocalDate tomorrow=today.plusDays(1);
             
                if (timeToWake.isBefore(timeToSleep))
                {
                    //Wake up is next day
                    this.sleepTime=timeToSleep.atDate(today);
                    this.wakeUpTime=timeToWake.atDate(tomorrow);
                }
                else if (lastSleep.equals(yesterday))
                {
                    //Wake up is same day
                    this.sleepTime=timeToSleep.atDate(today);
                    this.wakeUpTime=timeToWake.atDate(today);
                }
                else if (lastSleep.equals(today))
                {
                    this.sleepTime=timeToSleep.atDate(tomorrow);
                    this.wakeUpTime=timeToWake.atDate(tomorrow);
                }                   
            }
            public void run()
            {
                LocalDateTime currentTime;
                while (true)
                {
                    currentTime=LocalDateTime.now();
                        if (currentTime.isAfter(this.sleepTime) && currentTime.isBefore(wakeUpTime)&&!isAsleep)
                        {
                            isAsleep=true;
                            lastSleep=LocalDate.now();
                            onEvent();
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
                            System.out.println("putting to sleep");
                        }
                        else if(currentTime.isAfter(wakeUpTime) && isAsleep)
                        {
                            isAsleep=false;
                            setSleepSchedule();
                            onEvent();
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
