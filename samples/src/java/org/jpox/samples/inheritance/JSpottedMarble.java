/**********************************************************************
 Copyright (c) 2005 Erik Bengtson and others.
 Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
  

 Contributors:
 ...
 **********************************************************************/
package org.jpox.samples.inheritance;

/**
 * Sub-Sub class for inheritance tests - sample "J".
 * This sample has 2 subclasses - one a Container, and one an Element. The Element has 2 subclasses.
 * @version $Revision: 1.1 $
 */
public class JSpottedMarble extends JMarble
{
    private String spotColor;

    public JSpottedMarble()
    {
    }

    public JSpottedMarble(final String color, final String spotColor)
    {
        super(color);

        this.spotColor = spotColor;
    }

    public JSpottedMarble(int id,final String color, final String spotColor)
    {
        super(id,color);

        this.spotColor = spotColor;
    }    
    
    public String getSpotColor()
    {
        return spotColor;
    }

    public void setSpotColor(final String spotColor)
    {
        this.spotColor = spotColor;
    }

    public String toString()
    {
        return "A " + getColor() + " marble with " + getSpotColor() + " spots.";
    }
}