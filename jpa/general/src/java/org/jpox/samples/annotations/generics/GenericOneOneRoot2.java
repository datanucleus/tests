/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.generics;

import java.io.Serializable;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GenericOneOneRoot2<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = -7756510615319129299L;

    @ManyToOne(optional = false)
    private T owner;
    
    public T getOwner()
    {
        return owner;
    }
    public void setOwner(T owner)
    {
        this.owner = owner;
    }
}
