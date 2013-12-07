/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;

import junit.framework.Assert;

import org.datanucleus.tests.TestObject;

/**
 * A test object for testing inverse <code>java.util.List</code> fields.
 * 
 */
public class InverseListFieldTester extends TestObject implements InstanceCallbacks
{
    private List inverseList = new ArrayList();

    /**
     * Constructs an empty map field tester object.
     */

    public InverseListFieldTester()
    {
    }


    public Object clone()
    {
        InverseListFieldTester lft = (InverseListFieldTester)super.clone();

        ArrayList il = new ArrayList();

        ListIterator i = inverseList.listIterator();

        while (i.hasNext())
        {
            InverseListValue ilv = (InverseListValue)((InverseListValue)i.next()).clone();

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
