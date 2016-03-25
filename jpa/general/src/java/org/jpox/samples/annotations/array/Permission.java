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
package org.jpox.samples.annotations.array;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity held within an array.
 */
@Entity
public class Permission
{
    @Id
    long id;

    String name;

    public Permission(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
}
