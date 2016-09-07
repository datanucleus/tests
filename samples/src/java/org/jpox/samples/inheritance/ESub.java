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
package org.jpox.samples.inheritance;

import java.util.Collection;
import java.util.HashSet;

/**
 * Subclass for Inheritance tests - sample "E".
 * This sample has a container in the subclass.
 */
public class ESub extends EBase
{
    private double value;
    private Collection<EElemSub> elements = new HashSet<>();
    
    public ESub()
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

    public void addElement(EElemSub elem)
    {
        elements.add(elem);
    }

    public Collection<EElemSub> getElements()
    {
        return elements;
    }
}