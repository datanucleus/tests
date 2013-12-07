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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Container of Shapes. Has the following :-
 * <ul>
 * <li>a field containing a Shape object (1-1 relation)</li>
 * <li>a field containing a Set of Shapes (1-N relation)</li>
 * </ul>
 */
@Entity
public class ShapeHolder
{
    @Id
    private int id;

    @OneToOne
    protected Shape shape1 = null;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable
    protected Set<Shape> shapeSet1 = new HashSet();

    public ShapeHolder(int id)
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

    public Set getShapeSet1()
    {
        return shapeSet1;
    }
}