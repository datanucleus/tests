/**********************************************************************
Copyright (c) 2003 Mike Martin and others. All rights reserved.
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

import java.io.Serializable;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

public class Widget extends TestObject implements InstanceCallbacks, Serializable
{
    private boolean booleanField;

    private Boolean booleanObjField;

    private byte byteField;

    private Byte byteObjField;

    private char charField;

    private Character charObjField;

    private short shortField;

    private Short shortObjField;

    private int intField;

    private Integer intObjField;

    private long longField;

    private Long longObjField;

    public Widget()
    {
    }

    public boolean getBooleanField()
    {
        return booleanField;
    }

    public Boolean getBooleanObjField()
    {
        return booleanObjField;
    }

    public byte getByteField()
    {
        return byteField;
    }

    public Byte getByteObjField()
    {
        return byteObjField;
    }

    public char getCharField()
    {
        return charField;
    }

    public Character getCharObjField()
    {
        return charObjField;
    }

    public short getShortField()
    {
        return shortField;
    }

    public Short getShortObjField()
    {
        return shortObjField;
    }

    public int getIntField()
    {
        return intField;
    }

    public Integer getIntObjField()
    {
        return intObjField;
    }

    public long getLongField()
    {
        return longField;
    }

    public Long getLongObjField()
    {
        return longObjField;
    }

    public void setByteField(byte byteField)
    {
        this.byteField = byteField;
    }

    public void setShortField(short shortField)
    {
        this.shortField = shortField;
    }

    public void setIntField(int intField)
    {
        this.intField = intField;
    }

    public byte setByteFieldRandom()
    {
        byte b = nextByte();

        this.byteField = b;

        return b;
    }

    /**
     * Fills all of the object's fields with random data values. Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */

    public void fillRandom()
    {
        booleanField = r.nextBoolean();
        booleanObjField = nextNull() ? null : new Boolean(r.nextBoolean());
        byteField = nextByte();
        byteObjField = nextNull() ? null : new Byte(nextByte());
        charField = nextCharacter();
        charObjField = nextNull() ? null : new Character(nextCharacter());
        shortField = (short) (r.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE);
        shortObjField = nextNull() ? null : new Short((short) (r.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE));
        intField = r.nextInt();
        intObjField = nextNull() ? null : new Integer(r.nextInt());
        longField = r.nextLong();
        longObjField = nextNull() ? null : new Long(r.nextLong());
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

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof Widget))
            return false;

        Widget w = (Widget) obj;

        if (booleanObjField == null)
        {
            if (w.booleanObjField != null)
            {
                return false;
            }
        }
        else if (!booleanObjField.equals(w.booleanObjField))
        {
            return false;
        }

        if (byteObjField == null)
        {
            if (w.byteObjField != null)
            {
                return false;
            }
        }
        else if (!byteObjField.equals(w.byteObjField))
        {
            return false;
        }

        if (charObjField == null)
        {
            if (w.charObjField != null)
            {
                return false;
            }
        }
        else if (!charObjField.equals(w.charObjField))
        {
            return false;
        }

        if (shortObjField == null)
        {
            if (w.shortObjField != null)
            {
                return false;
            }
        }
        else if (!shortObjField.equals(w.shortObjField))
        {
            return false;
        }

        if (intObjField == null)
        {
            if (w.intObjField != null)
            {
                return false;
            }
        }
        else if (!intObjField.equals(w.intObjField))
            return false;

        if (longObjField == null)
        {
            if (w.longObjField != null)
            {
                return false;
            }
        }
        else if (!longObjField.equals(w.longObjField))
        {
            return false;
        }

        return booleanField == w.booleanField && byteField == w.byteField && 
            charField == w.charField && shortField == w.shortField && 
            intField == w.intField && longField == w.longField;
    }

    /**
     * Returns a string representation for this object. All of the field values
     * are included in the string for debugging purposes.
     * @return a string representation for this object.
     */

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  booleanField = ").append(booleanField);
        s.append('\n');
        s.append("  booleanObjField = ").append(booleanObjField);
        s.append('\n');
        s.append("  byteField = ").append(byteField);
        s.append('\n');
        s.append("  byteObjField = ").append(byteObjField);
        s.append('\n');
        s.append("  charField = ").append(charField);
        s.append('\n');
        s.append("  charObjField = ").append(charObjField);
        s.append('\n');
        s.append("  shortField = ").append(shortField);
        s.append('\n');
        s.append("  shortObjField = ").append(shortObjField);
        s.append('\n');
        s.append("  intField = ").append(intField);
        s.append('\n');
        s.append("  intObjField = ").append(intObjField);
        s.append('\n');
        s.append("  longField = ").append(longField);
        s.append('\n');
        s.append("  longObjField = ").append(longObjField);
        s.append('\n');

        return s.toString();
    }

    public void jdoPostLoad()
    {
    }

    public void jdoPreClear()
    {
    }

    public void jdoPreDelete()
    {
    }

    public void jdoPreStore()
    {
        /*
         * This is here for the benefit of one particular test in
         * BasicStorageTest.updateObjects(), which tests the updating of a
         * default-fetch-group field when the default fetch group has not been
         * loaded. All it needs to do is touch a couple of DFG fields.
         */
        if (booleanField && intField < 0)
            nextCharacter();
    }
}