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
package org.jpox.samples.annotations.one_many.unidir_2;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Details of a member in a group.
 */
@Entity
public class MemberDetails
{
    @Id
    private long id;

    private String firstName;

    private String surname;

    public MemberDetails(long id, String firstName, String surname)
    {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
    }

    public long getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String name)
    {
        this.firstName = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String name)
    {
        this.surname = name;
    }
}