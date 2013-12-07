/**********************************************************************
Copyright (c) 2007 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.annotations.idclass;

import java.io.Serializable;

/**
 * Primary key with private/protected modifiers for the fields.
 * @version $Revision: 1.1 $
 */
public class IdClassPrivateModifierPK implements Serializable
{
    private String name;
    private int name2;
    
    public IdClassPrivateModifierPK() {}
    
    public IdClassPrivateModifierPK(String name)
    {
        this.name = name;
    }

    public int getName2()
    {
        return name2;
    }

    public boolean equals(Object obj)
    {
        return (obj instanceof IdClassPrivateModifierPK) && name.equals(obj);
    }
    
    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        return name;
    }
}