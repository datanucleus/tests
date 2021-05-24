/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.embedded.collection;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

/**
 * Owner class with collection fields that are embedded.
 */
@Entity
@Table(name="JPA_COLL_EMBEDDED_OWNER")
public class EmbeddedCollectionOwner
{
    @Id
    long id;

    @ElementCollection
    @CollectionTable(name="JPA_COLL_EMB", joinColumns={@JoinColumn(name="JPA_COLL_EMB_OWNER_ID")})
    Set<EmbeddedCollElement> collEmbedded = new HashSet<>();

    @ElementCollection
    @CollectionTable(name="JPA_COLL_EMB_OVERRIDE", joinColumns={@JoinColumn(name="JPA_COLL_EMB_OWNER_ID")})
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="COLL_ELEM_NAME")), 
        @AttributeOverride(name="value", column=@Column(name="COLL_ELEM_VALUE"))})
    Set<EmbeddedCollElement> collEmbeddedOverride = new HashSet<>();

    public EmbeddedCollectionOwner(long id)
    {
        this.id = id;
    }

}