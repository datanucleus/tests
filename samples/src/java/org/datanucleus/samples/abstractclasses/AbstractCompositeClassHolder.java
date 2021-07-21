/**********************************************************************
Copyright (c) 2003 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.abstractclasses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Container of abstract objects with composite identity. Has the following :-
 * <ul>
 * <li>a field containing an abstract object (1-1 relation)</li>
 * <li>a second field containing an abstract object (1-1 relation)</li>
 * <li>a field containing a List of abstract objects (1-N relation)</li>
 * <li>a field containing a Set of abstract objects (1-N relation)</li>
 * </ul>
 *
 * @version $Revision: 1.1 $ 
 */
public class AbstractCompositeClassHolder
{
    private int id; // Identity

    protected AbstractCompositeBase abstract1;
    protected AbstractCompositeBase abstract2;

    protected List abstractList1 = new ArrayList();
    protected Set abstractSet1 = new HashSet();

    public AbstractCompositeClassHolder(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setAbstract1(AbstractCompositeBase abs)
    {
        abstract1 = abs;
    }

    public AbstractCompositeBase getAbstract1()
    {
        return abstract1;
    }

    public void setAbstract2(AbstractCompositeBase abs)
    {
        abstract2 = abs;
    }

    public AbstractCompositeBase getAbstract2()
    {
        return abstract2;
    }

    public List getAbstractList1()
    {
        return abstractList1;
    }

    public Set getAbstractSet1()
    {
        return abstractSet1;
    }
}