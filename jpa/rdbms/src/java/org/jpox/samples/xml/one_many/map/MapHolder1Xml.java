/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.xml.one_many.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder of a Map field.
 */
public class MapHolder1Xml
{
    long id;

    Map<String, String> properties = new HashMap<String, String>();

    Map<String, String> properties2 = new HashMap<String, String>();

    public MapHolder1Xml(long id)
    {
        this.id = id;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }
    public Map<String, String> getProperties2()
    {
        return properties2;
    }
}