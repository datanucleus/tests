/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests.directory.dn_nested;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(table = "ou=Groups,dc=example,dc=com", schema = "top,groupOfNames", detachable = "true")
public class Group extends GroupMember
{
    @Persistent(defaultFetchGroup = "true", recursionDepth = -1, column="member")
    @Extension(vendorName = "datanucleus", key = "empty-value", value = "uid=admin,ou=system")
    protected Set<GroupMember> members = new HashSet<GroupMember>();

    public Group(String id)
    {
        super(id);
    }

    public Set<GroupMember> getMembers()
    {
        return members;
    }

    public void setMembers(Set<GroupMember> members)
    {
        this.members = members;
    }

    @Override
    public String toString()
    {
        return "Group [id=" + id + "]";
    }


}
