/**********************************************************************
 Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.types.enums;

/**
 * Palette of colours.
 */
public class AlternativePalette
{
	String id;
    int amount;

    AlternativeColour colour;

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public AlternativeColour getColour()
    {
        return colour;
    }

    public void setColour(AlternativeColour colour)
    {
        this.colour = colour;
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