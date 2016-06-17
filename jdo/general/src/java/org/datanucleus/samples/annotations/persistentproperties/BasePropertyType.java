/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.persistentproperties;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Base class using properties access.
 */
@PersistenceCapable(detachable="true")
@Discriminator
public class BasePropertyType
{
    protected long id;
    protected String name;
    
    protected RelatedPropertyType related;

    public BasePropertyType(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @PrimaryKey
    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @Persistent
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    @Persistent
    public RelatedPropertyType getRelated()
    {
        return related;
    }
    public void setRelated(RelatedPropertyType rel)
    {
        this.related = rel;
    }
}
