package raspiframe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import raspiframe.utilities.Setup;
import javafx.scene.layout.StackPane;

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
        FXMLLoader loader=new FXMLLoader(getClass().getResource("RasPiFrame.fxml"));       
        //setup the model
        PhotoFrameModel model = new PhotoFrameModel();
        //setup the controller and pass it a reference to the model
        PhotoFrameController controller = new PhotoFrameController(model);
        //attach the controller to the view (FXML)
        loader.setController(controller);

        AnchorPane root = (AnchorPane)loader.load();
    //    root.getChildren().add
//        BorderPane root=new BorderPane();
  //      FXMLLoader loader=new FXMLLoader(getClass().getResource("RasPiFrame.fxml"));
    //    root.setCenter(loader);

        

      //  loader.setRoot(root);
        //loader.setController(controller);
        //loader.load();

       // AnchorPane root = FXMLLoader.load(getClass().getResource("RasPiFrame.fxml"));
        /***
         * all test code below
         */

        /*end test code*/
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
