/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;


import junit.framework.Assert;


public class HashSetWidget extends Widget implements HasNormalSetField, InstanceCallbacks
{
    private HashSet normalSet;
    private int numWidgets;


    public HashSetWidget()
    {
        normalSet = new HashSet();
    }


    public Set getNormalSet()
    {
        return normalSet;
    }


    public int getNumWidgets()
    {
        return numWidgets;
    }


    public Object clone()
    {
        HashSetWidget sw = (HashSetWidget)super.clone();

        HashSet ns = new HashSet();

        Iterator i = normalSet.iterator();

        while (i.hasNext())
        {
            Widget w = (Widget)((Widget)i.next()).clone();

            ns.add(w);
        }

        sw.normalSet = ns;

        return sw;
    }


    /**
     * Fills all of the object's fields with random data values.
     */

    public void fillRandom()
    {
        fillRandom(r.nextInt(5));
    }


    public void fillRandom(int numWidgets)
    {
        fillRandom(numWidgets, false);
    }


    /**
     * Fills the collection fields with the given number of random Widget
     * objects.
     */

    public void fillRandom(int numWidgets, boolean includeSetWidgets)
    {
        super.fillRandom();

        /*
         * Clear normalSet iteratively in order to test remove().
         */
        PersistenceManager myPM = JDOHelper.getPersistenceManager(this);
        Iterator i = new ArrayList(normalSet).iterator();

        while (i.hasNext())
        {
            Object obj = i.next();

            Assert.assertTrue("normalSet.remove() did not return true after removing existing object", normalSet.remove(obj));
            Assert.assertTrue("normalSet.remove() did not return false attempting to remove non-existent object", !normalSet.remove(obj));

            if (myPM != null)
                myPM.deletePersistent(obj);
        }

        /*
         * Fill up normalSet with random Widget objects of random types.
         */

        this.numWidgets = numWidgets;

        if( numWidgets > 0 )
        {
            // In general, must have at least one date
            Widget obj = new DateWidget();
            obj.fillRandom();
            normalSet.add(obj);
            numWidgets--;
        }
        
        while (numWidgets-- > 0)
        {
            Widget obj;

            switch (r.nextInt(includeSetWidgets ? 5 : 4))
            {
                case 0:
                default:
                    obj = new Widget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;

                case 1:
                    obj = new DateWidget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;

                case 2:
                    obj = new StringWidget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;

                case 3:
                    obj = new FloatWidget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;

                case 4:
                    obj = new HashSetWidget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;
            }
        }
    }


    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     *
     * @param   obj     the reference object with which to compare
     *
     * @return  <code>true</code> if this object is equal to the obj argument;
     *          <code>false</code> otherwise.
     */

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof HashSetWidget) || !super.compareTo(obj))
            return false;

        HashSetWidget w = (HashSetWidget)obj;

        return compareSet(normalSet, w.normalSet);
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

        s.append("  normalSet = ").append(normalSet);
        s.append('\n');
        s.append("  numWidgets = ").append(numWidgets);
        s.append('\n');

        return s.toString();
    }


    public void jdoPostLoad() {}
    public void jdoPreClear() {}
    public void jdoPreStore() {}


    public void jdoPreDelete()
    {
        PersistenceManager myPM = JDOHelper.getPersistenceManager(this);
        Object[] elements = normalSet.toArray();

        normalSet.clear();

        myPM.deletePersistentAll(elements);
    }
}
