/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.javatime;

import java.time.MonthDay;

/**
 * Sample using MonthDay.
 */
public class JavaTimeSample5
{
    private long id;

    private MonthDay monthDay1;
    private MonthDay monthDay2;

    public JavaTimeSample5(long id, MonthDay md1, MonthDay md2)
    {
        this.id = id;
        this.monthDay1 = md1;
        this.monthDay2 = md2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public MonthDay getMonthDay1()
    {
        return monthDay1;
    }

    public void setMonthDay1(MonthDay md)
    {
        this.monthDay1 = md;
    }

    public MonthDay getMonthDay2()
    {
        return monthDay2;
    }

    public void setMonthDay2(MonthDay md)
    {
        this.monthDay2 = md;
    }
}