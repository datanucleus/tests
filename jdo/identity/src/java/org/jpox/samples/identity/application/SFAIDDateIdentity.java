/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
 * SingleField identity using StringIdentity
 *
 * @version $Revision: 1.1 $
 */
public class SFAIDDateIdentity extends TestObject implements InstanceCallbacks
{
    private java.util.Date date;

    private String description;

    public SFAIDDateIdentity()
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

    public java.util.Date getDate()
    {
        return date;
    }

    public void setDate(java.util.Date date)
    {
        this.date = date;
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
        this.date = new java.util.Date();
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
        if (!(obj instanceof SFAIDDateIdentity))
            return false;

        SFAIDDateIdentity other = (SFAIDDateIdentity) obj;

        return this.date.getTime() == other.date.getTime() && this.description.equals(other.description);
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  date = ").append(date);
        s.append('\n');
        s.append("  description = ").append(description);
        s.append('\n');
        return s.toString();
    }
}