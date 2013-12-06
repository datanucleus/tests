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
package org.jpox.samples.types.basic;

import java.util.Calendar;

import org.datanucleus.tests.TestObject;

/**
 * Holder of all Date types to use as a test for persisting basic information.
 *
 * @version $Revision: 1.2 $
 */
public class DateHolder extends TestObject
{
    private java.util.Date dateField;
    private java.sql.Date sqlDateField;
    private java.sql.Time sqlTimeField;
    private java.sql.Timestamp sqlTimestampField;

    private java.util.Date dateJdbcTimestampField; // Alternative Date so it can be mapped in a different way

    public DateHolder()
    {
        super();
    }

    public java.util.Date getDateField()
    {
        return dateField;
    }

    public java.util.Date getDateJdbcTimestampField()
    {
        return dateJdbcTimestampField;
    }

    public java.sql.Date getSQLDateField()
    {
        return sqlDateField;
    }

    public java.sql.Time getSQLTimeField()
    {
        return sqlTimeField;
    }

    public java.sql.Timestamp getSQLTimestampField()
    {
        return sqlTimestampField;
    }

    public void setDateField(java.util.Date date)
    {
        this.dateField = date;
    }

    public void setDateJdbcTimestampField(java.util.Date date)
    {
        this.dateJdbcTimestampField = date;
    }

    public void setSQLDateField(java.sql.Date date)
    {
        this.sqlDateField = date;
    }

    public void setSQLTimeField(java.sql.Time time)
    {
        this.sqlTimeField = time;
    }

    public void setSQLTimestampField(java.sql.Timestamp timest)
    {
        this.sqlTimestampField = timest;
    }

    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields will also be assigned <code>null</code> on a random
     * basis.
     */
    public void fillRandom()
    {
        /*
         * The dates below are all made to be relative to an even second value
         * because we don't insist that the database manage fractional seconds
         * correctly.
         */
        dateField = nextNull() ? null : new java.util.Date((long)r.nextInt() * 1000);

        /*
         * We have to convert the random date value to String and back in order
         * to discard the time-of-day portion of the data, otherwise the field
         * won't compare exactly after it's been transferred to the database and
         * back.
         */
        java.sql.Date rndDate = new java.sql.Date((long)r.nextInt() * 1000);
        sqlDateField = nextNull() ? null : java.sql.Date.valueOf(rndDate.toString());
        java.sql.Time rndTime = new java.sql.Time((long)r.nextInt() * 1000);
        sqlTimeField = nextNull() ? null : java.sql.Time.valueOf(rndTime.toString());
        sqlTimestampField = nextNull() ? null : new java.sql.Timestamp((long)r.nextInt() * 1000);
        dateJdbcTimestampField = nextNull() ? null : new java.util.Date((long)r.nextInt() * 1000);
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
        if (this == obj)
            return true;

        if (!(obj instanceof DateHolder))
            return false;

        DateHolder w = (DateHolder)obj;
        if (dateField == null)
        {
            if (w.dateField != null)
            {
                return false; 
            }
        }
        else if (!dateField.equals(w.dateField))
        {
            return false;
        }

        if (sqlDateField == null)
        { 
            if (w.sqlDateField != null) return false; 
        }
        else if (!sqlDateField.equals(w.sqlDateField)) 
        {
            return false;
        }

        if (sqlTimeField == null)
        { 
            if (w.sqlTimeField != null) return false; 
        }
        else if (!sqlTimeField.equals(w.sqlTimeField)) 
        {
            return false;
        }

        if (sqlTimestampField == null) 
        { 
            if (w.sqlTimestampField != null) 
            {
                return false;
            }
        }
        else if (!sqlTimestampField.equals(w.sqlTimestampField))
        {
            return false;
        }

        /* we compare only YYYYMMDD HH:MM:ss */
        if (dateJdbcTimestampField == null)
        { 
            if (w.dateJdbcTimestampField != null)
            {
                return false;
            }
        }
        else
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateJdbcTimestampField);
            Calendar calW = Calendar.getInstance();
            calW.setTime(w.dateJdbcTimestampField);
            if (cal.get(Calendar.YEAR) != calW.get(Calendar.YEAR))
            {
                return false;
            }
            if (cal.get(Calendar.MONTH) != calW.get(Calendar.MONTH))
            {
                return false;
            }
            if (cal.get(Calendar.DATE) != calW.get(Calendar.DATE))
            {
                return false;
            }
            if (cal.get(Calendar.HOUR) != calW.get(Calendar.HOUR))
            {
                return false;
            }
            if (cal.get(Calendar.MINUTE) != calW.get(Calendar.MINUTE))
            {
                return false;
            }
            if (cal.get(Calendar.SECOND) != calW.get(Calendar.SECOND))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a string representation for this object.
     * All of the field values are included in the string for debugging purposes.
     *
     * @return a string representation for this object.
     */
    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  dateField = ").append(dateField);
        if (dateField != null)
        {
            s.append(" (").append(dateField.getTime()).append(')');
        }
        s.append('\n');

        s.append("  sqlDateField = ").append(sqlDateField);
        if (sqlDateField != null)
        {
            s.append(" (").append(sqlDateField.getTime()).append(')');
        }
        s.append('\n');

        s.append("  sqlTimeField = ").append(sqlTimeField);
        if (sqlTimeField != null)
        {
            s.append(" (").append(sqlTimeField.getTime()).append(')');
        }
        s.append('\n');

        s.append("  sqlTimestampField = ").append(sqlTimestampField);
        if (sqlTimestampField != null)
        {
            s.append(" (").append(sqlTimestampField.getTime()).append(')');
        }
        s.append('\n');

        s.append("  dateJdbcTimestampField = ").append(dateJdbcTimestampField);
        if (dateJdbcTimestampField != null)
        {
            s.append(" (").append(dateJdbcTimestampField.getTime()).append(')');
        }
        s.append('\n');

        return s.toString();
    }
}