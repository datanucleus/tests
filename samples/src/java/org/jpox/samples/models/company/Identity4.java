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
package org.jpox.samples.models.company;

/**
 * Class used as a container of the results of a query returning firstName, lastName, age.
 * Provides public put method for the fields.
 *
 * @version $Revision: 1.1 $  
 **/
public class Identity4
{
    protected String firstName;
    protected String lastName;
    protected int age;

    public Identity4()
    {
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

    public void put(Object field, Object value)
    {
        String fieldName = (String)field;
        if (fieldName.equals("age"))
        {
            age = ((Integer)value).intValue();
        }
        else if (fieldName.equals("firstName"))
        {
            firstName = (String)value;
        }
        else if (fieldName.equals("lastName"))
        {
            lastName = (String)value;
        }
    }
}