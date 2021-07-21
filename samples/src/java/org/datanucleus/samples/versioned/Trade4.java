/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.versioned;

import java.util.Date;

/**
 * A trade in a stock market (versioned). Used to test optimistic transactions.
 * @version $Revision: 1.1 $
 */
public class Trade4
{
    private long id; // Can be used for PK
    private long version; // Used for version (when using "version field")
    private String person;
    private double value;
    private Date date;

    public Trade4(String person, double value, Date date)
    {
        this.person = person;
        this.value = value;
        this.date = date;
    }

    public void setPerson(String pers)
    {
        this.person = pers;
    }

    public long getId()
    {
        return id;
    }

    public long getVersion()
    {
        return version;
    }

    public String getPerson()
    {
        return person;
    }

    public double getValue()
    {
        return value;
    }

    public Date getDate()
    {
        return date;
    }
}