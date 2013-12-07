/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;

import junit.framework.Assert;

import org.datanucleus.tests.TestObject;

public class InverseListValue extends TestObject implements InstanceCallbacks
{
    private InverseListFieldTester owner;
    private String strField;
    private Widget objField;

    public InverseListValue()
    {
        this.owner = null;
        this.strField = null;
        this.objField = null;
    }


    public void setOwner(InverseListFieldTester owner)
    {
        this.owner = owner;
    }


    public InverseListFieldTester getOwner()
    {
        return owner;
    }

    public void fillRandom()
    {
        jdoPreDelete();

        strField = nextNull() ? null : nextString(r.nextInt(21));

        if (nextNull())
            objField = null;
        else
        {
            Widget w = new Widget();
            w.fillRandom();
            objField = w;
        }

    }


    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof InverseListValue))
            return false;

        InverseListValue imv = (InverseListValue)obj;

        return (owner == null ? imv.owner == null : owner.equals(imv.owner))
            && (strField == null ? imv.strField == null : strField.equals(imv.strField))
            && (objField == null ? imv.objField == null : objField.equals(imv.objField));
    }


    public void assertEquals(InverseListValue imv)
    {
        Assert.assertEquals(strField, imv.strField);
        Assert.assertEquals(objField, imv.objField);
    }


    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  owner = ").append(JDOHelper.getObjectId(owner));
        s.append('\n');
        s.append("  strField = ").append(strField);
        s.append('\n');
        s.append("  objField = ").append(objField);
        s.append('\n');

        return s.toString();
    }

    public void jdoPostLoad() {}

    public void jdoPreStore() {}

    public void jdoPreClear() {}

    public void jdoPreDelete() {}
}
