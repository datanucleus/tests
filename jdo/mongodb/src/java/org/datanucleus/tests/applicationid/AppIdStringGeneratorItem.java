/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributions
    ...
***********************************************************************/
package org.datanucleus.tests.applicationid;


import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Sample model class that has a String field that is to be generated as IDENTITY.
 */
@PersistenceCapable
public class AppIdStringGeneratorItem
{
    @Persistent(primaryKey="true", valueStrategy=IdGeneratorStrategy.IDENTITY)
    private String identifier; // Generated

    protected String name=null;

    public AppIdStringGeneratorItem(String name)
    {
        this.name = name;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}