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
package org.datanucleus.samples.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;

import org.junit.Assert;

import org.datanucleus.tests.TestObject;

/**
 * A test object for testing inverse <code>java.util.List</code> fields.
 */
public class InverseListFieldTester extends TestObject implements InstanceCallbacks
{
    private List<InverseListValue> inverseList = new ArrayList<>();

    /**
     * Constructs an empty map field tester object.
     */

    public InverseListFieldTester()
    {
    }

    public Object clone()
    {
        InverseListFieldTester lft = (InverseListFieldTester)super.clone();

        ArrayList<InverseListValue> il = new ArrayList<>();
        ListIterator<InverseListValue> i = inverseList.listIterator();
        while (i.hasNext())
        {
            InverseListValue ilv = (InverseListValue)i.next().clone();

            ilv.setOwner(lft);
            il.add(ilv);
        }
        lft.inverseList = il;

        return lft;
    }


    public void fillRandom()
    {
        throw new UnsupportedOperationException();
    }


    public boolean compareTo(Object obj)
    {
        throw new UnsupportedOperationException();
    }


    public void jdoPreStore() {}
    public void jdoPostLoad() {}
    public void jdoPreClear() {}

    public void jdoPreDelete()
    {
    }


    public void addListValue(InverseListValue ilv)
    {
        Assert.assertTrue("inverseList already contains value: " + ilv, !inverseList.contains(ilv));

        Assert.assertTrue("add() returned false adding value: " + ilv, inverseList.add(ilv));

        Assert.assertTrue("inverseList does not contain value: " + ilv, inverseList.contains(ilv));

    }


    public void removeListValue(InverseListValue ilv)
    {
        Assert.assertSame(this, ilv.getOwner());

        if (JDOHelper.isPersistent(ilv))
            JDOHelper.getPersistenceManager(ilv).deletePersistent(ilv);
        else
        {
            inverseList.remove(ilv);
        }

        Assert.assertTrue("inverseList still contains value", !inverseList.contains(ilv));
    }


    public void assertListEqual(List values)
    {
    	if( inverseList == null )
    	{
			Assert.assertTrue(values == null);
    	}
    	else
    	{
			Assert.assertTrue(inverseList.containsAll(values));
	    }
    }
}
