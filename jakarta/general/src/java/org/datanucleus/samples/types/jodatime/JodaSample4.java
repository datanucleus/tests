/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.jodatime;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import org.datanucleus.api.jakarta.annotations.JdbcType;
import org.joda.time.Duration;

/**
 * Sample using Duration.
 */
@Entity
public class JodaSample4
{
    @Id
    private long id;

    @Basic
    private Duration duration1;

    @Basic
    @JdbcType("VARCHAR")
    private Duration duration2;

    public JodaSample4(long id, Duration d1, Duration d2)
    {
        this.id = id;
        this.duration1 = d1;
        this.duration2 = d2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Duration getDuration1()
    {
        return duration1;
    }

    public void setDuration1(Duration d)
    {
        this.duration1 = d;
    }

    public Duration getDuration2()
    {
        return duration2;
    }

    public void setDuration2(Duration d)
    {
        this.duration2 = d;
    }
}