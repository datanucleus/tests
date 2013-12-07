/**********************************************************************
Copyright (c) Jul 14, 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.metadata.user;

/**
 * @author Erik Bengtson
 * @version $Revision: 1.1 $
 */
public class User2
{
    public String id;
    
    public String name;
    
    /**
     * 
     */
    public User2()
    {
        super();
    }
    /**
     * Accessor for id
     * @return Returns the id.
     */
    public final String getId()
    {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public final void setId(String id)
    {
        this.id = id;
    }
    /**
     * Accessor for name
     * @return Returns the name.
     */
    public final String getName()
    {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public final void setName(String name)
    {
        this.name = name;
    }
}
