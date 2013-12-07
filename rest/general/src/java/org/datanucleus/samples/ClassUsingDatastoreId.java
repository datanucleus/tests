package org.datanucleus.samples;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class ClassUsingDatastoreId
{
    String name;

    public ClassUsingDatastoreId(String name)
    {
        this.name = name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
