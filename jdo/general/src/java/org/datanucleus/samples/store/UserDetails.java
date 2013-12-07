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
 * The details for a User of a system.
 * Used to demonstrate a 1-1 bidirectional relationship (with User) using 2 FKs.
 *
 * @version $Revision: 1.1 $
 **/
public class UserDetails
{
    /**
     * Forename.
     **/
    protected String forename=null;

    /**
     * Surname
     **/
    protected String surname=null;

    /**
     * The User associated with these details.
     **/
    protected User user=null;

    /** Default constructor. */
    protected UserDetails()
    {
    }

    /** Constructor.
     * @param forename forename of the user
     * @param surname  surname of the user
     **/
    public UserDetails(String forename,String surname)
    {
        this.forename = forename;
        this.surname  = surname;
    }

    // ------------------------------- Accessors -------------------------------
    /** Accessor for the forename
     * @return forename
     **/
    public String getForename()
    {
        return forename;
    }

    /** Accessor for the surname
     * @return surname
     **/
    public String getSurname()
    {
        return surname;
    }

    /** Accessor for the user.
     * @return User.
     **/
    public User getUser()
    {
        return user;
    }

    // ------------------------------- Mutators --------------------------------
    /** Mutator for the user
     * @param user The user
     **/
    public void setUser(User user)
    {
        this.user = user;
    }

    /** Utility to return the object as a string.
     * @return  Stringified version of this object. */
    public String   toString()
    {
        StringBuffer str=new StringBuffer("UserDetails : " + forename + " " + surname);

        return str.toString();
    }
}
