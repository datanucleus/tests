/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributions
    ...
***********************************************************************/
package org.jpox.samples.valuegeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Sample container of elements using "identity" strategy.
 * 
 * @version $Revision: 1.1 $
 */
public class IdentityGeneratorItemContainer
{
    Collection members = new ArrayList();

    public IdentityGeneratorItemContainer(IdentityGeneratorItem[] members)
    {
        for (int i = 0; i < members.length; i++)
        {
            this.members.add(members[i]);
        }
    }

    public long getNumberOfMembers()
    {
        return members.size();
    }

    public boolean containsMember(IdentityGeneratorItem item)
    {
        return members.contains(item);
    }

    public void clear()
    {
        for (Iterator iter = members.iterator(); iter.hasNext();)
        {
            iter.next();
            iter.remove();
        }
    }
}