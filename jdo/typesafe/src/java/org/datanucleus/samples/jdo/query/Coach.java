/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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
***********************************************************************/
package org.datanucleus.samples.jdo.query;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
public class Coach
{
    @PrimaryKey
    long id;

    String firstName;

    String lastName;

    int yearsExperience;

    double salary;

    public Coach(long id, String first, String last, int yrs)
    {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
        this.yearsExperience = yrs;
    }

    public double getSalary()
    {
        return salary;
    }
    public void setSalary(double sal)
    {
        this.salary = sal;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public int getYearsExperience()
    {
        return yearsExperience;
    }
}
