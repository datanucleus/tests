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
 * Triangle class.
 * 
 * @version $Revision: 1.2 $
 */
public class Triangle implements Shape, Cloneable, Serializable
{
    private static final long serialVersionUID = 5912342834818282162L;
    private int id;
    private String composed;
    protected double width=0.0;
    protected double length=0.0;

    public Triangle(int id,String composed,double width,double length)
    {
        this.id = id;
        this.composed = composed;
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

    public String getComposed()
    {
        return composed;
    }

    public void setComposed(String string)
    {
        composed = string;
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
        return id == trs.id && composed.equals(trs.composed) && width == trs.width && length == trs.length;
    }

    public String toString()
    {
        return "Triangle [width=" + width + ", length=" + length + "]";
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -2226836635817709878L;
        public int id;
        public String composed;
        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer toke = new java.util.StringTokenizer (s, "::");
            //ignore first token
            s = toke.nextToken ();
            s = toke.nextToken ();
            this.id = Integer.valueOf(s).intValue();
            s = toke.nextToken ();
            this.composed = s;
        }

        public int hashCode()
        {
            return id ^ composed.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.id == this.id && k.composed.equals(this.composed);
            }
            return false;
        }

        public String toString()
        {
            return this.getClass().getName() + "::"  + id + "::" + composed;
        }
    }
}