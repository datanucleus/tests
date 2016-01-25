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

import java.time.YearMonth;

/**
 * Sample using YearMonth.
 */
public class JavaTimeSample6
{
    private long id;

    private YearMonth yearMonth1;
    private YearMonth yearMonth2;

    public JavaTimeSample6(long id, YearMonth ym1, YearMonth ym2)
    {
        this.id = id;
        this.yearMonth1 = ym1;
        this.yearMonth2 = ym2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public YearMonth getYearMonth1()
    {
        return yearMonth1;
    }

    public void setYearMonth1(YearMonth ym)
    {
        this.yearMonth1 = ym;
    }

    public YearMonth getYearMonth2()
    {
        return yearMonth2;
    }

    public void setYearMonth2(YearMonth ym)
    {
        this.yearMonth2 = ym;
    }
}