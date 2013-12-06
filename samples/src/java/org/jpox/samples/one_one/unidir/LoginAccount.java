/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others.
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
package org.jpox.samples.one_one.unidir;

/**
 * An account associated with a login.
 * @version $Revision: 1.3 $
 */
public class LoginAccount
{
    protected long id;
    protected String firstName = null;
    protected String lastName = null;
    protected Login login = null;

    public LoginAccount(String firstName, String lastName, String login, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = new Login(login,password);
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public Login getLogin()
    {
        return login;
    }

    public void setLogin(Login login)
    {
        this.login = login;
    }

    public void setFirstName(String name)
    {
        this.firstName = name;
    }

    public void setLastName(String name)
    {
        this.lastName = name;
    }

    /**
     * Utility to return the object as a string.
     * @return String form of this account
     */
    public String toString()
    {
        return firstName + " " + lastName + " [login " + login.getUserName() + "]";
    }
}