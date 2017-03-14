/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.one_many.collection;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Holder of a Collection field.
 */
@Entity
@Table(name="JPA_AN_COLLHOLDER1")
public class CollectionHolder1
{
    @Id
    @Column(name="JPA_AN_COLLHOLDER1_ID")
    long id;

    // ElementCollection with defined namings
    @ElementCollection
    @CollectionTable(name="JPA_AN_COLLHOLDER1_STRINGS", joinColumns=@JoinColumn(name="COLLHOLDER1_ID"))
    @Column(name="PROP_VALUE")
    Collection<String> collBasic1 = new HashSet<String>();

    // ElementCollection with default namings
    @ElementCollection
    @CollectionTable
    Collection<String> collBasic2 = new HashSet<String>();

    // Example of Collection<Entity> with default namings
    @OneToMany
    @JoinTable
    Collection<CollectionHolder1Element> coll3;

    // Example of Collection<Entity> with specified namings
    @OneToMany
    @JoinTable(name="JPA_AN_COLLHOLDER1_COLL4", joinColumns=@JoinColumn(name="COLL4_ELEMENT"), inverseJoinColumns=@JoinColumn(name="COLL4_OWNER_ID"))
    Collection<CollectionHolder1Element> coll4;

    public CollectionHolder1(long id)
    {
        this.id = id;
    }
}