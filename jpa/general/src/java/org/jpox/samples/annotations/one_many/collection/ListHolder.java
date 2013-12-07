/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.one_many.collection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

/**
 * Sample class with different types of List fields.
 */
@Entity
public class ListHolder
{
    @Id
    private long id;

    @ElementCollection(targetClass=String.class)
    @CollectionTable(name="JPA_ANN_LISTHOLDER_NONPC1")
    private List listNonPC1; // String elements, with join table

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true)
    @OrderColumn(name="temp")
    private List<PCFKListElement> joinListPC; // Using join table, with PC elements

    public ListHolder()
    {
    }

    public ListHolder(int id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public List getListNonPC1()
    {
        if (listNonPC1 == null)
        {
            listNonPC1 = new ArrayList();
        }
        return listNonPC1;
    }

    public List getJoinListPC()
    {
        if (joinListPC == null)
        {
            joinListPC = new ArrayList<PCFKListElement>();
        }
        return joinListPC;
    }
}