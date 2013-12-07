/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.interfaces;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Triangle class.
 */
@Entity
public class Triangle implements Shape, Cloneable, Serializable
{
    @Id
    private int id;
    protected double width=0.0;
    protected double length=0.0;

    public Triangle(int id, double width, double length)
    {
        this.id = id;
        this.length = length;
        this.width = width;
    }

    public String getName()
    {
        return "Triangle";
    }

    public void setLength(double length)
    {
        this.length = length;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public double getArea()
    {
        return (length*width)/2;
    }
    
    public int getId()
    {
        return id;
    }

    public void setId(int i)
    {
        id = i;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
            return null;
        }
    }

    public boolean equals(Object o)
    {
        if (o == null || !o.getClass().equals(this.getClass()))
        {
            return false;
        }
        Triangle trs = (Triangle)o;
        return id == trs.id && width == trs.width && length == trs.length;
    }

    public String toString()
    {
        return "Triangle [width=" + width + ", length=" + length + "]";
    }
}