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
package org.datanucleus.samples.annotations.embedded.map;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Value embedded in a map.
 */
@Embeddable
public class EmbeddedMapValue
{
    @Column(name="NAME")
    String name;

    @Column(name="VALUE")
    long value;

    public EmbeddedMapValue(String name, long val)
    {
        this.name = name;
        this.value = val;
    }

    public String getName()
    {
        return name;
    }
    public long getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (value ^ (value >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmbeddedMapValue other = (EmbeddedMapValue) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (value != other.value)
            return false;
        return true;
    }
}
