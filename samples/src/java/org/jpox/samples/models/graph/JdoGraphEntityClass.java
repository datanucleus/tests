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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JdoGraphEntityClass
{
    private Map declaredAttributes;
    private String name;

    public JdoGraphEntityClass(String name)
    {
        this.name = name;
        this.declaredAttributes = new HashMap();
    }

    public JdoAttribute declareAttribute(String attribName, Class type)
    {
        if (declaredAttributes.containsKey(name))
        {
            throw new RuntimeException("Attribute " + name + " declared");
        }
        JdoAttribute newAttrib = new JdoAttribute(this, attribName, type);
        declaredAttributes.put(attribName, newAttrib);
        return newAttrib;
    }

    public boolean equals(Object o)
    {
        if ((null == o) || (!(o instanceof JdoGraphEntityClass)))
        {
            return false;
        }
        JdoGraphEntityClass oo = (JdoGraphEntityClass) o;
        return getName().equals(oo.getName());
    }

    public JdoAttribute getAttribute(String attribName)
    {
        // Check if the attribute declaration is in our class.
        if (declaredAttributes.containsKey(attribName))
        {
            return (JdoAttribute) declaredAttributes.get(attribName);
        }
        throw new RuntimeException("An attribute named " + name + " is not defined in this graph entity class.");
    }

    public Collection getDeclaredAttributes()
    {
        return declaredAttributes.values();
    }

    public String getName()
    {
        return name;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        return "JdoGraphEntityClass[" + name + "]";
    }
}