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
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.util.List;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.animation.SequentialTransition;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.util.Duration;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import raspiframe.utilities.myImageView;
import raspiframe.utilities.Setup;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class PhotoFrameController implements Initializable
{
    @FXML private AnchorPane photoFrameRoot;
    private PhotoFrameModel model;
    private  List<myImageView> pictures;

    @FXML private Text clock;
    @FXML private BorderPane borderPane;
    
    
    public PhotoFrameController()
    {       
        pictures=new ArrayList();
    }
    public PhotoFrameController(PhotoFrameModel model)
    {
        this();
        this.model=model;
    }
  
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setBindings();
        model.getObservablePhotoList().addListener(new ListChangeListener()
        {
            @Override
            public void onChanged(ListChangeListener.Change change)
            {
                //have to iterate through the changes
                while(change.next())
                    //only interested in the pictures that have been added or deleted
                    if((change.wasAdded()) || change.wasRemoved())
                        //copy the pictures over to the array that will be used in the slideshow
                        pictures=model.getObservablePhotoList();  
            }
        });
        
        //Load the photos for the slideshow
        model.loadImgFiles();
        startSlideShow();
        //clockLayout.layoutXProperty().set(0);
        //clockLayout.layoutYProperty().set(1600);


        
    }
    public void setBindings()
    {
        clock.textProperty().bindBidirectional(model.displayClock());
        borderPane.prefWidthProperty().bind(photoFrameRoot.widthProperty());
        borderPane.prefHeightProperty().bind(photoFrameRoot.heightProperty());
    }
    public void startSlideShow()
    {
        //Sequential Transition to add the fade in, pause, and fade out transitions for each slide
        SequentialTransition slides;
        //Sequential Transition to play each slide
        SequentialTransition slideshow= new SequentialTransition();
        //shuffle the pictures so the pictures won't always be displayed in the same order
        Collections.shuffle(pictures);
        for (ImageView picture:pictures)
        {
            slides=new SequentialTransition();
            //set fade in transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(Setup.fadeInDuration()),picture);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
            //set how long to display picture
            PauseTransition showPicture = new PauseTransition(Duration.seconds(Setup.PauseDuration()));
            //set fade out transition
            FadeTransition fadeOut=new FadeTransition(Duration.millis(Setup.fadeOutDuration()),picture);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
            //add transitions to the slide
            slides.getChildren().addAll(fadeIn,showPicture,fadeOut);
            //since all pictures are stacked on the screen at once, 
            //the opacity has to be set to 0
            picture.setOpacity(0);
            //add the pictures to the photo frame
            photoFrameRoot.getChildren().add(picture);
            //add the slides to the slideshow
            slideshow.getChildren().add(slides);
        }
        //go through the slideshow once then restart and refresh with any changes
        slideshow.setCycleCount(1);
        slideshow.setOnFinished(new EventHandler<ActionEvent>()
       {
           public void handle(ActionEvent event)
           {
               //clear the child nodes from the photoFrame root
               photoFrameRoot.getChildren().clear();
               //Restart the slideshow to randomize the order of the pictures
               startSlideShow();    
           }
       });
        slideshow.play();
        
    }
    
}

