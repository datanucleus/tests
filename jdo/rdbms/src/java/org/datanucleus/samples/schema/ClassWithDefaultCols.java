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
package org.datanucleus.samples.schema;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Example of a class using default value for some columns.
 */
@PersistenceCapable
public class ClassWithDefaultCols
{
    @PrimaryKey
    long id;

    @Column(defaultValue="Name 1")
    String defaultedName;

    @Column(defaultValue="#NULL")
    String defaultedNameNull;

    @Column(defaultValue="3")
    Long defaultedLong;

    public ClassWithDefaultCols(long id)
    {
        this.id = id;
    }

    public void setDefaultedName(String val)
    {
        this.defaultedName = val;
    }
    public String getDefaultedName()
    {
        return defaultedName;
    }

    public void setDefaultedNameNull(String val)
    {
        this.defaultedNameNull = val;
    }
    public String getDefaultedNameNull()
    {
        return defaultedNameNull;
    }

    public void setDefaultedLong(Long val)
    {
        this.defaultedLong = val;
    }
    public Long getDefaultedLong()
    {
        return defaultedLong;
    }
}
