/**********************************************************************
Copyright (c) 2003 Mike Martin (TJDO) and others. All rights reserved.
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
2004 Andy Jefferson - added toString()
    ...
**********************************************************************/
package org.datanucleus.samples.widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import junit.framework.Assert;

import org.datanucleus.tests.TestObject;

/**
 * Sample container for use in testing 1-N bidirectional FK relationships.
 * 
 * @version $Revision: 1.2 $
 */
public class InverseSetFieldTester extends TestObject implements InstanceCallbacks
{
    private Set elements = new HashSet();

    /**
     * Constructs an empty map field tester object.
     */
    public InverseSetFieldTester()
    {
    }

    public Object clone()
    {
        InverseSetFieldTester owner = (InverseSetFieldTester) super.clone();

        Set elementsClone = new HashSet();

        Iterator i = elements.iterator();

        while (i.hasNext())
        {
            InverseSetValue element = (InverseSetValue) ((InverseSetValue) i.next()).clone();

            element.setOwner(owner);
            elementsClone.add(element);
        }

        owner.elements = elementsClone;

        return owner;
    }

    public void fillRandom()
    {
        throw new UnsupportedOperationException();
    }

    public boolean compareTo(Object obj)
    {
        throw new UnsupportedOperationException();
    }

    public void jdoPreStore()
    {
    }
    public void jdoPostLoad()
    {
    }
    public void jdoPreClear()
    {
    }

    public void jdoPreDelete()
    {
        PersistenceManager pm = JDOHelper.getPersistenceManager(this);

        /* Delete all dependent objects. */
        pm.deletePersistentAll(elements);
    }

    public void addValue(InverseSetValue value)
    {
		value.setOwner(this);
		
        Assert.assertFalse(this.getClass().getName() + " already contains value: " + value, elements.contains(value));

        Assert.assertTrue("add() returned false adding value: " + value, elements.add(value));

        Assert.assertTrue(this.getClass().getName() + " does not contain value: " + value, elements.contains(value));
    }

    public void removeValue(InverseSetValue value)
    {
        Assert.assertSame(this, value.getOwner());

        elements.remove(value);
        if (JDOHelper.isPersistent(value))
        {
            JDOHelper.getPersistenceManager(value).deletePersistent(value);
        }

        Assert.assertFalse(this.getClass().getName() + " still contains value", elements.contains(value));
    }

    public void assertEqual(Set values)
    {
        Assert.assertEquals(elements.size(), values.size());
        Assert.assertTrue(elements.containsAll(values));
    }
    
    public Set getElements()
    {
        return elements;
    }

	/**
	 * @param elements The elements to set.
	 */
	public void setElements(Set elements) 
	{
		this.elements = elements;
	}
	
	public String toString()
	{
	    StringBuffer s = new StringBuffer(super.toString());

        s.append("  elements = {");
        Iterator iter=elements.iterator();
        while (iter.hasNext())
        {
            s.append(JDOHelper.getObjectId(iter.next()));
            if (iter.hasNext())
            {
                s.append(", ");
            }
        }
        s.append("}");

        return s.toString();
	}
}