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
    ...
**********************************************************************/
package org.datanucleus.samples.widget;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.datanucleus.tests.TestObject;

public class OwnerWidget extends TestObject
{
    /** An interface field so we can test 1-1 unidirectional with interfaces. */
    private Cloneable cloneable;

    /** A PC field so we can test 1-1 unidirectional with PC. */
    private TestObject testObject;

    public OwnerWidget()
    {
        cloneable = null;
        testObject = null;
    }

    public Object clone()
    {
        OwnerWidget ow = (OwnerWidget)super.clone();

        ow.cloneable = (Cloneable)((TestObject)cloneable).clone();
        ow.testObject = (TestObject)testObject.clone();

        return ow;
    }

    public Cloneable getCloneable()
    {
        return cloneable;
    }

    public TestObject getTestObject()
    {
        return testObject;
    }

    private Widget randomWidget()
    {
        Widget obj;

        switch (r.nextInt(3))
        {
            case 0:
            default:
                obj = new Widget();
                break;

            case 1:
                obj = new DateWidget();
                break;

            case 2:
                obj = new StringWidget();
                break;
        }

        obj.fillRandom();

        return obj;
    }

    /**
     * Fills all of the object's fields with random data values.
     */
    public void fillRandom()
    {
        PersistenceManager myPM = JDOHelper.getPersistenceManager(this);

        if (myPM != null)
        {
            myPM.deletePersistent(cloneable);
            myPM.deletePersistent(testObject);
        }

        cloneable  = randomWidget();
        testObject = randomWidget();
    }

    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     *
     * @param   obj     the reference object with which to compare
     * @return  <code>true</code> if this object is equal to the obj argument;
     *          <code>false</code> otherwise.
     */
    public boolean compareTo(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof OwnerWidget))
        {
            return false;
        }

        OwnerWidget w = (OwnerWidget)obj;

        return ((TestObject)cloneable).compareTo(w.cloneable) && testObject.compareTo(w.testObject);
    }

    /**
     * Returns a string representation for this object.  All of the field
     * values are included in the string for debugging purposes.
     *
     * @return  a string representation for this object.
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  cloneable = ").append(cloneable);
        s.append('\n');
        s.append("  testObject = ").append(testObject);
        s.append('\n');

        return s.toString();
    }
}