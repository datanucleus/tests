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

import javax.jdo.InstanceCallbacks;
import javax.jdo.JDOHelper;

import junit.framework.Assert;

import org.datanucleus.tests.TestObject;

/**
 * Sample value that is stored in a 1-N bidirectional Foreign-Key
 * relationship.
 * 
 * @version $Revision: 1.2 $
 */
public class InverseSetValue extends TestObject implements InstanceCallbacks
{
    private InverseSetFieldTester owner;
    private String strField;
    private Widget objField;

    public InverseSetValue()
    {
        this.owner = null;
        this.strField = null;
        this.objField = null;
    }

    public void setOwner(InverseSetFieldTester owner)
    {
        this.owner = owner;
    }

    public InverseSetFieldTester getOwner()
    {
        return owner;
    }

    public String getStrField()
    {
        return strField;
    }

    public void fillRandom()
    {
        jdoPreDelete();

        strField = nextNull() ? null : nextString(r.nextInt(21));

        if (nextNull())
        {
            objField = null;
        }
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
        {
            return true;
        }

        if (!(obj instanceof InverseSetValue))
        {
            return false;
        }

        InverseSetValue value = (InverseSetValue) obj;
        return (owner == null ? value.owner == null : owner.equals(value.owner))
            && (strField == null ? value.strField == null : strField.equals(value.strField))
            && (objField == null ? value.objField == null : objField.equals(value.objField));
    }

    public void assertEquals(InverseSetValue value)
    {
        Assert.assertEquals(strField, value.strField);
        Assert.assertEquals(objField, value.objField);
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
}