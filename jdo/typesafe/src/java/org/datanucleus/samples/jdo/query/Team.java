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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

@PersistenceCapable
@Version(strategy=VersionStrategy.VERSION_NUMBER)
public class Team
{
    @PrimaryKey
    long id;

    String name;

    Manager manager;

    Set<Player> players = new HashSet<Player>();

    @Persistent
    String[] nicknames;

    @Persistent
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

    public void addNickname(String name)
    {
        if (nicknames == null)
        {
            nicknames = new String[] {name};
            return;
        }
        String[] names = new String[nicknames.length+1];
        for (int i=0;i<nicknames.length;i++)
        {
            names[i] = nicknames[i];
        }
        names[names.length-1] = name;
        nicknames = names;
    }
}