/**********************************************************************
Copyright (c) 21-Apr-2004 Andy Jefferson and others.
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
package org.datanucleus.samples.types.hashtable;

/**
 * An item to store in an Hashtable container. Inheritance of elements. 
 *
 * @version $Revision: 1.1 $  
 **/
public class Hashtable2ItemChild extends Hashtable2Item
{
    protected String code=null;

    protected Hashtable2ItemChild()
    {
    }

    public Hashtable2ItemChild(String name, double value, int status, String code)
    {
        super(name,value,status);
        this.code = code;
    }

    public boolean equals(Object arg0)
    {
        if (arg0 == null || !(arg0 instanceof Hashtable2ItemChild))
        {
            return false;
        }
        Hashtable2ItemChild item = (Hashtable2ItemChild)arg0;
        if (Double.compare(item.value,value)==0 && item.status == status &&
            (name == null ? item.name == null : name.equals(item.name)) &&
            (code == null ? item.code == null : code.equals(item.code)) )
        {
            return true;
        }
        return false;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String toString()
    {
        return getClass().getName() + " " + getName() + " - value=" + getValue() + " [status=" + getStatus() + "] CODE=" + code;
    }
}