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
package org.jpox.samples.interfaces;

/**
 * Circle class.
 **/
public class Circle3b implements ShapeInverseB
{
    private int id;
    protected double radius = 0.0;
    protected ShapeHolder3b shapeHolder = null;
    protected int listOrder;
    
    public Circle3b()
    {
    }

    public Circle3b(int id, double radius)
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

    public ShapeHolder3b getShapeHolder()
    {
        return shapeHolder;
    }    

    public boolean equals(Object o)
    {
        if (o == null || !o.getClass().equals(this.getClass()))
        {
            return false;
        }
        Circle3b rhs = (Circle3b)o;
        return radius == rhs.radius;
    }

    public String toString() 
    {
        return "{Circle radius=" + radius + "}";
    }
}