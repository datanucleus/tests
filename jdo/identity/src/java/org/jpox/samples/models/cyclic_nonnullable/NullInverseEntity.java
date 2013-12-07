/**********************************************************************
Copyright (c) 2006 Michael Brown and others.
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
package org.jpox.samples.models.cyclic_nonnullable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class NullInverseEntity
{
    private long id = 0;
    private Map other = new HashMap();

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Map getOther()
    {
        return other;
    }
}
