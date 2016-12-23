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
public class Sleep 
{
        

    public void putToSleep(String time_to_sleep,String time_to_wake)
    {
        class PutScreenToSleep implements Runnable
        {

            private final int sleepHour;
            private final int sleepMin;
            private final int wakeHour;
            private final int wakeMin;
            
            PutScreenToSleep(String timeToSleep,String timeToWake)
            {
                sleepHour=Integer.parseInt(timeToSleep.substring(0,timeToSleep.indexOf(":")));
                sleepMin=Integer.parseInt(timeToSleep.substring(timeToSleep.indexOf(":")+1,timeToSleep.length()));
                wakeHour=Integer.parseInt(timeToWake.substring(0,timeToWake.indexOf(":")));
                wakeMin=Integer.parseInt(timeToWake.substring(timeToWake.indexOf(":")+1,timeToWake.length()));

           }
            public void run()
            {
                boolean awake=true;
                boolean asleep=false;
                LocalTime time;
                while (true)
                {
                    time=LocalTime.now();
                        if ((time.getHour()==sleepHour) && (time.getMinute() == sleepMin) && awake)
                        {
                            awake=false;
                            asleep=true;
                            try
                            {
                                //path to linux script to turn off the backlight
                                String scriptPath=new String("/home/pi/RasPiFrame/scripts/turnBacklightOff");
                                Runtime rt = Runtime.getRuntime();
                                //execute script
                                Process proc=rt.exec(scriptPath);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            System.out.println("putting to sleep");

                        }
                        if ((time.getHour()==wakeHour) && (time.getMinute() == wakeMin) && asleep)
                        {
                            asleep=false;
                            awake=true;
                            try
                            {
                                //path to linux script to turn on the backlight
                                String scriptPath=new String("/home/pi/RasPiFrame/scripts/turnBacklightOn");
                                Runtime rt = Runtime.getRuntime();
                                //execute script
                                Process proc=rt.exec(scriptPath);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            System.out.println("waking up");

                        }
                }   
        }
        }
        //start the thread 
        Thread t=new Thread(new PutScreenToSleep(time_to_sleep,time_to_wake));
        t.setName("Sleep & wakeup thread");
        t.start();
    }
    
}
