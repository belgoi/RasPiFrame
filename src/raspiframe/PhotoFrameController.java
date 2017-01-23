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
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.util.List;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Group;
import javafx.animation.SequentialTransition;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.util.Duration;
import java.util.Collections;
import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import raspiframe.utilities.myImageView;
import raspiframe.utilities.Setup;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class PhotoFrameController implements Initializable
{
    @FXML private AnchorPane photoFrameRoot;
    @FXML private Text clock;
    @FXML private Group clockGroup;
    @FXML private Text date;
    @FXML private Group currentGroup;
    @FXML private Group forecastGroup;
    @FXML private Text windSpeed;
    @FXML private Text humidity;
    @FXML private Text feelsLike;
    @FXML private Text currentTemp;
    @FXML private ImageView currentIcon;
    @FXML private ImageView day0Icon;
    @FXML private Text day0High;
    @FXML private Text day0Low;
    @FXML private Text day0Label;
    @FXML private Text day1Label;
    @FXML private ImageView day1Icon;
    @FXML private Text day1High;
    @FXML private Text day1Low;
    @FXML private Text day2Label;
    @FXML private ImageView day2Icon;
    @FXML private Text day2High;
    @FXML private Text day2Low;
    @FXML private Text day3Label;
    @FXML private ImageView day3Icon;
    @FXML private Text day3High;
    @FXML private Text day3Low;
    @FXML private Text rainChance;
    @FXML private Text lastUpdated;
    @FXML private Text location;
    
    private PhotoFrameModel model;
    private WeatherModel weatherModel;
    private  List<myImageView> pictures;
    
    public PhotoFrameController()
    {       
        pictures=new ArrayList();
    }
    public PhotoFrameController(PhotoFrameModel model, WeatherModel weatherModel)
    {
        this();
        this.model=model;
        this.weatherModel=weatherModel;
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
                    {
                        pictures=model.getObservablePhotoList();
                    }
            }
        });
        
        //Load the photos for the slideshow
        model.loadImgFiles();
        startSlideShow();
    }
    public void setBindings()
    {
        clock.textProperty().bindBidirectional(model.timeProperty());
        date.textProperty().bindBidirectional(model.dateProperty());
        double position =Setup.screenHeight()-clockGroup.getLayoutBounds().getMaxY();
        clockGroup.setLayoutY(position);
        double positionX=Setup.screenWidth()-forecastGroup.getLayoutBounds().getMaxX();
        forecastGroup.setLayoutX(positionX);
        day0High.textProperty().bindBidirectional(weatherModel.day0High());
        day0Low.textProperty().bindBidirectional(weatherModel.day0Low());
        day0Icon.imageProperty().bindBidirectional(weatherModel.day0Icon());
        day0Label.textProperty().bindBidirectional(weatherModel.day0Label());
        day1Label.textProperty().bindBidirectional(weatherModel.day1Label());
        day1High.textProperty().bindBidirectional(weatherModel.day1High());
        day1Low.textProperty().bindBidirectional(weatherModel.day1Low());
        day1Icon.imageProperty().bindBidirectional(weatherModel.day1Icon());
        day2Label.textProperty().bindBidirectional(weatherModel.day2Label());
        day2High.textProperty().bindBidirectional(weatherModel.day2High());
        day2Low.textProperty().bindBidirectional(weatherModel.day2Low());
        day2Icon.imageProperty().bindBidirectional(weatherModel.day2Icon());
        day3Label.textProperty().bindBidirectional(weatherModel.day3Label());
        day3Low.textProperty().bindBidirectional(weatherModel.day3Low());
        day3High.textProperty().bindBidirectional(weatherModel.day3High());
        day3Icon.imageProperty().bindBidirectional(weatherModel.day3Icon());
        currentIcon.imageProperty().bindBidirectional(weatherModel.currentIcon());
        currentGroup.setLayoutX(0);
        currentGroup.setLayoutY(28);
        windSpeed.textProperty().bindBidirectional(weatherModel.windMph());
        humidity.textProperty().bindBidirectional(weatherModel.relativeHumidity());
        feelsLike.textProperty().bindBidirectional(weatherModel.feelsLike());
        currentTemp.textProperty().bindBidirectional(weatherModel.currentTemp());
        rainChance.textProperty().bindBidirectional(weatherModel.precipChance());
        lastUpdated.textProperty().bindBidirectional(weatherModel.lastUpdated());
        location.textProperty().bindBidirectional(weatherModel.location());
    }
    public SequentialTransition buildSlide(ImageView picture)
    {
         SequentialTransition slide=new SequentialTransition();
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
            slide.getChildren().addAll(fadeIn,showPicture,fadeOut);
            return slide;
         
    }
    public void startSlideShow()
    {
        //Sequential Transition to add the fade in, pause, and fade out transitions for each slide
        SequentialTransition slides;
        //Sequential Transition to play each slide
        SequentialTransition slideshow= new SequentialTransition();
        //if there are no pictures then a default picture will be displayed
        if (pictures.isEmpty())
        {
            ImageView defaultPicture;
            String imgUri=Paths.get(Setup.imageDirectory()+"/default/").toUri().toString();
            defaultPicture=new ImageView(new Image(imgUri+"default.png",Setup.screenWidth(),Setup.screenHeight(),false,false));

            slides=buildSlide(defaultPicture);
            //since all pictures are stacked on the screen at once, 
            //the opacity has to be set to 0
            defaultPicture.setOpacity(0);
            //add the pictures to the photo frame
            photoFrameRoot.getChildren().add(defaultPicture);
            //add the slides to the slideshow
            slideshow.getChildren().add(slides);
        } 
        else
        {
            //shuffle the pictures so the pictures won't always be displayed in the same order
            Collections.shuffle(pictures);
            for (ImageView picture:pictures)
            {
                slides=buildSlide(picture);
                //since all pictures are stacked on the screen at once, 
                //the opacity has to be set to 0
                picture.setOpacity(0);
                //add the pictures to the photo frame
                photoFrameRoot.getChildren().add(picture);
                //add the slides to the slideshow
                slideshow.getChildren().add(slides);
            }
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

