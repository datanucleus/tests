/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.models.company;

import org.datanucleus.metadata.MetaData;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

/**
 * Project in a company.
 */
@PersistenceCapable(table="JDO_AN_PROJECT")
@Version(strategy= VersionStrategy.VERSION_NUMBER, column="versionno",
        extensions = {
                @Extension(vendorName = MetaData.VENDOR_NAME, key = MetaData.EXTENSION_VERSION_NUMBER_INITIAL_VALUE, value = "0"),
                @Extension(vendorName = MetaData.VENDOR_NAME, key = MetaData.EXTENSION_VERSION_NUMBER_MAX_VALUE, value = "9999")
        }
)
public class Project
{
    @PrimaryKey
    String name; // PK when app id

    long budget;

    public Project(String name, long budget)
    {
        super();
        this.name = name;
        this.budget = budget;
    }

    public long getBudget()
    {
        return budget;
    }

    public void setBudget(long budget)
    {
        this.budget = budget;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}