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
package raspiframe.weather;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import java.io.IOException;
import org.apache.http.ParseException;
import javax.net.ssl.HttpsURLConnection;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * Setups the connection to a web server in order to retrieve the weather data.  
 * The constructor sets the host and the URI with the get request.  The getEntity then
 * makes the call to the host and retrieves the httpEntity.  
 */

public class httpConnection
{
    private String host;
    private String getURI;
    //Constructor sets the host, and the getURI
    public httpConnection(String host,String getURI)
    {

        this.host=host;
        this.getURI=getURI;
                
    }
    
    //makes the call to the web host and retrieves the httpEntity
    public String getEntity()
    {
        String entityToString=new String();
        try
        {
            HttpHost target = new HttpHost(host);
            HttpGet getRequest = new HttpGet(getURI);
             //uses the try with resources to automatically close the httpclient after use
             //the close happens irregardless if the try block executes successfully
            try(CloseableHttpClient httpClient = HttpClientBuilder.create().build())
            {
                CloseableHttpResponse httpResponse=httpClient.execute(target,getRequest);
                HttpEntity entity = httpResponse.getEntity();
                entityToString =EntityUtils.toString(entity);
            }
            catch (IOException e)
            {
                System.err.println(e);
            }
        }
        catch (ParseException e)
        {
          System.err.println(e);  
        }
        return entityToString;
    }
}
