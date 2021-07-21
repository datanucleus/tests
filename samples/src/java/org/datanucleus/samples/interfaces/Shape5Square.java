/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others.
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

/**
 * Square class.
 **/
public class Shape5Square implements Shape5
{
    private int id;
    protected double width = 0.0;
    protected double length = 0.0;

    protected Shape5Holder shapeHolder = null;
    
    public Shape5Square()
    {
    }

    public Shape5Square(int id,double width,double length)
    {
        this.id = id;       
        this.length = length;
        this.width = width;
    }

    public String getName()
    {
        return "Square";
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

    public int getId()
    {
        return id;
    }

    public void setId(int i)
    {
        id = i;
    }

    public Shape5Holder getShapeHolder()
    {
        return shapeHolder;
    }    

    public boolean equals(Object o)
    {
        if (o == null || !o.getClass().equals(this.getClass()))
        {
            return false;
        }
        Shape5Square sqs = (Shape5Square)o;
        return width == sqs.width && length == sqs.length;
    }

    public String toString()
    {
        return "{Square width=" + width + "; length=" + length + "}";
    }
}