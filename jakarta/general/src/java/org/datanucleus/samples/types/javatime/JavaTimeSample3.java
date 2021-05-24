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
package org.datanucleus.samples.types.javatime;

import java.time.LocalTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import org.datanucleus.api.jakarta.annotations.JdbcType;

/**
 * Sample using LocalTime.
 */
@Entity
public class JavaTimeSample3
{
    @Id
    private long id;

    @Basic
    private LocalTime localTime1;

    @Basic
    @JdbcType("VARCHAR")
    private LocalTime localTime2;

    public JavaTimeSample3(long id, LocalTime lt1, LocalTime lt2)
    {
        this.id = id;
        this.localTime1 = lt1;
        this.localTime2 = lt2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public LocalTime getLocalTime1()
    {
        return localTime1;
    }

    public void setLocalTime1(LocalTime lt)
    {
        this.localTime1 = lt;
    }

    public LocalTime getLocalTime2()
    {
        return localTime2;
    }

    public void setLocalTime2(LocalTime lt)
    {
        this.localTime2 = lt;
    }
}