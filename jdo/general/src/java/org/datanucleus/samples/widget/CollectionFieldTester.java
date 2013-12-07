/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionFieldTester
{
    private Set personSet;
    private Collection personCollection;
    private Collection primitiveCollection;
    private Collection inversePrimitiveCollection;
    private Collection objectCollection;
    //Not yet supported
    //private Collection interfaceCollection;

    public CollectionFieldTester()
    {
        personSet = new HashSet();
        personCollection = new HashSet();
        primitiveCollection = new HashSet();
        inversePrimitiveCollection = new HashSet();
        objectCollection = new HashSet();
        //interfaceCollection = new HashSet();
    }

    public Set getPersonSet()
    {
        return this.personSet;
    }

    public void setPersonSet(Set s)
    {
        this.personSet = s;
    }

    public Collection getPersonCollection()
    {
        return this.personCollection;
    }

    public void setPersonCollection(Collection c)
    {
        this.personCollection = c;
    }


    public Collection getPrimitiveCollection()
    {
        return this.primitiveCollection;
    }

    public void setPrimitiveCollection(Collection c)
    {
        this.primitiveCollection = c;
    }

    public Collection getInversePrimitiveCollection()
    {
        return this.inversePrimitiveCollection;
    }

    public void setInversePrimitiveCollection(Collection c)
    {
        this.inversePrimitiveCollection = c;
    }

    public Collection getObjectCollection()
    {
        return this.objectCollection;
    }

    public void setObjectCollection(Collection c)
    {
        this.objectCollection = c;
    }

    /*public Collection getInterfaceCollection()
    {
        return this.interfaceCollection;
    }

    public void setInterfaceCollection(Collection c)
    {
        this.interfaceCollection = c;
    }*/
}
