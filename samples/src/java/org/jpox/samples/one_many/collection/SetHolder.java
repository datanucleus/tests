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
package org.jpox.samples.one_many.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * Sample class with various Set fields, testing all combinations of the types of Set field possible.
 */
public class SetHolder
{
    private long id; // Used for app identity

    private String name;

    private Set joinSetPC; // Using join table, with PC elements
    private Set<String> joinSetNonPC1; // Using join table, with String elements
    private Set joinSetNonPC2; // Using join table, with Date elements
    private Set joinSetNonPC3; // Using join table, with String elements
    private Set<PCJoinElement> joinSetPCSerial; // Using join table with PC elements serialised
    private Set joinSetPCShared1; // Using join table (shared), with PC elements
    private Set joinSetPCShared2; // Using join table (shared), with PC elements

    private Set fkSetPC; // Using FK, with PC elements
    private Set fkSetPC2; // Using FK, with PC elements
    private Set fkSetPCShared1; // Using FK (shared), with PC elements
    private Set fkSetPCShared2; // Using FK (shared), with PC elements

    private Set setNonPCSerial1; // String elements, serialised Set
    private Set setNonPCSerial2; // Date elements, serialised Set

    private Set setNonPC1; // String elements, no join table - so serialised
    private Set setNonPC2; // Date elements, no join table - so serialised

    private Set setPCEmbedded; // PC elements (embedded), no join table - so serialised

    public SetHolder()
    {
    }

    public SetHolder(long id)
    {
        this.id = id;
    }

    public SetHolder(String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Set getJoinSetPC()
    {
        if (joinSetPC == null)
        {
            joinSetPC = new HashSet<>();
        }
        return joinSetPC;
    }

    public Set<String> getJoinSetNonPC1()
    {
        if (joinSetNonPC1 == null)
        {
            joinSetNonPC1 = new HashSet<>();
        }
        return joinSetNonPC1;
    }

    public Set getJoinSetNonPC2()
    {
        if (joinSetNonPC2 == null)
        {
            joinSetNonPC2 = new HashSet<>();
        }
        return joinSetNonPC2;
    }

    public Set getJoinSetNonPC3()
    {
        if (joinSetNonPC3 == null)
        {
            joinSetNonPC3 = new HashSet<>();
        }
        return joinSetNonPC3;
    }

    public Set<PCJoinElement> getJoinSetPCSerial()
    {
        if (joinSetPCSerial == null)
        {
            joinSetPCSerial = new HashSet<>();
        }
        return joinSetPCSerial;
    }

    public Set getJoinSetPCShared1()
    {
        if (joinSetPCShared1 == null)
        {
            joinSetPCShared1 = new HashSet<>();
        }
        return joinSetPCShared1;
    }

    public Set getJoinSetPCShared2()
    {
        if (joinSetPCShared2 == null)
        {
            joinSetPCShared2 = new HashSet<>();
        }
        return joinSetPCShared2;
    }

    public Set getFkSetPC()
    {
        if (fkSetPC == null)
        {
            fkSetPC = new HashSet<>();
        }
        return fkSetPC;
    }

    public Set getFkSetPC2()
    {
        if (fkSetPC2 == null)
        {
            fkSetPC2 = new HashSet<>();
        }
        return fkSetPC2;
    }

    public Set getFkSetPCShared1()
    {
        if (fkSetPCShared1 == null)
        {
            fkSetPCShared1 = new HashSet<>();
        }
        return fkSetPCShared1;
    }

    public Set getFkSetPCShared2()
    {
        if (fkSetPCShared2 == null)
        {
            fkSetPCShared2 = new HashSet<>();
        }
        return fkSetPCShared2;
    }

    public Set getSetNonPCSerial1()
    {
        if (setNonPCSerial1 == null)
        {
            setNonPCSerial1 = new HashSet<>();
        }
        return setNonPCSerial1;
    }

    public Set getSetNonPCSerial2()
    {
        if (setNonPCSerial2 == null)
        {
            setNonPCSerial2 = new HashSet<>();
        }
        return setNonPCSerial2;
    }

    public Set getSetNonPC1()
    {
        if (setNonPC1 == null)
        {
            setNonPC1 = new HashSet<>();
        }
        return setNonPC1;
    }

    public Set getSetNonPC2()
    {
        if (setNonPC2 == null)
        {
            setNonPC2 = new HashSet<>();
        }
        return setNonPC2;
    }

    public Set getSetPCEmbedded()
    {
        if (setPCEmbedded == null)
        {
            setPCEmbedded = new HashSet<>();
        }
        return setPCEmbedded;
    }
}