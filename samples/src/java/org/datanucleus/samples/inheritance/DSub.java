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

import java.util.Collection;
import java.util.HashSet;

/**
 * Subclass for Inheritance tests - sample "D".
 * This sample has a container in the subclass.
 */
public class DSub extends DBase
{
    private double value;
    private Collection<DElemSub> elements = new HashSet<>();
    
    public DSub()
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

    public void addElement(DElemSub elem)
    {
        elements.add(elem);
    }

    public Collection<DElemSub> getElements()
    {
        return elements;
    }
}