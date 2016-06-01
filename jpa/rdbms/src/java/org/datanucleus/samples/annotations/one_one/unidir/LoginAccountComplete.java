package org.datanucleus.samples.annotations.one_one.unidir;

public class LoginAccountComplete
{
    String firstName;
    String lastName;
    String userName;
    String password;

    public LoginAccountComplete(String firstName, String lastName, String userName, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public String getUserName()
    {
        return userName;
    }
    public String getPassword()
    {
        return password;
    }
}