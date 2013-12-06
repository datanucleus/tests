/**********************************************************************
Copyright (c) 2004 Ralf Ulrich and others. All rights reserved.
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
package org.jpox.samples.models.leftright;

import java.util.ArrayList;
import java.util.List;

/**
 * Subclass of object on left-hand-side of relation.
 * Has a List<LeftBase>.
 * @version $Revision: 1.1 $
 */
public class LeftSub extends LeftBase
{
    List members = new ArrayList();

    public LeftSub(int id, LeftBase[] members)
    {
        super(id);
        for (int x = 0; x < members.length; x++)
        {
            this.members.add(members[x]);
        }
    }

    public final List getMembers()
    {
        return members;
    }
}