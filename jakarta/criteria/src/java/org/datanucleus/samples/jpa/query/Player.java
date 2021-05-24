/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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
***********************************************************************/
package org.datanucleus.samples.jpa.query;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;

@Entity
public class Player extends Person
{
    Date startDate;

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    Team team;

    LocalDateTime dateTime;

    public Player(long id, String first, String last)
    {
        super(id, first, last);
    }

    public void setStartDate(Date date)
    {
        this.startDate = date;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public LocalDateTime getDateTime()
    {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dt)
    {
        this.dateTime = dt;
    }

    public void setTeam(Team t)
    {
        this.team = t;
    }

    public Team getTeam()
    {
        return team;
    }
}
