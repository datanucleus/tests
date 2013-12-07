/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.typeconversion;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Convert;

/**
 * Sample class holding another type to be converted for persistence.
 */
@Entity
public class TypeHolder
{
    @Id
    long id;

    String name;

    @Basic
    @Column(name="MY_DETAILS")
    @Convert(converter=ComplicatedTypeConverter.class)
    ComplicatedType details;

    @Basic
    @Column(name="MY_DETAILS2")
    ComplicatedType2 details2;

    @Basic
    @Column(name="MY_DETAILS3")
    @Convert(converter=InheritedTypeConverter.class)
    ComplicatedType details3;
	
    public TypeHolder(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public void setDetails(ComplicatedType det)
    {
        this.details = det;
    }

    public ComplicatedType getDetails()
    {
        return details;
    }

    public void setDetails2(ComplicatedType2 det)
    {
        this.details2 = det;
    }

    public ComplicatedType2 getDetails2()
    {
        return details2;
    }
	 
	public void setDetails3(ComplicatedType det)
    {
        this.details3 = det;
    }

    public ComplicatedType getDetails3()
    {
        return details3;
    }
}
