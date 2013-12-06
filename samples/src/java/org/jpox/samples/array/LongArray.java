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
package org.jpox.samples.array;

/**
 * Container of a long array.
 * 
 * @version $Revision: 1.2 $
 */
public class LongArray implements ArrayHolderInterface
{
    long id;
    long[] array1;
    long[] array2;

    public LongArray(long[] elements1, long[] elements2)
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