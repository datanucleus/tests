/**********************************************************************
Copyright (c) 2005 Boris Boehlen and others. All rights reserved.
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
package org.jpox.samples.models.graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class JdoGraphEntity
{
    private Map attributes;
    private JdoGraphEntityClass entityType;
    private JdoGraph parent;

    public JdoGraphEntity(JdoGraphEntityClass type, JdoGraph parent)
    {
        this.entityType = type;
        this.parent = parent;
        this.attributes = new HashMap();
    }

    public Serializable getAttributeValue(JdoAttribute attribute)
    {
        JdoAttributeHolder holder = (JdoAttributeHolder) attributes.get(attribute);
        return holder == null ? null : holder.getValue();
    }

    public boolean isAttributeValid(JdoAttribute attribute)
    {
        return attributes.containsKey(attribute);
    }

    public void putAttributeValue(JdoAttribute attribute, Serializable value)
    {
        attributes.put(attribute, new JdoAttributeHolder(value));
    }

    public void setAttributeInvalid(JdoAttribute attribute)
    {
        attributes.remove(attribute);
    }

    public Map getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map attributes)
    {
        this.attributes = attributes;
    }

    public JdoGraphEntityClass getEntityType()
    {
        return entityType;
    }

    public void setEntityType(JdoGraphEntityClass entityType)
    {
        this.entityType = entityType;
    }

    public JdoGraph getParent()
    {
        return parent;
    }

    public void setParent(JdoGraph parent)
    {
        this.parent = parent;
    }
}