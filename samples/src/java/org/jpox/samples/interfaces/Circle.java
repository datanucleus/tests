/**********************************************************************
Copyright (c) 2003 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.interfaces;

import java.io.Serializable;

/**
 * Circle class.
 * @version $Revision: 1.2 $
 */
public class Circle implements Shape, Cloneable, Serializable
{
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

    /**
     * @return Returns the id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
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

    public static class Oid implements Serializable
    {
        public int id;

        public Oid()
        {
        }

        public Oid(String s)
        {
            this.id = Integer.valueOf(s).intValue();
        }

        public int hashCode()
        {
            return id;
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.id == this.id;
            }
            return false;
        }

        public String toString()
        {
            return String.valueOf(id);
        }
    }
}