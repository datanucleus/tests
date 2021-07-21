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
package org.datanucleus.samples.rdbms.views;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(detachable="true", identityType=IdentityType.NONDURABLE)
@Extension(vendorName="datanucleus", key="view-definition", 
    value="CREATE VIEW {this} ({this.id},{this.name}) AS "+
        "SELECT {NameObject}.{NameObject.id}, {NameObject}.{NameObject.name} FROM {NameObject} WHERE {NameObject}.{NameObject.name} LIKE 'F%'")
@Extension(vendorName="datanucleus", key="view-imports", value="import org.datanucleus.samples.rdbms.views.NameObject;")
public class FNameView 
{
    Long id;
    String name;

    public FNameView(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Long getId()
    {
        return id;
    }
}