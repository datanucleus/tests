/******************************************************************
Copyright (c) 2003 Erik Bengtson and others. All rights reserved. 
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
*****************************************************************/
package org.datanucleus.tests;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.jdo.JDOHelper;

/**
 * Sample test object, providing population helpers for fields.
 * 
 * @version $Revision: 1.2 $
 */
public abstract class TestObject implements Cloneable
{
    protected static Random r = new Random(0);

    public static boolean allowNegativeByteValues = true;

    protected static byte nextByte()
    {
        if (allowNegativeByteValues)
        {
            return (byte) (r.nextInt(Byte.MAX_VALUE * 2) - Byte.MAX_VALUE);
        }
        else
        {
            return (byte) r.nextInt(Byte.MAX_VALUE + 1);
        }
    }

    protected static char nextCharacter()
    {
        char c = (char) ('!' + r.nextInt(93));
        return c;
    }

    protected static String nextString(int length)
    {
        StringBuffer s = new StringBuffer();

        while (length-- > 0)
            s.append(nextCharacter());

        return s.toString();
    }

    /*
     * Indicates whether or not the next random nullable field value should be
     * null. Returns true approx. 20% of the time.
     */
    protected static boolean nextNull()
    {
        return r.nextInt(5) < 1;
    }

    public Object clone()
    {
        Object obj = null;

        try
        {
            obj = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
        }

        return obj;
    }

    public abstract void fillRandom();

    public void fillUpdateRandom()
    {
        fillRandom();
    }

    /**
     * Indicates whether some other object is "equal to" this one. By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     * @param obj the reference object with which to compare
     * @return <code>true</code> if this object is equal to the obj argument;
     * <code>false</code> otherwise.
     */
    public abstract boolean compareTo(Object obj);

    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        Object id = JDOHelper.getObjectId(this);

        return id == null ? super.equals(obj) : id.equals(JDOHelper.getObjectId(obj));
    }

    public int hashCode()
    {
        Object id = JDOHelper.getObjectId(this);

        return id == null ? super.hashCode() : id.hashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(getClass().getName() + ":");

        s.append("  JVM id = ").append(System.identityHashCode(this));
        s.append('\n');

        Object id = JDOHelper.getObjectId(this);
        s.append("  JDO id = ").append(id);
        s.append('\n');

        return s.toString();
    }

    /**
     * Compares two sets of TestObjects. Returns true if and only if the two
     * sets contain the same number of objects and each element of the first set
     * has a corresponding element in the second set whose fields compare equal
     * according to the compareTo() method.
     * @return <tt>true</tt> if the sets compare equal, <tt>false</tt>
     * otherwise.
     */
    @SuppressWarnings("unchecked")
    public static boolean compareSet(Set s1, Set s2)
    {
        if (s1 == null)
        {
            return s2 == null;
        }
        else if (s2 == null)
        {
            return false;
        }

        if (s1.size() != s2.size())
        {
            return false;
        }

        s2 = new HashSet(s2);

        Iterator i = s1.iterator();

        while (i.hasNext())
        {
            TestObject obj = (TestObject) i.next();

            boolean found = false;
            Iterator j = s2.iterator();

            while (j.hasNext())
            {
                if (obj.compareTo(j.next()))
                {
                    j.remove();
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares two lists of TestObjects. Returns true if and only if the two
     * lists contain the same number of objects and each element of the first
     * list has a corresponding element in the second list whose fields compare
     * equal according to the compareTo() method.
     * @return <tt>true</tt> if the lists compare equal, <tt>false</tt>
     * otherwise.
     */

    public static boolean compareList(List l1, List l2)
    {
        if (l1 == null)
        {
            return l2 == null;
        }
        else if (l2 == null)
        {
            return false;
        }

        if (l1.size() != l2.size())
        {
            return false;
        }

        for (int i = 0; i < l1.size(); i++)
        {
            if (!((TestObject) l1.get(i)).compareTo(l2.get(i)))
            {
                return false;
            }

        }

        return true;
    }
}