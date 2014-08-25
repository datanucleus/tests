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

import java.util.Random;

/**
 * Container of Shapes. Has a Shape field.
 * Can be used as a test of missing/incomplete ORM info via and ORM file.
 */
public class ShapeHolder4
{
    private int id;
    protected Shape shape1 = null;

    public ShapeHolder4(int id)
    {
        this.id = id;
        Random r = new Random();
        shape1 = new Circle(r.nextInt(), 5.0);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setShape1(Shape sh)
    {
        shape1 = sh;
    }

    public Shape getShape1()
    {
        return shape1;
    }
}