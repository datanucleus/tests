/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.annotations.one_many.unidir_2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * User group, consisting of a list of members.
 */
@Entity
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("UserGroup")
public class UserGroup
{
    @Id
    private long id;

    @Basic
    private String name;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable
    @OrderBy("name ASC")
    private List<GroupMember> members = new ArrayList<GroupMember>();

    public UserGroup(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public List getMembers()
    {
        return members;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static class Key implements Serializable
    {
        public long id;
        public String name;

        public Key()
        {
        }

        public Key(String value) 
        {
            StringTokenizer token = new StringTokenizer (value, "::");
            //className
            token.nextToken ();
            //id
            this.id = new Long(token.nextToken()).longValue();
            //name
            this.name = token.nextToken();
        }

        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            if (!(obj instanceof Key))
            {
                return false;
            }
            Key c = (Key)obj;

            return id == c.id && name.equals(c.name);
        }

        public int hashCode ()
        {
            return (int) this.id ^ this.name.hashCode();
        }

        public String toString ()
        {
            return this.getClass().getName() + "::"  + this.id + "::" + this.name;
        }
    }
}