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

import org.joda.time.Interval;

/**
 * Sample using Interval.
 */
public class JodaSample5
{
    private long id;

    private Interval interval1;
    private Interval interval2;

    public JodaSample5(long id, Interval d1, Interval d2)
    {
        this.id = id;
        this.interval1 = d1;
        this.interval2 = d2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Interval getInterval1()
    {
        return interval1;
    }

    public void setInterval1(Interval d)
    {
        this.interval1 = d;
    }

    public Interval getInterval2()
    {
        return interval2;
    }

    public void setInterval2(Interval d)
    {
        this.interval2 = d;
    }
}