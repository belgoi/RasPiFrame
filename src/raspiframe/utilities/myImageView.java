package raspiframe.utilities;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 *  Purpose:  Extends class ImageView to add a getImageUrl method that  
 *  is necessary for removing an imageview from the observableList
 */
public class myImageView extends ImageView
{
    private String url;
    public myImageView(String url,Image image)
    {
            super(image);
            this.url=url;
    }
    public String getImageUrl()
    {
        return url;
    }
}
