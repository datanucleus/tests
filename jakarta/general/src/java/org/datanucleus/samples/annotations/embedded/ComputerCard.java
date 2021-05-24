/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved. 
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
package org.datanucleus.samples.annotations.embedded;

import jakarta.persistence.Embeddable;

/**
 * Representation of a Card (PCI, ISA, etc) in a Computer.
 */
@Embeddable
public class ComputerCard
{
    public static final int ISA_CARD = 0;
    public static final int PCI_CARD = 1;
    public static final int AGP_CARD = 2;

    private int type;

    private String makerName;

    private Computer computer;

    public ComputerCard(String makerName, int type)
    {
        this.makerName = makerName;
        this.type = type;
    }

    public Computer getComputer()
    {
        return computer;
    }

    public String getMakerName()
    {
        return makerName;
    }

    public int getType()
    {
        return type;
    }

    public void setComputer(Computer computer)
    {
        this.computer = computer;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setMakerName(String maker)
    {
        this.makerName = maker;
    }

    public String toString()
    {
        String str = "ComputerCard of type ";
        if (type == ISA_CARD)
        {
            return str += "ISA";
        }
        else if (type == PCI_CARD)
        {
            return str += "PCI";
        }
        else if (type == AGP_CARD)
        {
            return str += "AGP";
        }
        else
        {
            return str += "Unknown";
        }
    }
}