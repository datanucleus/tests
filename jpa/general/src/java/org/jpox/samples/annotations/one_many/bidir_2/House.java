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
package org.jpox.samples.annotations.one_many.bidir_2;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Representation of a House, using JPA annotations.
 * Has 1-N join table set.
 *
 * @version $revision$
 */
@Entity
@Table(name="JPA_AN_HOUSE")
@TableGenerator(name="HouseGenerator")
public class House
{
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="HouseGenerator")
    long id;

    private int number;
    private String street;

    @OneToMany(mappedBy="house", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="JPA_AN_HOUSE_WINDOWS")
    private HashSet<Window> windows = new HashSet<Window>();

    public House(int number, String street)
    {
        this.number = number;
        this.street = street;
    }

    public final Collection<Window> getWindows()
    {
        return windows;
    }

    /**
     * Accessor for the street
     * @return THe street
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * Mutator for the street
     * @param street The street
     */
    public void setStreet(String street)
    {
        this.street = street;
    }

    /**
     * Accessor for the number of the house
     * @return The number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Mutator for the number of the house
     * @param number The number
     */
    public void setNumber(int number)
    {
        this.number = number;
    }
}