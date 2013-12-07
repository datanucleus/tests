/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.javaxtime;

import javax.time.calendar.LocalDateTime;

/**
 * Sample using LocalDateTime.
 */
public class JavaxtimeSample1
{
    private long id;

    private LocalDateTime dateTime1;
    private LocalDateTime dateTime2;

    public JavaxtimeSample1(long id, LocalDateTime dt1, LocalDateTime dt2)
    {
        this.id = id;
        this.dateTime1 = dt1;
        this.dateTime2 = dt2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public LocalDateTime getDateTime1()
    {
        return dateTime1;
    }

    public void setDateTime1(LocalDateTime dt)
    {
        this.dateTime1 = dt;
    }

    public LocalDateTime getDateTime2()
    {
        return dateTime2;
    }

    public void setDateTime2(LocalDateTime dt)
    {
        this.dateTime2 = dt;
    }
}