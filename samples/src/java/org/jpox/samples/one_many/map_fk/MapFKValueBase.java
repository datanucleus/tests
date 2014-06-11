/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.one_many.map_fk;

import java.io.Serializable;

/**
 * Base of persistable item that is stored as the value in a Map
 */
public class MapFKValueBase implements Serializable
{
    private static final long serialVersionUID = -2966703820831890307L;
    long id; // Used for app identity
    String key;

    public MapFKValueBase(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }
}