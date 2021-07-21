/**********************************************************************
 Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.types.enums;

import java.util.HashSet;

/**
 * Palette of colours.
 */
public class Palette
{
	String id;
    int amount;

    HashSet<Colour> colours = new HashSet<Colour>();
    HashSet<Colour> coloursAsInts = new HashSet<Colour>();

    Colour colour;
    Colour colourOrdinal;
    Colour colourSerialized;

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public Colour getColour()
    {
        return colour;
    }

    public void setColour(Colour colour)
    {
        this.colour = colour;
    }

    public Colour getColourOrdinal()
    {
        return colourOrdinal;
    }

    public void setColourOrdinal(Colour colourOrdinal)
    {
        this.colourOrdinal = colourOrdinal;
    }

    public Colour getColourSerialized()
    {
        return colourSerialized;
    }

    public void setColourSerialized(Colour colourSerialized)
    {
        this.colourSerialized = colourSerialized;
    }

    public void addColour(Colour colour)
    {
        colours.add(colour);
    }

    public void addColourAsInt(Colour colour)
    {
        coloursAsInts.add(colour);
    }

    public HashSet<Colour> getColours()
    {
        return colours;
    }

    public void setId(String id)
    {
		this.id = id;
	}
    
    public String getId()
    {
        return id;
    }
}