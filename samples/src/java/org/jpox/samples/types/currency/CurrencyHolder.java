/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.types.currency;

import java.util.Currency;

import org.datanucleus.tests.TestObject;

/**
 * Container class with Currency field.
 */
public class CurrencyHolder extends TestObject
{
    private static int instanceNumber = 0;
    private Currency currencyField;

    public CurrencyHolder()
    {
        super();
    }

    public Currency getCurrencyField()
    {
        return currencyField;
    }

    public void setCurrencyField(Currency curr)
    {
        this.currencyField = curr;
    }

    /**
     * Fills all of the object's fields with random data values. Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */
    public void fillRandom()
    {
        // Not quite a random method, but we set different values depending on the instance number
        if ((instanceNumber/3)*3 == instanceNumber)
        {
            currencyField = Currency.getInstance("GBP");
        }
        else if ((instanceNumber/2)*2 == instanceNumber)
        {
            currencyField = Currency.getInstance("EUR");
        }
        else
        {
            currencyField = null;
        }
        instanceNumber++;
    }

    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read back correctly.
     * @param obj the reference object with which to compare
     * @return  <code>true</code> if this object is equal to the obj argument; <code>false</code> otherwise.
     */
    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof CurrencyHolder))
            return false;

        CurrencyHolder w = (CurrencyHolder)obj;

        if (currencyField == null) 
        {
            if (w.currencyField != null) 
            {
                return false; 
            }
        }
        else if (!currencyField.equals(w.currencyField)) 
        {
            return false;
        }

        return true;
    }

    /**
     * Returns a string representation for this object.
     * All of the field values are included in the string for debugging purposes.
     * @return a string representation for this object.
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  currencyField = ").append(currencyField);
        s.append('\n');

        return s.toString();
    }
}