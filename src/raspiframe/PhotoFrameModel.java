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
import java.time.LocalDate;
import java.time.LocalTime;
import raspiframe.utilities.Setup;
import raspiframe.utilities.ImageLoader;
import raspiframe.utilities.Sleep;
import raspiframe.utilities.DirectoryWatcher;
import raspiframe.utilities.Clock;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import raspiframe.utilities.myImageView;
import javafx.collections.FXCollections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class PhotoFrameModel
{
        private Setup setup;
        private final String os;
        private ObservableList<myImageView> observablePhotoList;
        private List<myImageView>photoList;
        private StringProperty timeString=new SimpleStringProperty();
        private StringProperty dateString = new SimpleStringProperty();
        public PhotoFrameModel()
        {
            os=System.getProperty("os.name");
            //Instantiate the Setup object and set the values for the static variables
            new Setup(getSetupFilePath());
            //underlying arraylist for the observable photo list
            photoList=new ArrayList();
            //the observableList for all of the photos 
            observablePhotoList=FXCollections.observableList(photoList);
            //sets when to put the screen to sleep
            setSleep();
            startClock();
        }
        public StringProperty getDate()
        {
            Clock clock=new Clock();
            dateString.set(clock.getDate());
            return dateString;
        }
        public StringProperty getTime()
        {
            return timeString;
        }
        public ObservableList<myImageView> getObservablePhotoList()
        {
            return observablePhotoList;
        }
        public void startClock()
        {
           TimerTask task=new TimerTask()
           {
                @Override
                public void run()
                {
                   Clock clock=new Clock();
                   timeString.set(clock.getTime());
                   if(LocalTime.now().equals(LocalTime.MIDNIGHT))
                   {
                        dateString.set(clock.getDate());
                   }
                //   Clock clock=new Clock();
                  //  timeString.set(clock.getTime());
                }
            };
                  int startMin;
                  LocalTime time=LocalTime.now();
        startMin=time.getMinute();        
           Timer timer=new Timer();
        timer.scheduleAtFixedRate(task, startMin,1000);
        }
        public void setSleep()
        {
            //sets when the screen goes to sleep and wakes up
            //Only works on the raspberry pi
            Sleep sleep=new Sleep();
            if(Setup.OS().equals("Linux"))
                sleep.putToSleep(Setup.timeToSleep(), Setup.timeToWake());
        }
        public void loadImgFiles()
        {
            //load the image files. 
            //this is the tie in for the controller.  when the images are loaded into observableList
            //the controller's OnChange event is called.  
             ImageLoader imageLoader=new ImageLoader();
             imageLoader.Load(observablePhotoList,imageLoader.getFileNames());     
             //starts the directory watcher thread after the pictures have been loaded
             DirectoryWatcher watcher=new DirectoryWatcher();
                    watcher.watchImgDirectory(Setup.imageDirectory(),observablePhotoList);
        }

        private String getSetupFilePath()
        {
            String path=new String();
            if (os.equals("Linux"))
                path="/home/pi/RasPiFrame/config/config.json";
            else if (os.contains("Windows"))
                path="C:/RasPiFrame/config.json";
        
            return path;
        }          
}
