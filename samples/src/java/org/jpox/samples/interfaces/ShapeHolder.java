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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Container of Shapes. Has the following :-
 * <ul>
 * <li>a field containing a Shape object (1-1 relation)</li>
 * <li>a second field containing a Shape object (1-1 relation)</li>
 * <li>a field containing a List of Shapes (1-N relation)</li>
 * <li>a field containing a Set of Shapes (1-N relation)</li>
 * </ul>
 *
 * @version $Revision: 1.4 $ 
 */
public class ShapeHolder
{
    private int id;
    protected Shape shape1 = null;
    protected Shape shape2 = null;

    protected List shapeList1 = new ArrayList();
    protected Set shapeSet1 = new HashSet();

    public ShapeHolder(int id)
    {
        this.id = id;
        Random r = new Random();
        shape1 = new Circle(r.nextInt(), 5.0);
        shape2 = new Circle(r.nextInt(), 5.0);
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

    public void setShape2(Shape sh)
    {
        shape2 = sh;
    }

    public Shape getShape2()
    {
        return shape2;
    }

    public List getShapeList1()
    {
        return shapeList1;
    }

    public Set getShapeSet1()
    {
        return shapeSet1;
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