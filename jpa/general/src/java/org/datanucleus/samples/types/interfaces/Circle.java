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
 * Circle class.
 */
@Entity
public class Circle implements Shape, Cloneable, Serializable
{
    @Id
    private int id;    
    protected double radius=0.0;

    public Circle(int id, double radius)
    {
        this.id = id;
        this.radius = radius;
    }

    public String getName()
    {
        return "Circle";
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }
 
    public double getRadius()
    {
        return radius;
    }

    public double getArea()
    {
        return Math.PI*radius*radius;
    }

    public double getCircumference()
    {
        return Math.PI*radius*2;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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
        Circle rhs = (Circle)o;
        return radius == rhs.radius;
    }

    public String toString() 
    {
        return "Circle [radius=" + radius + "]";
    }
}