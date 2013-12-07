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

Contributors :
 ...
***********************************************************************/
package org.datanucleus.samples.ann_xml.override;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * User account for a person.
 */
@PersistenceCapable(detachable="true")
public class Account implements Serializable
{
  //@Persistent(primaryKey="true", valueStrategy=IdGeneratorStrategy.NATIVE)
    @PrimaryKey
    @Persistent(valueStrategy=IdGeneratorStrategy.NATIVE) // default
    private long id; // PK if app id

    @Persistent(nullValue=NullValue.EXCEPTION)
    private String username;

    private boolean enabled;

    public Account()
    {
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean b)
    {
        enabled = b;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String s)
    {
        username = s;
    }
}