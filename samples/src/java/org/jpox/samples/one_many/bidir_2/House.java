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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a House.
 */
public class House
{
    private int number;
    private String street;

    private Set<Window> windows = new HashSet();

    public House(int number, String street)
    {
        this.number = number;
        this.street = street;
    }

    public String toString()
    {
        // generated in Eclipse using Source->"Generate toString()...", "skip null values"
        return "House [number=" + number + ", " + (street != null ? "street=" + street + ", " : "") + (windows != null ? "windows=" + windows : "") + "]";
    }

    public final Collection getWindows()
    {
        return windows;
    }

    public int getNumberOfWindows()
    {
        return windows.size();
    }

    /**
     * Method to add a window
     * @param w The window to add
     */
    public void addWindow(Window w)
    {
        windows.add(w);
    }

    /**
     * Method to remove a window
     * @param w The window to remove
     */
    public void removeWindow(Window w)
    {
        windows.remove(w);
    }

    /**
     * Method to set new windows
     * @param windows The windows to replace the former collection
     */
    public void setWindows(Collection windows)
    {
        this.windows = new HashSet(windows);
    }

    /**
     * Accessor for the street
     * @return THe street
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * Mutator for the street
     * @param street The street
     */
    public void setStreet(String street)
    {
        this.street = street;
    }

    /**
     * Accessor for the number of the house
     * @return The number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Mutator for the number of the house
     * @param number The number
     */
    public void setNumber(int number)
    {
        this.number = number;
    }
}