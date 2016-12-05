package raspiframe;
import java.net.URL;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import java.util.TimerTask;
import javafx.application.Platform;
import java.util.Timer;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */


public class ClockController implements Initializable
{
            LocalTime time=LocalTime.now();
    @FXML 
    private Label clock;
    
    TimerTask task=new TimerTask(){
        @Override
        public void run(){
            Platform.runLater(new Runnable(){
                public void run()
                {
                    clock.setText(time.toString());
                }
            });
        }
    };
    
       public void initialize(URL url, ResourceBundle rb)
       {
        int startMin;

        startMin=time.getMinute();
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(task, startMin,1000);
    }
}
