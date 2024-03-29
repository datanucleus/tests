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
package org.datanucleus.samples.inheritance;

/**
 * Subclass for inheritance tests - sample "D".
 * This sample has a 1-N bidir relation between the subclasses.
 * @version $Revision: 1.1 $
 */
public class DElemSub extends DElemBase
{
    private double value;
    private String description;
    private DSub container;

    public DElemSub()
    {
        super();
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public String getDescription()
    {
        return description;
    }

    public DSub getContainer()
    {
        return container;
    }

    public void setContainer(DSub container)
    {
        this.container = container;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}