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
package org.datanucleus.samples.annotations.embedded;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Owner of an embedded object.
 */
@Entity
public class EmbeddedOwner2
{
    @Id
    long id;

    String name;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="EMB_OWNER_DETAIL_NAME"))})
    EmbeddedObject2 embeddedObject;

    public EmbeddedOwner2(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }

    public void setEmbeddedObject(EmbeddedObject2 obj)
    {
        this.embeddedObject = obj;
    }
    public EmbeddedObject2 getEmbeddedObject()
    {
        return embeddedObject;
    }
}