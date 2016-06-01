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

Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.annotations.models.company;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Phone number of a person.
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_PHONENUMBER")
public class PhoneNumber
{
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    long id; // PK when using app id

    @Column(name="NAME")
    String name;

    @Column(name="NUMBER")
    String number;

    public PhoneNumber(String name, String number)
    {
        this.name = name;
        this.number = number;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getNumber()
    {
        return number;
    }
}