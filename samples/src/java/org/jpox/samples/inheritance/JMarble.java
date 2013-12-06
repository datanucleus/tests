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
 * Sub class for inheritance tests - sample "J".
 * This sample has 2 subclasses - one a Container, and one an Element. The Element has 2 subclasses.
 * @version $Revision: 1.1 $
 */
public class JMarble extends JBase
{
    private String color;

    private JBowl bowl;

    public JMarble()
    {
    }

    public JMarble(final String color)
    {
        this.color = color;
    }

    public JMarble(int id,final String color)
    {
        setId(new Integer(id));
        this.color = color;
    }
    
    public String getColor()
    {
        return this.color;
    }

    public void setColor(final String color)
    {
        this.color = color;
    }

    public JBowl getBowl()
    {
        return bowl;
    }

    public String toString()
    {
        return "A " + getColor() + " marble";
    }
}