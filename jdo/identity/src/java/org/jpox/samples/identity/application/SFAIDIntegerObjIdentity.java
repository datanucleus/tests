/**********************************************************************
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
**********************************************************************/
package org.jpox.samples.identity.application;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * SingleField identity using IntegerIdentity (object form).
 *
 * @version $Revision: 1.1 $
 */
public class SFAIDIntegerObjIdentity extends TestObject implements InstanceCallbacks
{
    private Integer code;

    private String description;

    public SFAIDIntegerObjIdentity()
    {
        super();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public void jdoPostLoad()
    {
    }

    public void jdoPreStore()
    {
    }

    public void jdoPreClear()
    {
    }

    public void jdoPreDelete()
    {
    }

    public void fillRandom()
    {
        this.code = new Integer(r.nextInt());
        fillUpdateRandom();
    }

    public void fillUpdateRandom()
    {
        description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble() * 1000);
    }

    public boolean compareTo(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SFAIDIntegerObjIdentity))
            return false;

        SFAIDIntegerObjIdentity other = (SFAIDIntegerObjIdentity) obj;

        if ((this.code == null && other.code != null) || (this.code != null && other.code == null))
        {
            return false;
        }
        if (this.code == null && other.code == null)
        {
            return this.description.equals(other.description);
        }
        return this.code.intValue() == other.code.intValue() && this.description.equals(other.description);
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  code = ").append(code);
        s.append('\n');
        s.append("  description = ").append(description);
        s.append('\n');
        return s.toString();
    }
}