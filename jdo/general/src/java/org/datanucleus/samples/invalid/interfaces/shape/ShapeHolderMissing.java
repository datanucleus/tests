/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.invalid.interfaces.shape;

import java.io.Serializable;
import java.util.Random;

import org.jpox.samples.interfaces.Circle;
import org.jpox.samples.interfaces.Shape;

/**
 * Container of Interface field, with its metadata declaration for the interface
 * field specifying an inconsistent number of columns.
 * Used as an example of incomplete MetaData specification.
 *
 * @version $Revision: 1.2 $ 
 */
public class ShapeHolderMissing
{
    protected Shape shape=null;
    private int id;

    public ShapeHolderMissing()
    {
    }

    public ShapeHolderMissing(int id)
    {
        this.id = id;
        Random r = new Random();
        shape = new Circle(r.nextInt(), 5.0);
    }

    public void setShape(Shape aShape)
    {
        shape = aShape;
    }

    public Shape getShape()
    {
        return shape;
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