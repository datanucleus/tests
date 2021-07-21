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
package org.datanucleus.samples.dependentfield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that has fields of all types as a test for dependent, dependent-element, dependent-key, dependent-value.
 */
public class DependentHolder
{
    private int id; // PK when using app id
    String description;

    DependentElement element;

    DepInterface intf;

    Set setDependent1;
    Set setDependent2;
    Set setNonDependent1;
    Set setNonDependent2;

    List listDependent1;
    List listDependent2;
    List listNonDependent1;
    List listNonDependent2;

    Map mapDependentValues1;
    Map mapDependentValues2;
    Map mapNonDependentValues1;
    Map mapNonDependentValues2;
    Map mapDependentKeys1;
    Map mapDependentKeys2;
    Map mapNonDependentKeys1;
    Map mapNonDependentKeys2;
    Map mapDependent1;
    Map mapDependent2;
    Map mapNonDependent1;
    Map mapNonDependent2;

    DependentElement[] arrayDependent1;
    /*DependentElement[] arrayDependent2;*/
    DependentElement[] arrayNonDependent1;
    /*DependentElement[] arrayNonDependent2;*/

    public DependentHolder()
    {
        super();
    }

    public DependentHolder(int id, String description)
    {
        super();
        this.id = id;
        this.description = description;
    }

    public final String getDescription()
    {
        return description;
    }

