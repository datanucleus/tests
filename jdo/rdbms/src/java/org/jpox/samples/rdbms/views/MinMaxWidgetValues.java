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
package org.jpox.samples.rdbms.views;

import org.datanucleus.tests.TestObject;
import org.jpox.samples.rdbms.views.MinMaxWidgetValues;


public class MinMaxWidgetValues extends TestObject
{
    private boolean booleanValue;
    private byte minByteValue;
    private short minShortValue;
    private int maxIntValue;
    private long maxLongValue;

    protected MinMaxWidgetValues() {}

    public MinMaxWidgetValues(boolean booleanValue)
    {
        this.booleanValue  = booleanValue;
        this.minByteValue  = Byte.MAX_VALUE;
        this.minShortValue = Short.MAX_VALUE;
        this.maxIntValue   = Integer.MIN_VALUE;
        this.maxLongValue  = Long.MIN_VALUE;
    }

    public boolean getBooleanValue()
    {
        return booleanValue;
    }

    public byte getMinByteValue()
    {
        return minByteValue;
    }

    public short getMinShortValue()
    {
        return minShortValue;
    }

    public int getMaxIntValue()
    {
        return maxIntValue;
    }

    public long getMaxLongValue()
    {
        return maxLongValue;
    }

    public void setMinByteValue(byte minByteValue)
    {
        this.minByteValue = minByteValue;
    }

    public void setMinShortValue(short minShortValue)
    {
        this.minShortValue = minShortValue;
    }

    public void setMaxIntValue(int maxIntValue)
    {
        this.maxIntValue = maxIntValue;
    }

    public void setMaxLongValue(long maxLongValue)
    {
        this.maxLongValue = maxLongValue;
    }

    public void fillRandom()
    {
        booleanValue  = r.nextBoolean();
        minByteValue  = (byte)(r.nextInt(Byte.MAX_VALUE * 2) - Byte.MAX_VALUE);
        minShortValue = (short)(r.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE);
        maxIntValue   = r.nextInt();
        maxLongValue  = r.nextLong();
    }

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof MinMaxWidgetValues))
            return false;

        MinMaxWidgetValues wv = (MinMaxWidgetValues)obj;

        return booleanValue  == wv.booleanValue
            && minByteValue  == wv.minByteValue
            && minShortValue == wv.minShortValue
            && maxIntValue   == wv.maxIntValue
            && maxLongValue  == wv.maxLongValue;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  booleanValue = ").append(booleanValue);
        s.append('\n');
        s.append("  minByteValue = ").append(minByteValue);
        s.append('\n');
        s.append("  minShortValue = ").append(minShortValue);
        s.append('\n');
        s.append("  maxIntValue = ").append(maxIntValue);
        s.append('\n');
        s.append("  maxLongValue = ").append(maxLongValue);
        s.append('\n');

        return s.toString();
    }
}