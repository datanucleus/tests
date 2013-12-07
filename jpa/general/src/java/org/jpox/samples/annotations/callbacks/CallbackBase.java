/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.callbacks;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * Base object on which callbacks are invoked.
 *
 * @version $Revision: 1.2 $
 */
@Entity
@Table(name="JPA_AN_CALLBACK")
@DiscriminatorColumn(name="TYPE", length=60, discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("Base")
public class CallbackBase
{
    public String id;
    public String name;

    /** Register of callbacks invoked. */
    public static transient List<Class> invoked = new ArrayList<Class>();

    @Id
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
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
    
    @PrePersist
    public void prePersist()
    {
        invoked.add(CallbackBase.class);
    }
}