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
package org.datanucleus.samples.models.graph;

public class JdoAttribute
{
    public JdoAttribute(JdoGraphEntityClass geClass, String name, Class type) 
    {
        this.geClass = geClass;
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    private Class type;
    private String name;
    private JdoGraphEntityClass geClass;

    public JdoGraphEntityClass getGeClass()
    {
        return geClass;
    }

    public void setGeClass(JdoGraphEntityClass geClass)
    {
        this.geClass = geClass;
    }

    public Class getType()
    {
        return type;
    }

    public void setType(Class type)
    {
        this.type = type;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean equals(Object obj)
    {
        return (obj instanceof JdoAttribute) && ((JdoAttribute) obj).name.equals(name);
    }
}