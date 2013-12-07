/**********************************************************************
Copyright (c) 2008 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.directory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Group of Users.
 */
public class Group implements Serializable
{
    String name;
    protected Set<Person> users = new HashSet();
    protected Set<String> roles = new HashSet();
    protected Map<String,String> roleMap = new HashMap();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set getUsers()
    {
        return users;
    }

    public void addUser(Person p)
    {
        this.users.add(p);
    }

    public Set getRoles()
    {
        return roles;
    }

    public void addRole(String str)
    {
        this.roles.add(str);
    }

    public Map getRoleMap()
    {
        return roleMap;
    }

    public void addRoleMapEntry(String key, String val)
    {
        roleMap.put(key, val);
    }
}