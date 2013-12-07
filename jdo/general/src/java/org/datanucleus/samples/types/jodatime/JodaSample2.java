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

import org.joda.time.LocalDate;

/**
 * Sample using LocalDate.
 */
public class JodaSample2
{
    private long id;

    private LocalDate localDate1;
    private LocalDate localDate2;

    public JodaSample2(long id, LocalDate ld1, LocalDate ld2)
    {
        this.id = id;
        this.localDate1 = ld1;
        this.localDate2 = ld2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public LocalDate getLocalDate1()
    {
        return localDate1;
    }

    public void setLocalDate1(LocalDate ld)
    {
        this.localDate1 = ld;
    }

    public LocalDate getLocalDate2()
    {
        return localDate2;
    }

    public void setLocalDate2(LocalDate ld)
    {
        this.localDate2 = ld;
    }
}