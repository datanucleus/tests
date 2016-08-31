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
package org.datanucleus.samples.annotations.embedded.map;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

/**
 * Owner class with fields that are embedded.
 */
@Entity
@Table(name="JPA_EMBEDDED_OWNER")
public class EmbeddedOwner
{
    @Id
    long id;

    @ElementCollection
    @CollectionTable(name="JPA_MAP_EMB_VALUE")
    Map<String, EmbeddedMapValue> mapEmbeddedValue = new HashMap<>();

    @ElementCollection
    @CollectionTable(name="JPA_MAP_EMB_VALUE_OVERRIDE")
    @MapKeyColumn(name="MAP_KEY")
    @AttributeOverrides({
        @AttributeOverride(name="value.name", column=@Column(name="MAP_VALUE_NAME")), 
        @AttributeOverride(name="value.value", column=@Column(name="MAP_VALUE_VALUE"))})
    Map<String, EmbeddedMapValue> mapEmbeddedValueOverride = new HashMap<>();

    @ElementCollection
    @CollectionTable(name="JPA_MAP_EMB_KEY")
    Map<EmbeddedMapKey, String> mapEmbeddedKey = new HashMap<>();

    @ElementCollection
    @CollectionTable(name="JPA_MAP_EMB_KEY_OVERRIDE")
    @AttributeOverrides({
        @AttributeOverride(name="key.name", column=@Column(name="MAP_KEY_NAME")), 
        @AttributeOverride(name="key.value", column=@Column(name="MAP_KEY_VALUE"))})
    Map<EmbeddedMapKey, String> mapEmbeddedKeyOverride = new HashMap<>();

    public EmbeddedOwner(long id)
    {
        this.id = id;
    }

}