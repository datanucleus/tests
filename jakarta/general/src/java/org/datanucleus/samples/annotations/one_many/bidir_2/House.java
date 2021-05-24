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
package org.datanucleus.samples.annotations.one_many.bidir_2;

import java.util.Collection;
import java.util.HashSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

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