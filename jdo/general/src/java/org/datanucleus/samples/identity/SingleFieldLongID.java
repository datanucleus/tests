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
package org.datanucleus.samples.identity;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * SingleField identity using LongIdentity
 *
 * @version $Revision: 1.2 $
 */
public class SingleFieldLongID extends TestObject implements InstanceCallbacks
{
    private long code;

    private String description;

    /**
     * Constructor.
     */
    public SingleFieldLongID()
    {
        super();
    }

    /**
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * @return code
     */
    public long getCode()
    {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(long code)
    {
        this.code = code;
    }

    /*
     * (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPostLoad()
     */
    public void jdoPostLoad()
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPreStore()
     */
    public void jdoPreStore()
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPreClear()
     */
    public void jdoPreClear()
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see javax.jdo.InstanceCallbacks#jdoPreDelete()
     */
    public void jdoPreDelete()
    {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.jpox.samples.utils.TestObject#fillRandom()
     */
    public void fillRandom()
    {
        this.code = r.nextInt();
        fillUpdateRandom();
    }

    /*
     * (non-Javadoc)
     * @see org.jpox.samples.utils.TestObject#fillRandom()
     */
    public void fillUpdateRandom()
    {
        description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble() * 1000);
    }

    /*
     * (non-Javadoc)
     * @see org.jpox.samples.utils.TestObject#compareTo(java.lang.Object)
     */
    public boolean compareTo(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof SingleFieldLongID))
            return false;

        SingleFieldLongID other = (SingleFieldLongID) obj;

        return this.code == other.code && this.description.equals(other.description);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
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