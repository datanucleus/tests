/******************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved. 
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
*****************************************************************/
package org.jpox.samples.inheritance;

/**
 * Base Inheritance class with subclass using "superclass-table".
 * Used as an element in a "normal" 1-N relationship.
 * @version $Revision: 1.1 $
 */
public class EElemBase
{
    private long id;
    private String name;
    private int revision;

    /**
     * Default constructor
     */
    public EElemBase()
    {
        // Do nothing
    }

    /**
     * Accessor for the id
     * @return Returns the id.
     */
    public long getId()
    {
        return id;
    }

    /**
     * Accessor for the name
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Mutator for the name
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Accessor for revision.
     * @return Returns the revision.
     */
    public int getRevision()
    {
        return revision;
    }

    /**
     * Mutator for revision.
     * @param revision The revision to set.
     */
    public void setRevision(int revision)
    {
        this.revision = revision;
    }
}