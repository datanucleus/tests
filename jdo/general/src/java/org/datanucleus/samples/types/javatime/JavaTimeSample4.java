/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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

import java.time.Instant;

/**
 * Sample using LocalDateTime.
 */
public class JavaTimeSample4
{
    private long id;

    private Instant instant1;
    private Instant instant2;

    public JavaTimeSample4(long id, Instant dt1, Instant dt2)
    {
        this.id = id;
        this.instant1 = dt1;
        this.instant2 = dt2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Instant getInstant1()
    {
        return instant1;
    }

    public void setInstant1(Instant dt)
    {
        this.instant1 = dt;
    }

    public Instant getInstant2()
    {
        return instant2;
    }

    public void setInstant2(Instant dt)
    {
        this.instant2 = dt;
    }
}