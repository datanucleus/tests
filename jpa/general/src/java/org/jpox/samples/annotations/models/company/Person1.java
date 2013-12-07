package org.jpox.samples.annotations.models.company;

/**
 * Sample result class with a constructor for creation/population.
 */
public class Person1
{
    String firstName;
    String lastName;

    public Person1(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
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