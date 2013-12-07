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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Team
{
    @Id
    long id;

    String name;

    @OneToOne(cascade={CascadeType.ALL})
    Manager manager;

    @OneToMany(cascade={CascadeType.ALL})
    Set<Player> players = new HashSet<Player>();

    URL website;

    public Team(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setManager(Manager mgr)
    {
        this.manager = mgr;
    }
    
    public Manager getManager()
    {
        return manager;
    }

    public void setWebsite(URL url)
    {
        this.website = url;
    }
    
    public URL getWebsite()
    {
        return website;
    }

    public void addPlayer(Player pl)
    {
        this.players.add(pl);
    }

    public Set<Player> getPlayers()
    {
        return this.players;
    }
}
