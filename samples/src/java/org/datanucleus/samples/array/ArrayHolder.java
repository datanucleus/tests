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

Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.array;

import java.util.Locale;

import org.datanucleus.samples.interfaces.Shape;

/**
 * Holder of various array fields.
 *
 * @version $Revision: 1.2 $
 */
public class ArrayHolder
{
    long id;

    int[] intArray1;
    int[] intArray2;

    Long[] longObjArray1;
    Long[] longObjArray2;

    String[] stringArray1;
    String[] stringArray2;

    Locale[] localeArray1;
    Locale[] localeArray2;

    ArrayElement[] elementArray1; // Array of persistables
    ArrayElement[] elementArray2; // Array of persistables (in case there are 2 mappings available)
    ArrayElement[] elementArray3; // Array of persistables (in case there are 3 mappings available)

    Shape[] interfaceArray; // Array of interfaces
    Object[] objectArray; // Array of objects (references)

    public ArrayHolder()
    {
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public void setIntArray1(int[] arr)
    {
        intArray1 = arr;
    }

    public int[] getIntArray1()
    {
        return intArray1;
    }

    public void setIntArray2(int[] arr)
    {
        intArray2 = arr;
    }

    public int[] getIntArray2()
    {
        return intArray2;
    }

    public void setLongObjArray1(Long[] arr)
    {
        longObjArray1 = arr;
    }

    public Long[] getLongObjArray1()
    {
        return longObjArray1;
    }

    public void setLongObjArray2(Long[] arr)
    {
        longObjArray2 = arr;
    }

    public Long[] getLongObjArray2()
    {
        return longObjArray2;
    }

    public void setStringArray1(String[] arr)
    {
        stringArray1 = arr;
    }

    public String[] getStringArray1()
    {
        return stringArray1;
    }

    public void setStringArray2(String[] arr)
    {
        stringArray2 = arr;
    }

    public String[] getStringArray2()
    {
        return stringArray2;
    }

    public void setLocaleArray1(Locale[] arr)
    {
        localeArray1 = arr;
    }

    public Locale[] getLocaleArray1()
    {
        return localeArray1;
    }

    public void setLocaleArray2(Locale[] arr)
    {
        localeArray2 = arr;
    }

    public Locale[] getLocaleArray2()
    {
        return localeArray2;
    }

    public void setElementArray1(ArrayElement[] arr)
    {
        elementArray1 = arr;
    }

    public ArrayElement[] getElementArray1()
    {
        return elementArray1;
    }

    public void setElementArray2(ArrayElement[] arr)
    {
        elementArray2 = arr;
    }

    public ArrayElement[] getElementArray2()
    {
        return elementArray2;
    }

    public void setInterfaceArray(Shape[] arr)
    {
        interfaceArray = arr;
    }

    public Shape[] getInterfaceArray()
    {
        return interfaceArray;
    }

    public void setObjectArray(Object[] arr)
    {
        objectArray = arr;
    }

    public Object[] getObjectArray()
    {
        return objectArray;
    }
}