/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved. 
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

import java.util.Date;

/**
 * Class used as a container of the results of a query returning firstName, lastName, age.
 * Has too many fields and no setters/put so is sample of invalid resultClass.
 *
 * @version $Revision: 1.1 $  
 **/
public class Identity5
{
    protected String firstName = null;
    protected String lastName = null;
    protected int age;
    protected Date date = null;

    public Identity5(String firstName, String lastName, int age, Date date)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.date = date;
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
}