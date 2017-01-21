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
 * watches the image directory for changes   
 */
public class DirectoryWatcher
{
    public void watchImgDirectory(String imgDirectory,ObservableList<myImageView> observablePhotoList)
    {
        //Have to embed the class inside the method since parameters have to be passed in
        class watchDirectory implements Runnable
        {
            private String directory;
            private Path path;
            private WatchService watchService;
            private WatchKey watchKey;
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
                    System.err.println("DirectoryWatcher: watchDirectory: " + e);
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
                        System.err.println("DirectoryWatcher: Interrupted Exception has been thrown by watcher thread");
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
                        else if(eventKind == ENTRY_CREATE)
                        {
                            try{
                            ImageLoader il=new ImageLoader();
                            String[] imgs={file.getFileName().toString()};
                            //need to delay loading the images just long enough to allow the file to finish writing    
                            Thread.sleep(500);
                            il.Load(observablePhotoList, imgs);
                           
                            }
                            catch(InterruptedException e)
                            {
                                System.err.println("DirectoryWatcher: FileAdded: " + e);
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
            try
            {
                Thread t=new Thread(new watchDirectory(imgDirectory,observablePhotoList));
                t.setName("Directory Watcher thread");
                t.start();
            }
            catch(Exception e)
            {
                System.err.println("Directory Watcher thread has encountered an exception");
                System.err.println(e);
                        
            }
     }
}
