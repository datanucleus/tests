/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.jpa.query;

import javax.persistence.Entity;

/**
 * Superclass of Coach and Player, set as abstract so we can test use of abstract entities.
 */
@Entity
public abstract class Person extends AbstractPerson
{
    String firstName;

    String lastName;

    public Person(long id, String first, String last)
    {
        super(id);
        this.firstName = first;
        this.lastName = last;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
}