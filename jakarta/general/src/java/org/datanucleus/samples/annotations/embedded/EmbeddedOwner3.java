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
package org.datanucleus.samples.annotations.embedded;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * 
 */
@Entity
public class EmbeddedOwner3
{
    @Id
    EmbeddedOwner3Id id;

    String name;

    @Embedded
    EmbeddedObject3A a = new EmbeddedObject3A();

    public EmbeddedOwner3()
    {
    }

    public EmbeddedOwner3Id getId() 
    {
        return id;
    }
    public void setId(EmbeddedOwner3Id id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public EmbeddedObject3A getA()
    {
        return a;
    }
    public void setA(EmbeddedObject3A a)
    {
        this.a = a;
    }
}
