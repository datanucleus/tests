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
package org.datanucleus.samples.interfaces;

import java.io.Serializable;

/**
 * Rectangle class.
 *
 * @version $Revision: 1.2 $
 */
public class Rectangle implements Shape, Cloneable, Serializable
{
	private static final long serialVersionUID = 5488145913366327153L;
    private int id;    
    protected double width=0.0;
    protected double length=0.0;

    public Rectangle(int id, double width,double length)
    {
        this.id = id;
        this.length = length;
        this.width = width;
    }

    public String getName()
    {
        return "Rectangle";
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
        return length*width;
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

    /**
     * @return Returns the length.
     */
    public double getLength()
    {
        return length;
    }

    /**
     * @return Returns the width.
     */
    public double getWidth()
    {
        return width;
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
        Rectangle rhs = (Rectangle)o;
        return width == rhs.width && length == rhs.length;
    }

    public String toString()
    {
        return "Rectangle [width=" + width + ", length=" + length + "]";
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = 7157350923150502225L;
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