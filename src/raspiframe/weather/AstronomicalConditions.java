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

import java.time.LocalTime;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class AstronomicalConditions
{
    private LocalTime sunrise;
    private LocalTime sunset;
    private String moonPhase;
    
    public AstronomicalConditions()
    {
        //iniatilize values to defaults;
        sunrise=LocalTime.of(7, 0);
        sunset=LocalTime.of(7,0);
        moonPhase="Unknown";
    }
    public LocalTime getSunrise()
    {
        return sunrise;
    }
    void setSunrise(LocalTime sunrise)
    {
        this.sunrise=sunrise;
    }
    public LocalTime getSunset()
    {
        return sunset;
    }
    void setSunset(LocalTime sunset)
    {
        this.sunset=sunset;
    }
    public String getMoonPhase()
    {
        return this.moonPhase;
    }
    void setMoonPhase(String moonPhase)
    {
        this.moonPhase=moonPhase;
    }
}
