package raspiframe.utilities;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javafx.collections.ObservableList;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 *  Purpose: watches the image directory for changes   
 */
public class DirectoryWatcher
{
    public void watchImgDirectory(String imgDirectory,ObservableList<myImageView> observablePhotoList)
    {
        //Have to embed the class inside the method since parameters have to be passed in
        class watchDirectory implements Runnable
        {
            String directory;
            private Path path;
            WatchService watchService;
            WatchKey watchKey;
            private ObservableList<myImageView> observablePhotoList;
            watchDirectory(String imgDirectory,ObservableList<myImageView> observablePhotoList)
            {
                this.directory=imgDirectory;
                this.path=Paths.get(imgDirectory);
                this.observablePhotoList=observablePhotoList;
                try
                {
                    //setup the watch service and and register the directory to watch for changes
                    watchService=FileSystems.getDefault().newWatchService();
                    watchKey=path.register(watchService,ENTRY_DELETE,ENTRY_CREATE);
                }
                catch(IOException e)
                {
                    System.err.println(e);
                    return;
                }
            }
            public void run()
            {
                while(true)
                {
                    WatchKey key;
                    try
                    {
                        key=watchService.take();
                    }
                    catch(InterruptedException e)
                    {
                        System.err.println("Interrupted Exception has been thrown by watcher thread");
                        return;
                    }
                                //get the events that have been generated 
                    List<WatchEvent<?>> eventList=key.pollEvents();
                    //iterate through the events to determine what events have been generated 
                    for (WatchEvent<?> event: eventList)
                    {
                        WatchEvent.Kind<?> eventKind = event.kind();
                        //ignore the OVERFLOW event 
                        if(eventKind == OVERFLOW)
                            continue;
                        WatchEvent<Path> pathEvent=(WatchEvent) event;
                        //get the file that was added or deleted
                        Path file=pathEvent.context();
                        if(eventKind == ENTRY_DELETE)
                        {
                            for(int i=0;i<observablePhotoList.size();i++)
                                if (file.getFileName().toString().equals(observablePhotoList.get(i).getImageUrl()))
                                    observablePhotoList.remove(i);
                        }
                        else if(eventKind == ENTRY_CREATE)//changed from ENTRY_CREATE
                        {
                            try{
                            ImageLoader il=new ImageLoader();
                            String[] imgs={file.getFileName().toString()};
                            //need to delay loading the images just long enough to allow the file to finish writing    
                            Thread.sleep(100);
                            il.Load(observablePhotoList, imgs);
                            }
                            catch(InterruptedException e)
                            {
                                System.err.println(e);
                            }
                        }

                    }
                    //must reset flag to continue checking for changes 
                    boolean validKey=key.reset();
                    if(!validKey)
                        break;
                }
            }
        }
                Thread t=new Thread(new watchDirectory(imgDirectory,observablePhotoList));
                t.start();
     }
}
