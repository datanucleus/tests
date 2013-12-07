/******************************************************************
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
*****************************************************************/
package org.datanucleus.samples.widget;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.datanucleus.tests.TestHelper;


public class DecimalWidget extends Widget
{
    private BigInteger bigIntegerField;
    private BigDecimal bigDecimalField;

    public DecimalWidget()
    {
        super();
    }


    public BigInteger getBigIntegerField()
    {
        return bigIntegerField;
    }


    public BigDecimal getBigDecimalField()
    {
        return bigDecimalField;
    }


    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields (with the exception of <code>id</code>) will also be
     * assigned <code>null</code> on a random basis.
     */

    public void fillRandom()
    {
        super.fillRandom();

        /*
         * The number of bits specified here must transform to a max number of
         * decimal digits that will accomodate the most limited known DBMS,
         * which at present is Firebird (18 digits).  2^59 is the largest power
         * of two that fits in 18 decimal digits.
         */
        int numRandBits = 59;

        // As of 3.23.55 MySQL was known to fail (return values that are close but not equal) with values >= 2^53.
        if (TestHelper.getJDBCSubprotocolForDatastore(1).equals("mysql"))
        {
            numRandBits = 52;
        }

        bigIntegerField = nextNull() ? null : new BigInteger(numRandBits, r);
        bigDecimalField = nextNull() ? null : new BigDecimal(new BigInteger(numRandBits, r), 2);
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

        if (!(obj instanceof DecimalWidget) || !super.compareTo(obj))
            return false;

        DecimalWidget w = (DecimalWidget)obj;

        if (bigIntegerField == null) { if (w.bigIntegerField != null) return false; }
        else if (!bigIntegerField.equals(w.bigIntegerField)) return false;

        if (bigDecimalField == null) { if (w.bigDecimalField != null) return false; }
        else if (bigDecimalField.compareTo(w.bigDecimalField) != 0) return false;

        return true;
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

        s.append("  bigIntegerField = ").append(bigIntegerField);
        s.append('\n');
        s.append("  bigDecimalField = ").append(bigDecimalField);
        s.append('\n');

        return s.toString();
    }
}
