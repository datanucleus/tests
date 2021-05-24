/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

/**
 * Class with id-class defined where the PK class uses accessors for accessing the fields.
 *
 * @version $Revision: 1.1 $
 */
@Entity
@IdClass(IdClassAccessorsPK.class)
public class IdClassAccessors implements Serializable
{
    private static final long serialVersionUID = -2416432717521790387L;

    private IdClassAccessorsPK compoundPK;

    private String name;
    private String basic;
    private boolean free;

    public IdClassAccessors()
    {
    }

    @Id
    public IdClassAccessorsPK getCompoundPK()
    {
        return compoundPK;
    }

    public void setCompoundPK(IdClassAccessorsPK compoundPK)
    {
        this.compoundPK = compoundPK;
    }
    
    @Basic
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Basic
    public String getBasic()
    {
        return basic;
    }

    public void setBasic(String basic)
    {
        this.basic = basic;
    }

    @Column(name="FFFF")
    public boolean isFree()
    {
        return free;
    }
    
    public void setFree(boolean free)
    {
        this.free = free;
    }
}