package org.datanucleus.samples.annotations;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Person
{
    @Id
    long id;

    String name;

    public Person(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}