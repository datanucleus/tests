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
 **/
public class UserDetails
{
    protected String forename=null;

    protected String surname=null;

    protected User user=null;

    protected UserDetails()
    {
    }

    public UserDetails(String forename,String surname)
    {
        this.forename = forename;
        this.surname  = surname;
    }

    public String getForename()
    {
        return forename;
    }

    public String getSurname()
    {
        return surname;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String   toString()
    {
        StringBuffer str=new StringBuffer("UserDetails : " + forename + " " + surname);

        return str.toString();
    }
}
