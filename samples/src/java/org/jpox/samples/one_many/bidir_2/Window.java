/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
***********************************************************************/
package org.jpox.samples.one_many.bidir_2;

/**
 * Representation of a Window.
 */
public class Window
{
    private long width;
    private long height;
    private House house;
    private String description;

    public Window(long width, long height, House house)
    {
        this.width = width;
        this.height = height;
        this.house = house;
    }
    
    public Window(String description)
    {
        this.description = description;
    }
    

    /**
     * Accessor for the height of the window
     * @return the height
     */
    public long getHeight()
    {
        return height;
    }

    /**
     * Mutator for the height of the window
     * @param height
     */
    public void setHeight(long height)
    {
        this.height = height;
    }

    /**
     * Accessor for the house owning this window
     * @return The house
     */
    public House getHouse()
    {
        return house;
    }

    /**
     * Mutator for the owning house
     * @param house The house
     */
    public void setHouse(House house)
    {
        this.house = house;
    }

    /**
     * Accessor for the width of the window
     * @return The width
     */
    public long getWidth()
    {
        return width;
    }

    /**
     * Mutator for the width of the window
     * @param width The width
     */
    public void setWidth(long width)
    {
        this.width = width;
    }

    public String toString()
    {
        return "Window [" + (description != null ? "description=" + description + ", " : "")
        + (width!=0 ? "width=" + width + ", " : "")  
        + (height!=0 ? "height=" + height  + ", ": "") 
        + (house != null ? "house=" + house.getNumber() + " " + house.getStreet() + ", " : "") +  "]";
    }
 
}