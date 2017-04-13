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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import raspiframe.utilities.Setup;
import javafx.scene.text.Font;
import raspiframe.sleep.Sleep;

/**
 *
 *  RasPiFrame
 *  A digital picture frame for the Raspberry Pi.  
 *  @author David Hinchliffe <belgoi@gmail.com>
 * 
 *  It reads images from a specified image directory and 
 *  presents them in a slideshow type format.  The images are presented randomly and are updated as new 
 *  images are added or deleted.  The main motivation behind this project is to not only work with images that 
 *  have been added to a directory remotely i.e some cloud client but to also update those images as they are 
 *  added without requiring a restart.  
 *  
 */
public class RasPiFrame extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        //load the font 
        Font.loadFont(RasPiFrame.class.getResource("fonts/OpenSans-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(RasPiFrame.class.getResource("fonts/OpenSans-ExtraBold.ttf").toExternalForm(),10);
    
        FXMLLoader loader=new FXMLLoader(getClass().getResource("RasPiFrame.fxml"));       
        //setup the model
        PhotoFrameModel model = new PhotoFrameModel();
            //sets when the screen goes to sleep and wakes up
            //Only works on the raspberry pi
            Sleep sleep=new Sleep();
            sleep.scheduleSleep(Setup.timeToSleep(), Setup.timeToWake());
        WeatherModel weatherModel=new WeatherModel(sleep);
        ClockModel clockModel=new ClockModel();
        //setup the controller and pass it a reference to the model
        PhotoFrameController controller = new PhotoFrameController(model,weatherModel,clockModel);
        //attach the controller to the view (FXML)
        loader.setController(controller);

        AnchorPane root = (AnchorPane)loader.load();

        Scene scene = new Scene(root,Setup.screenWidth(),Setup.screenHeight());

        stage.setScene(scene);
        stage.show();
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
