/**********************************************************************
Copyright (c) 2019 Andy Jefferson and others. All rights reserved. 
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
package org.datanucleus.samples.valuegeneration;

import java.util.UUID;

/**
 * Test case for the "uuid" generator using a UUID object in the class.
 */
public class UUIDObjectGeneratorItem
{
    private UUID identifier;

    protected String name=null;

    protected UUIDObjectGeneratorItem()
    {
    }

    public UUIDObjectGeneratorItem(String name)
    {
        this.name   = name;
    }

    public UUID getIdentifier()
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

    public String toString()
    {
        return "UUIDGeneratorItem : " + name;
    }
}