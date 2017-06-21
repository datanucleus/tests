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

import java.time.Year;

/**
 * Sample using Year.
 */
public class JavaTimeSample7
{
    private long id;

    private Year year1;

    public JavaTimeSample7(long id, Year yr1)
    {
        this.id = id;
        this.year1 = yr1;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Year getYear1()
    {
        return year1;
    }

    public void setYear1(Year yr)
    {
        this.year1 = yr;
    }
}