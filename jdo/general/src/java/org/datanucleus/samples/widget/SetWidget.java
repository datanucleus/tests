/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;


import junit.framework.Assert;


public class SetWidget extends Widget
    implements HasNormalSetField, HasInverseSetField, InstanceCallbacks
{
    private Set normalSet;
    private Set inverseSet;
    private int numWidgets;
    private int numElementWidgets;


    public SetWidget()
    {
        normalSet = new HashSet();
        inverseSet = new HashSet();
        numWidgets = 0;
        numElementWidgets = 0;
    }


    public Set getNormalSet()
    {
        return normalSet;
    }


    public Set getInverseSet()
    {
        return inverseSet;
    }


    public int getNumWidgets()
    {
        return numWidgets;
    }


    public int getNumElementWidgets()
    {
        return numElementWidgets;
    }


    public Object clone()
    {
        SetWidget sw = (SetWidget)super.clone();

        HashSet ns = new HashSet();
        HashSet is = new HashSet();

        Iterator i = normalSet.iterator();

        while (i.hasNext())
        {
            Widget w = (Widget)((Widget)i.next()).clone();

            ns.add(w);

            if (w instanceof ElementWidget)
            {
                ElementWidget ew = (ElementWidget)w;
                ew.setOwner(sw);
                is.add(ew);
            }
        }

        sw.normalSet = ns;
        sw.inverseSet = is;

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
        Iterator i = normalSet.iterator();

        while (i.hasNext())
        {
            Object obj = i.next();

            Assert.assertTrue("normalSet.remove() did not return true after removing existing object", normalSet.remove(obj));
            Assert.assertTrue("normalSet.remove() did not return false attempting to remove non-existent object", !normalSet.remove(obj));

            if (myPM != null)
            {
                myPM.deletePersistent(obj);

                Assert.assertTrue("inverseSet should not contain deleted object", !inverseSet.contains(obj));
            }
            else
            {
                Assert.assertTrue("inverseSet.remove() did not return true after removing existing object", inverseSet.remove(obj));
            }
        }

        /*
         * Fill up normalSet with random Widget objects of random types.
         * Any ElementWidget objects are added to inverseSet as well.
         */
        this.numWidgets = numWidgets;
        numElementWidgets = 0;

        if (numWidgets > 0)
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

            switch (r.nextInt(includeSetWidgets ? 6 : 5))
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
                    obj = new ElementWidget(this);
                    obj.fillRandom();
                    normalSet.add(obj);
                    inverseSet.add(obj);
                    ++numElementWidgets;
                    break;

                case 4:
                    obj = new FloatWidget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;

                case 5:
                    obj = new SetWidget();
                    obj.fillRandom();
                    normalSet.add(obj);
                    break;
            }
        }
    }


    private void validate()
    {
        Assert.assertEquals("numWidgets != normalSet.size(): " + this, numWidgets, normalSet.size());
        Assert.assertEquals("numElementWidgets != inverseSet.size(): " + this, numElementWidgets, inverseSet.size());
        Assert.assertTrue("normalSet does not contain all elements of inverseSet: " + this, normalSet.containsAll(inverseSet));
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
        validate();

        if (obj == this)
            return true;

        if (!(obj instanceof SetWidget) || !super.compareTo(obj))
            return false;

        SetWidget w = (SetWidget)obj;

        w.validate();

        return compareSet(normalSet, w.normalSet) &&
               compareSet(inverseSet, w.inverseSet);
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
        s.append("  inverseSet = ").append(inverseSet);
        s.append('\n');
        s.append("  numWidgets = ").append(numWidgets);
        s.append('\n');
        s.append("  numElementWidgets = ").append(numElementWidgets);
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

        // With cached collections in JPOX this won't work
//      if (!inverseSet.isEmpty())
//      {
//          throw new RuntimeException("Elements still left in inverseSet");
//      }
    }
}
