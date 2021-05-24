package org.datanucleus.samples.annotations;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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