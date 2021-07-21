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
package org.datanucleus.samples.types.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Sample class with a Calendar.
 * Has two fields so they can be stored in different ways as required.
 */
public class CalendarHolder
{
    long id;
    Calendar cal1 = new GregorianCalendar();
    Calendar cal2 = new GregorianCalendar();

    public CalendarHolder()
    {
    }

    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }

    public Calendar getCal1()
    {
        return cal1;
    }

    public Calendar getCal2()
    {
        return cal2;
    }

    public void setCal1(Calendar cal)
    {
        this.cal1 = cal;
    }

    public void setCal1Time(long millisecs)
    {
        cal1.setTimeInMillis(millisecs);
    }

    public void setCal1TimeZone(TimeZone zone)
    {
        cal1.setTimeZone(zone);
    }

    public long getCal1TimeInMillisecs()
    {
        return cal1.getTimeInMillis();
    }

    public TimeZone getCal1TimeZone()
    {
        return cal1.getTimeZone();
    }

    public void setCal2(Calendar cal)
    {
        this.cal2 = cal;
    }

    public void setCal2Time(long millisecs)
    {
        cal2.setTimeInMillis(millisecs);
    }

    public void setCal2TimeZone(TimeZone zone)
    {
        cal2.setTimeZone(zone);
    }

    public long getCal2TimeInMillisecs()
    {
        return cal2.getTimeInMillis();
    }

    public TimeZone getCal2TimeZone()
    {
        return cal2.getTimeZone();
    }
}