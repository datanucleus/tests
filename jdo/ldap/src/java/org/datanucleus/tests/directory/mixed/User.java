/**********************************************************************
Copyright (c) 2010 Stefan Seelmann and others. All rights reserved.
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
package org.datanucleus.tests.directory.mixed;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "ou=Users,{country}", schema = "top,inetOrgPerson,organizationalPerson,person", detachable = "true")
public class User
{

    @PrimaryKey
    @Persistent(column = "cn")
    private String fullName;

    @Persistent(column = "givenName")
    private String firstName;

    @Persistent(column = "sn")
    private String lastName;

    @Persistent(defaultFetchGroup = "true")
    private Country country;

    public User()
    {
    }

    public User(String firstName, String lastName, String fullName, Country country)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.country = country;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Country getCountry()
    {
        return country;
    }

    public void setCountry(Country country)
    {
        this.country = country;
    }

    public String toString()
    {
        return getFullName();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (fullName == null)
        {
            if (other.fullName != null)
                return false;
        }
        else if (!fullName.equals(other.fullName))
            return false;
        return true;
    }

}
