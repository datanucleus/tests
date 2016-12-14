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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Id class for Embedded3Owner.
 */
@Embeddable
public class EmbeddedOwner3Id
{
    private String value;
    private String key;

    public EmbeddedOwner3Id() {
    }

    public EmbeddedOwner3Id(String value, String key)
    {
        this.value = value;
        this.key = key;
    }

    @Column(name = "test_value", nullable = false, length = 10)
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }

    @Column(name = "test_key", nullable = false, length = 100)
    public String getKey()
    {
        return key;
    }
    public void setKey(String key)
    {
        this.key = key;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof EmbeddedOwner3Id))
        {
            return false;
        }

        EmbeddedOwner3Id that = (EmbeddedOwner3Id) o;
        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null)
        {
            return false;
        }
        return getKey() != null ? getKey().equals(that.getKey()) : that.getKey() == null;
    }

    @Override
    public int hashCode()
    {
        int result = getValue() != null ? getValue().hashCode() : 0;
        result = 31 * result + (getKey() != null ? getKey().hashCode() : 0);
        return result;
    }
}
