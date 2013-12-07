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
package org.datanucleus.samples.identity;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * Sample for testing datastore identity
 * @version $Revision: 1.2 $
 */
public class SimpleDatastoreID extends TestObject implements InstanceCallbacks
{
    private String description;

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

    /*
     * (non-Javadoc)
     * @see org.jpox.samples.utils.TestObject#fillRandom()
     */
    public void fillRandom()
    {
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
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof SimpleDatastoreID))
        {
            return false;
        }

        SimpleDatastoreID other = (SimpleDatastoreID) obj;
        return description.equals(other.description);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  description = ").append(description);
        s.append('\n');
        return s.toString();
    }

    /**
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param string
     */
    public void setDescription(String string)
    {
        description = string;
    }

    public Object clone()
    {
        Object obj = null;

        obj = super.clone();

        return obj;
    }
}