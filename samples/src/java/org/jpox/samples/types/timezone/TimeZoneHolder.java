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
package org.jpox.samples.types.timezone;

import java.util.TimeZone;

/**
 * Holder of TimeZone type to use as a test for persisting basic information.
 */
public class TimeZoneHolder
{
    private TimeZone timeZoneField;

    public TimeZoneHolder()
    {
        super();
    }

    public TimeZone getTimeZoneField()
    {
        return timeZoneField;
    }

    public void setTimeZoneField(TimeZone zone)
    {
        this.timeZoneField = zone;
    }

    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read back correctly.
     *
     * @param obj the reference object with which to compare
     * @return <code>true</code> if this object is equal to the obj argument; <code>false</code> otherwise.
     */
    public boolean compareTo(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof TimeZoneHolder))
        {
            return false;
        }

        TimeZoneHolder w = (TimeZoneHolder)obj;

        if (timeZoneField == null) 
        { 
            if (w.timeZoneField != null) 
            {
                return false; 
            }
        }
        else if (!timeZoneField.toString().equals(w.timeZoneField.toString())) 
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

        s.append("  timeZoneField = ").append(timeZoneField);
        s.append('\n');

        return s.toString();
    }
}