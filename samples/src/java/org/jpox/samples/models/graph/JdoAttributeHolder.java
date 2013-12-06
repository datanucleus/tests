/**********************************************************************
Copyright (c) 2005 Boris Boehlen and others. All rights reserved.
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
package org.jpox.samples.models.graph;

import java.io.Serializable;

public class JdoAttributeHolder
{
    private Object value;
    private int hashedValue;

    public JdoAttributeHolder(Serializable value)
    {
        this.value = value;
        this.hashedValue = value.hashCode();
    }

    public Serializable getValue()
    {
        return (Serializable) value;
    }

    public int hashCode()
    {
        return hashedValue;
    }

    public boolean equals(Object obj)
    {
        return (obj instanceof JdoAttributeHolder) && ((JdoAttributeHolder) obj).value.equals(value);
    }
}