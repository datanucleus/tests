/**********************************************************************
Copyright (c) 05-May-2004 Andy Jefferson and others.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



Contributors :
    ...
***********************************************************************/
package org.datanucleus.samples.store;

/**
 * A User of a system.
 * Used to demonstrate a 1-1 bidirectional relationship (with UserDetails) using 2 FKs.
 *
 * @version $Revision: 1.1 $
 **/
public class User
{
    /**
     * Login name.
     **/
    protected String login=null;

    /**
     * Password.
     **/
    protected String password=null;

    /**
     * The details associated with this User.
     **/
    protected UserDetails details=null;

    /** Default constructor. */
    protected User()
    {
    }

    /** Constructor.
     * @param login    Login name for user.
     * @param password Password for user.
     **/
    public User(String login,String password)
    {
        this.login    = login;
        this.password = password;
    }

    // ------------------------------- Accessors -------------------------------
    /** Accessor for the login.
     * @return Login
     **/
    public String getLogin()
    {
        return login;
    }

    /** Accessor for the password.
     * @return Password
     **/
    public String getPassword()
    {
        return password;
    }

    /** Accessor for the users details.
     * @return User Details.
     **/
    public UserDetails getUserDetails()
    {
        return details;
    }

    // ------------------------------- Mutators --------------------------------
    /** Mutator for the user details.
     * @param details The user details.
     **/
    public void setDetails(UserDetails details)
    {
        this.details = details;
    }

    /** Utility to return the object as a string.
     * @return  Stringified version of this object. */
    public String   toString()
    {
        StringBuffer str=new StringBuffer("User : " + login);

        return str.toString();
    }
}
