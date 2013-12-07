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
package org.datanucleus.samples.annotations.versioned;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

/**
 * A trade in a stock market (versioned). Used to test optimistic transactions.
 */
@PersistenceCapable
@Version(strategy=VersionStrategy.VERSION_NUMBER, column="TRADE_VERSION")
public class Trade1
{
    @PrimaryKey
    private long id; // Can be used for PK

    private String person;
    private double value;
    private Date date;

    public Trade1(String person, double value, Date date)
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