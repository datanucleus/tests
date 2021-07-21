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
 * Container of abstract objects with simple identity. Has the following :-
 * <ul>
 * <li>a field containing an abstract object (1-1 relation)</li>
 * <li>a second field containing an abstract object (1-1 relation)</li>
 * <li>a field containing a List of abstract objects (1-N relation)</li>
 * <li>a field containing a Set of abstract objects (1-N relation)</li>
 * </ul>
 *
 * @version $Revision: 1.1 $ 
 */
public class AbstractSimpleClassHolder
{
    private int id;

    protected AbstractSimpleBase abstract1;
    protected AbstractSimpleBase abstract2;

    protected List abstractList1 = new ArrayList();
    protected Set abstractSet1 = new HashSet();

    public AbstractSimpleClassHolder(int id)
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

    public void setAbstract1(AbstractSimpleBase abs)
    {
        abstract1 = abs;
    }

    public AbstractSimpleBase getAbstract1()
    {
        return abstract1;
    }

    public void setAbstract2(AbstractSimpleBase abs)
    {
        abstract2 = abs;
    }

    public AbstractSimpleBase getAbstract2()
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