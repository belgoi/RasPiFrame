package raspiframe.utilities;

import java.util.List;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.Set;
import java.util.HashSet;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class ImageLoader
{
    //defines and instantiates the class FilenameFilter for the File object
    FilenameFilter imageFilter = new FilenameFilter(){
        @Override
        public boolean accept(File file,String name)
        {
            String extension=new String();
            extension=name.substring(name.lastIndexOf("."));
            if (extension.equals(".jpg") || extension.equals(".png"))
                return true;
            
            return false;
        }
    };    
    public ImageLoader(){}
    public boolean validateFile(String filename)
    {
        //Makes sure the passed filename exists, returns a boolean
        File file=new File(Setup.imageDirectory()+"/" + filename);
        return(file.exists());
    }
    public void Load(ObservableList<myImageView> images,String[] fileNames)
    {
        //Loads the image files into the observableList 
        
        //builds the uri to the image path that is needed by the Image object
        String imgUri=Paths.get(Setup.imageDirectory()).toUri().toString();
        
        //iterate through the filenames and load each image into the observableList triggering OnChange event 
        //defined in RasPiFrameController
        for (String file:fileNames)      
            if (validateFile(file))
                images.add(new myImageView(file,new Image(imgUri+file,Setup.screenWidth(),Setup.screenHeight(),false,false)));
    }
    public String[] getFileNames()
    {
        //Returns an array with all of the .png and .jpg files in the image directory
        
        String[] imgFileNames;
        int i=0;
        //set directory to the image directory plus the ending / or \ depending on OS
        File folder = new File(Setup.imageDirectory()+(Setup.OS().equals("Windows")?"\\":"/"));
        //Filter out just .jpg and .png files
        File[] imgFiles=folder.listFiles(imageFilter);
        imgFileNames=new String[imgFiles.length];
        for (File file:imgFiles)
            imgFileNames[i++]=file.getName();
        
        return imgFileNames;
    }   
}
