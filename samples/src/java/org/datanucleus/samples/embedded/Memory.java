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
 

Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.embedded;

/**
 * Representation of Memory for a Computer
 * @version $Revision: 1.1 $
 */
public class Memory
{
    public static final int COMPACT_FLASH = 0;
    public static final int SMART_MEDIA = 1;
    public static final int SONY_MEMORYSTICK = 2;

    private int type;

    private long size;

    private double voltage;

    private Chip chip;

    public Memory(int type,
                  long size,
                  double voltage)
    {
        this.type = type;
        this.size = size;
        this.voltage = voltage;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public double getVoltage()
    {
        return voltage;
    }

    public void setVoltage(double voltage)
    {
        this.voltage = voltage;
    }

    public Chip getChip()
    {
        return chip;
    }

    public void setChip(Chip chip)
    {
        this.chip = chip;
    }

    public String toString()
    {
        String str = "" + size + "Mb Memory of type ";
        if (type == COMPACT_FLASH)
        {
            str += "CompactFlash";
        }
        else if (type == SMART_MEDIA)
        {
            str += "SmartMedia";
        }
        else if (type == SONY_MEMORYSTICK)
        {
            str += "MemoryStick";
        }
        else
        {
            str += "Unknown";
        }
        return str += ", " + voltage + "V";
    }
}