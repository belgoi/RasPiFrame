package raspiframe.utilities;
import java.time.LocalTime;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
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
        t.start();
    }
    
}
