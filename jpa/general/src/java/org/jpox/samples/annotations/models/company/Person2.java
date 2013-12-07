package org.jpox.samples.annotations.models.company;

/**
 * Sample result class with a constructor for creation/population.
 */
public class Person2
{
    String firstName;
    String lastName;

    public Person2()
    {
    }

    public void setFirstName(String name)
    {
        this.firstName = name;
    }
    public void setLastName(String name)
    {
        this.lastName = name;
    }
    public String getFirstName()
    {
        return firstName;
    }
    public String getLastName()
    {
        return lastName;
    }
}