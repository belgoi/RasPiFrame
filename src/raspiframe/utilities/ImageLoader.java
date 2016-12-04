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

import javafx.scene.image.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import javafx.collections.ObservableList;



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
            String extension;
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