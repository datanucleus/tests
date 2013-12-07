/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import java.util.Calendar;


public class DateWidget extends Widget
{
    private java.util.Date dateField;
    private java.sql.Date sqlDateField;
    private java.sql.Timestamp sqlTimestampField;
    /** a java.util.Date mapped using jdbc-type TIMESTAMP **/
    private java.util.Date dateJdbcTimestampField;


    public DateWidget()
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


    public java.sql.Timestamp getSQLTimestampField()
    {
        return sqlTimestampField;
    }


    /**
     * Fills all of the object's fields with random data values.  Any non-
     * primitive fields will also be assigned <code>null</code> on a random
     * basis.
     */

    public void fillRandom()
    {
        super.fillRandom();

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

        if (!(obj instanceof DateWidget) || !super.compareTo(obj))
            return false;

        DateWidget w = (DateWidget)obj;

        if (dateField == null) { if (w.dateField != null) return false; }
        else if (!dateField.equals(w.dateField)) return false;

        if (sqlDateField == null) { if (w.sqlDateField != null) return false; }
        else if (!sqlDateField.equals(w.sqlDateField)) return false;

        if (sqlTimestampField == null) { if (w.sqlTimestampField != null) return false; }
        else if (!sqlTimestampField.equals(w.sqlTimestampField)) return false;

        /* we compare only YYYYMMDD HH:MM:ss */
        if (dateJdbcTimestampField == null) { if (w.dateJdbcTimestampField != null) return false; }
        else
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateJdbcTimestampField);
            Calendar calW = Calendar.getInstance();
            calW.setTime(w.dateJdbcTimestampField);
            if( cal.get(Calendar.YEAR) != calW.get(Calendar.YEAR) )
            {
                return false;
            }
            if( cal.get(Calendar.MONTH) != calW.get(Calendar.MONTH) )
            {
                return false;
            }
            if( cal.get(Calendar.DATE) != calW.get(Calendar.DATE) )
            {
                return false;
            }
            if( cal.get(Calendar.HOUR) != calW.get(Calendar.HOUR) )
            {
                return false;
            }
            if( cal.get(Calendar.MINUTE) != calW.get(Calendar.MINUTE) )
            {
                return false;
            }
            if( cal.get(Calendar.SECOND) != calW.get(Calendar.SECOND) )
            {
                return false;
            }
        }
        
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

        s.append("  dateField = ").append(dateField);
        if (dateField != null)
            s.append(" (").append(dateField.getTime()).append(')');
        s.append('\n');

        s.append("  sqlDateField = ").append(sqlDateField);
        if (sqlDateField != null)
            s.append(" (").append(sqlDateField.getTime()).append(')');
        s.append('\n');

        s.append("  sqlTimestampField = ").append(sqlTimestampField);
        if (sqlTimestampField != null)
            s.append(" (").append(sqlTimestampField.getTime()).append(')');
        s.append('\n');

        s.append("  dateJdbcTimestampField = ").append(dateJdbcTimestampField);
        if (dateJdbcTimestampField != null)
            s.append(" (").append(dateJdbcTimestampField.getTime()).append(')');
        s.append('\n');

        return s.toString();
    }
}
