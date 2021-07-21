/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.one_many.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample class with various List fields, testing all combinations of the types of List field possible.
 * NOTE : This class only has List fields, and no others (except the app id).
 * 
 * @version $Revision: 1.7 $
 */
public class ListHolder
{
    private long id; // Used for app identity

    private List joinListPC; // Using join table, with PC elements
    private List joinListNonPC1; // Using join table, with String elements
    private List joinListNonPC2; // Using join table, with Date elements
    private List joinListPCSerial; // Using join table with PC elements serialised
    private List joinListPCShared1; // Using join table (shared), with PC elements
    private List joinListPCShared2; // Using join table (shared), with PC elements

    private List fkListPC; // Using FK, with PC elements
    private List fkListPC2; // Using FK, with PC elements
    private List fkListPCShared1; // Using FK (shared), with PC elements
    private List fkListPCShared2; // Using FK (shared), with PC elements
    private List fkListPCOrdered; // Using FK, with PC elements and ordered (not indexed)

    private List listNonPCSerial1; // String elements, serialised List
    private List listNonPCSerial2; // Date elements, serialised List

    private List listNonPC1; // String elements, no join table - so serialised
    private List listNonPC2; // Date elements, no join table - so serialised

    private List listPCEmbedded; // PC elements (embedded), no join table - so serialised

    public ListHolder()
    {
    }

    public ListHolder(int id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public List getJoinListPC()
    {
        if (joinListPC == null)
        {
            joinListPC = new ArrayList();
        }
        return joinListPC;
    }

    public List getJoinListNonPC1()
    {
        if (joinListNonPC1 == null)
        {
            joinListNonPC1 = new ArrayList();
        }
        return joinListNonPC1;
    }

    public List getJoinListNonPC2()
    {
        if (joinListNonPC2 == null)
        {
            joinListNonPC2 = new ArrayList();
        }
        return joinListNonPC2;
    }

    public List getJoinListPCSerial()
    {
        if (joinListPCSerial == null)
        {
            joinListPCSerial = new ArrayList();
        }
        return joinListPCSerial;
    }

    public List getJoinListPCShared1()
    {
        if (joinListPCShared1 == null)
        {
            joinListPCShared1 = new ArrayList();
        }
        return joinListPCShared1;
    }

    public List getJoinListPCShared2()
    {
        if (joinListPCShared2 == null)
        {
            joinListPCShared2 = new ArrayList();
        }
        return joinListPCShared2;
    }

    public List getFkListPC()
    {
        if (fkListPC == null)
        {
            fkListPC = new ArrayList();
        }
        return fkListPC;
    }

    public List getFkListPC2()
    {
        if (fkListPC2 == null)
        {
            fkListPC2 = new ArrayList();
        }
        return fkListPC2;
    }

    public List getFkListPCShared1()
    {
        if (fkListPCShared1 == null)
        {
            fkListPCShared1 = new ArrayList();
        }
        return fkListPCShared1;
    }

    public List getFkListPCShared2()
    {
        if (fkListPCShared2 == null)
        {
            fkListPCShared2 = new ArrayList();
        }
        return fkListPCShared2;
    }

    public List getFkListPCOrdered()
    {
        if (fkListPCOrdered == null)
        {
            fkListPCOrdered = new ArrayList();
        }
        return fkListPCOrdered;
    }

    public List getListNonPCSerial1()
    {
        if (listNonPCSerial1 == null)
        {
            listNonPCSerial1 = new ArrayList();
        }
        return listNonPCSerial1;
    }

    public List getListNonPCSerial2()
    {
        if (listNonPCSerial2 == null)
        {
            listNonPCSerial2 = new ArrayList();
        }
        return listNonPCSerial2;
    }

    public List getListNonPC1()
    {
        if (listNonPC1 == null)
        {
            listNonPC1 = new ArrayList();
        }
        return listNonPC1;
    }

    public List getListNonPC2()
    {
        if (listNonPC2 == null)
        {
            listNonPC2 = new ArrayList();
        }
        return listNonPC2;
    }

    public List getListPCEmbedded()
    {
        if (listPCEmbedded == null)
        {
            listPCEmbedded = new ArrayList();
        }
        return listPCEmbedded;
    }
}