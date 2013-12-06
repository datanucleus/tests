/******************************************************************
 * Copyright (c) 13-Oct-2004 Andy Jefferson and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * 
 *  
 *
 * Contributors:
 *     ...
 *****************************************************************/
package org.jpox.samples.inheritance;

/**
 * Base class for Inheritance tests - sample "F".
 * This sample has 2 subclasses, with one of these subclasses having a subclass.
 * @version $Revision: 1.1 $
 */
public class FBase
{
    private long id;
    private String name;
    private int revision;

    public FBase()
    {
        // Do nothing
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getRevision()
    {
        return revision;
    }

    public void setRevision(int revision)
    {
        this.revision = revision;
    }
}