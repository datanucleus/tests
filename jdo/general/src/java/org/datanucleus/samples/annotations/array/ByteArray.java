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
package org.datanucleus.samples.annotations.array;

import javax.jdo.annotations.PersistenceCapable;

import org.jpox.samples.array.ArrayHolderInterface;

/**
 * Container of a byte array.
 */
@PersistenceCapable
public class ByteArray implements ArrayHolderInterface
{
    long id;
    byte[] array1;
    byte[] array2;

    public ByteArray(byte[] elements1, byte[] elements2)
    {
        this.array1 = elements1;
        this.array2 = elements2;
    }

    public Object getArray1()
    {
        return array1;
    }
    public Object getArray2()
    {
        return array2;
    }
}