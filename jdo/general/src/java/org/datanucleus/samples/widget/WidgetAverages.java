/**********************************************************************
Copyright (c) 2002 Mike Martin (TJDO) and others. All rights reserved.
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
***********************************************************************/
package org.datanucleus.samples.widget;

import org.datanucleus.tests.TestObject;

/**
 * Candidate class for use in JPOXSQL queries.
 * 
 * @version $Revision: 1.2 $
 */
public class WidgetAverages extends TestObject
{
    private boolean booleanValue = false;
    private short avgShortValue = 0;
    private int avgIntValue = 0;

    public WidgetAverages()
    {
    }

    public boolean getBooleanValue()
    {
        return booleanValue;
    }

    public short getAvgShortValue()
    {
        return avgShortValue;
    }

    public int getAvgIntValue()
    {
        return avgIntValue;
    }

    public void fillRandom()
    {
        booleanValue  = r.nextBoolean();
        avgShortValue = (short)(r.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE);
        avgIntValue   = r.nextInt();
    }

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof WidgetAverages))
            return false;

        WidgetAverages wv = (WidgetAverages)obj;

        return booleanValue  == wv.booleanValue
            && avgShortValue == wv.avgShortValue
            && avgIntValue   == wv.avgIntValue;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  booleanValue = ").append(booleanValue);
        s.append('\n');
        s.append("  avgShortValue = ").append(avgShortValue);
        s.append('\n');
        s.append("  avgIntValue = ").append(avgIntValue);
        s.append('\n');

        return s.toString();
    }
}