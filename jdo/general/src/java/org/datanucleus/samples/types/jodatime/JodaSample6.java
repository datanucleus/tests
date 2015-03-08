/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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

import org.joda.time.LocalDateTime;

/**
 * Sample using LocalDate.
 */
public class JodaSample6
{
    private long id;

    private LocalDateTime localDateTime1;
    private LocalDateTime localDateTime2;

    public JodaSample6(long id, LocalDateTime ldt1, LocalDateTime ldt2)
    {
        this.id = id;
        this.localDateTime1 = ldt1;
        this.localDateTime2 = ldt2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public LocalDateTime getLocalDateTime1()
    {
        return localDateTime1;
    }

    public void setLocalDate1(LocalDateTime ldt)
    {
        this.localDateTime1 = ldt;
    }

    public LocalDateTime getLocalDateTime2()
    {
        return localDateTime2;
    }

    public void setLocalDate2(LocalDateTime ldt)
    {
        this.localDateTime2 = ldt;
    }
}