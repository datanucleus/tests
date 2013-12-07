/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
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
package org.datanucleus.tests.directory.embedded;

import java.util.ArrayList;
import java.util.List;

public class Person
{

    private String fullName;

    private String firstName;

    private String lastName;

    private Account account;

    private ContactData contactData;

    private Notebook notebook;

    private List<Computer> computers = new ArrayList<Computer>();

    public Person()
    {
    }

    public Person(String firstName, String lastName, String fullName, Account account, ContactData contactData)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.account = account;
        this.contactData = contactData;
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

    public ContactData getContactData()
    {
        return contactData;
    }

    public void setContactData(ContactData contactData)
    {
        this.contactData = contactData;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public Notebook getNotebook()
    {
        return notebook;
    }

    public void setNotebook(Notebook notebook)
    {
        this.notebook = notebook;
    }

    public List<Computer> getComputers()
    {
        return computers;
    }

    public void setComputers(List<Computer> computers)
    {
        this.computers = computers;
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
        Person other = (Person) obj;
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
