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
package org.datanucleus.samples.jdo.query;

import javax.jdo.annotations.*;

@PersistenceCapable
public class Player
{
    @PrimaryKey
    long id;

    String firstName;

    String lastName;

    Team team;

    public Player(long id, String first, String last)
    {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
    }

    public long getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
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