    public final void setDescription(String description)
    {
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DependentElement getElement()
    {
        return element;
    }

    public void setElement(DependentElement element)
    {
        this.element = element;
    }

    public void setIntf(DepInterface intf)
    {
        this.intf = intf;
    }

    public DepInterface getIntf()
    {
        return intf;
    }

    public Map getMapDependent1()
    {
        if (mapDependent1 == null)
        {
            mapDependent1 = new HashMap();
        }
        return mapDependent1;
    }

    public void setMapDependent1(Map map)
    {
        this.mapDependent1 = map;
    }

    public Map getMapDependent2()
    {
        if (mapDependent2 == null)
        {
            mapDependent2 = new HashMap();
        }
        return mapDependent2;
    }

    public void setMapDependent2(Map map)
    {
        this.mapDependent2 = map;
    }

    public Map getMapDependentKeys1()
    {
        if (mapDependentKeys1 == null)
        {
            mapDependentKeys1 = new HashMap();
        }
        return mapDependentKeys1;
    }

    public void setMapDependentKeys1(Map map)
    {
        this.mapDependentKeys1 = map;
    }

    public Map getMapDependentKeys2()
    {
        if (mapDependentKeys2 == null)
        {
            mapDependentKeys2 = new HashMap();
        }
        return mapDependentKeys2;
    }

    public void setMapDependentKeys2(Map map)
    {
        this.mapDependentKeys2 = map;
    }

    public Map getMapDependentValues1()
    {
        if (mapDependentValues1 == null)
        {
            mapDependentValues1 = new HashMap();
        }
        return mapDependentValues1;
    }

    public void setMapDependentValues1(Map map)
    {
        this.mapDependentValues1 = map;
    }

    public Map getMapDependentValues2()
    {
        if (mapDependentValues2 == null)
        {
            mapDependentValues2 = new HashMap();
        }
        return mapDependentValues2;
    }

    public void setMapDependentValues2(Map map)
    {
        this.mapDependentValues2 = map;
    }

    public Map getMapNonDependent1()
    {
        if (mapNonDependent1 == null)
        {
            mapNonDependent1 = new HashMap();
        }
        return mapNonDependent1;
    }

    public void setMapNonDependent1(Map map)
    {
        this.mapNonDependent1 = map;
    }

    public Map getMapNonDependent2()
    {
        if (mapNonDependent2 == null)
        {
            mapNonDependent2 = new HashMap();
        }
        return mapNonDependent2;
    }

    public void setMapNonDependent2(Map map)
    {
        this.mapNonDependent2 = map;
    }

    public Map getMapNonDependentKeys1()
    {
        if (mapNonDependentKeys1 == null)
        {
            mapNonDependentKeys1 = new HashMap();
        }
        return mapNonDependentKeys1;
    }

    public void setMapNonDependentKeys1(Map map)
    {
        this.mapNonDependentKeys1 = map;
    }

    public Map getMapNonDependentKeys2()
    {
        if (mapNonDependentKeys2 == null)
        {
            mapNonDependentKeys2 = new HashMap();
        }
        return mapNonDependentKeys2;
    }

    public void setMapNonDependentKeys2(Map map)
    {
        this.mapNonDependentKeys2 = map;
    }

    public Map getMapNonDependentValues1()
    {
        if (mapNonDependentValues1 == null)
        {
            mapNonDependentValues1 = new HashMap();
        }
        return mapNonDependentValues1;
    }

    public void setMapNonDependentValues1(Map map)
    {
        this.mapNonDependentValues1 = map;
    }

    public Map getMapNonDependentValues2()
    {
        if (mapNonDependentValues2 == null)
        {
            mapNonDependentValues2 = new HashMap();
        }
        return mapNonDependentValues2;
    }

    public void setMapNonDependentValues2(Map map)
    {
        this.mapNonDependentValues2 = map;
    }

    public Set getSetDependent1()
    {
        if (setDependent1 == null)
        {
            setDependent1 = new HashSet();
        }
        return setDependent1;
    }

    public void setSetDependent1(Set set)
    {
        this.setDependent1 = set;
    }

    public Set getSetDependent2()
    {
        if (setDependent2 == null)
        {
            setDependent2 = new HashSet();
        }
        return setDependent2;
    }

    public void setSetDependent2(Set set)
    {
        this.setDependent2 = set;
    }

    public Set getSetNonDependent1()
    {
        if (setNonDependent1 == null)
        {
            setNonDependent1 = new HashSet();
        }
        return setNonDependent1;
    }

    public void setSetNonDependent1(Set set)
    {
        this.setNonDependent1 = set;
    }

    public Set getSetNonDependent2()
    {
        if (setNonDependent2 == null)
        {
            setNonDependent2 = new HashSet();
        }
        return setNonDependent2;
    }

    public void setSetNonDependent2(Set set)
    {
        this.setNonDependent2 = set;
    }

    public List getListDependent1()
    {
        if (listDependent1 == null)
        {
            listDependent1 = new ArrayList();
        }
        return listDependent1;
    }

    public void setListDependent1(List list)
    {
        this.listDependent1 = list;
    }

    public List getListDependent2()
    {
        if (listDependent2 == null)
        {
            listDependent2 = new ArrayList();
        }
        return listDependent2;
    }

    public void setListDependent2(List list)
    {
        this.listDependent2 = list;
    }

    public List getListNonDependent1()
    {
        if (listNonDependent1 == null)
        {
            listNonDependent1 = new ArrayList();
        }
        return listNonDependent1;
    }

    public void setListNonDependent1(List list)
    {
        this.listNonDependent1 = list;
    }

    public List getListNonDependent2()
    {
        if (listNonDependent2 == null)
        {
            listNonDependent2 = new ArrayList();
        }
        return listNonDependent2;
    }

    public void setListNonDependent2(List list)
    {
        this.listNonDependent2 = list;
    }

    public DependentElement[] getArrayDependent1()
    {
        return arrayDependent1;
    }

    public void setArrayDependent1(DependentElement[] array)
    {
        arrayDependent1 = array;
    }

    /*public DependentElement[] getArrayDependent2()
    {
        return arrayDependent2;
    }

    public void setArrayDependent2(DependentElement[] array)
    {
        arrayDependent2 = array;
    }*/

    public DependentElement[] getArrayNonDependent1()
    {
        return arrayNonDependent1;
    }

    public void setArrayNonDependent1(DependentElement[] array)
    {
        arrayNonDependent1 = array;
    }

    /*public DependentElement[] getArrayNonDependent2()
    {
        return arrayNonDependent2;
    }

    public void setArrayNonDependent2(DependentElement[] array)
    {
        arrayNonDependent2 = array;
    }*/

    public boolean equals(Object arg0)
    {
        if (arg0 == null || !(arg0 instanceof DependentHolder))
        {
            return false;
        }
        DependentHolder df = (DependentHolder) arg0;
        return this.id == df.id;
    }
}