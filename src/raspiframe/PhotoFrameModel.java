package raspiframe;
import raspiframe.utilities.Setup;
import raspiframe.utilities.ImageLoader;
import raspiframe.utilities.Sleep;
import raspiframe.utilities.DirectoryWatcher;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import raspiframe.utilities.myImageView;
import javafx.collections.FXCollections;
import java.util.List;
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
        }
        public ObservableList<myImageView> getObservablePhotoList()
        {
            return observablePhotoList;
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
