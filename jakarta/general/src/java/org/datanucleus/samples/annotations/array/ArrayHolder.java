/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

/**
 * Holder of different types of array field.
 */
@Entity
public class ArrayHolder
{
    @Id
    long id;

    @Lob
    int[] intArray;

    @ElementCollection
    @CollectionTable
    long[] longArray;

    @OneToMany
    @JoinTable
    Permission[] permissions;

    public ArrayHolder(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public int[] getIntArray()
    {
        return intArray;
    }
    public void setIntArray(int[] arr)
    {
        this.intArray = arr;
    }

    public long[] getLongArray()
    {
        return longArray;
    }
    public void setLongArray(long[] arr)
    {
        this.longArray = arr;
    }

    public Permission[] getPermissions()
    {
        return permissions;
    }
    public void setPermissions(Permission[] perms)
    {
        this.permissions = perms;
    }
}
