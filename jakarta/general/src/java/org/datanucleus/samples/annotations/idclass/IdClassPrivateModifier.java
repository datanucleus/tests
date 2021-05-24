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
package org.datanucleus.samples.annotations.idclass;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

/**
 * Class with id-class defined where the PK uses private modifiers for the fields.
 * @version $Revision: 1.1 $
 */
@Entity
@IdClass(IdClassPrivateModifierPK.class)
public class IdClassPrivateModifier implements Serializable
{
    private static final long serialVersionUID = 306810278685713312L;

    @Id
    private String name;

    @Id
    private int name2;

    @Column(name="FFFF")
    private boolean free;
    
    public IdClassPrivateModifier()
    {
    }

    public String getName()
    {
        return name;
    }

    public int getName2()
    {
        return name2;
    }
    
    public boolean isFree()
    {
        return free;
    }
    
    public void setFree(boolean free)
    {
        this.free = free;
    }
}