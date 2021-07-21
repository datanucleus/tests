/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved. 
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
package org.datanucleus.samples.models.company;

/**
 * Class used as a container of the results of a query returning firstname, lastname, age.
 * Provides a constructor for setting the fields, as well as setters/getters.
 *
 * @version $Revision: 1.1 $  
 **/
public class Identity1
{
    protected String firstName = null;
    protected String lastName = null;
    protected int age;

    public Identity1(String firstName, String lastName, int age)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setFirstName(String name)
    {
        this.firstName = name;
    }

    public void setLastName(String name)
    {
        this.lastName = name;
    }
}