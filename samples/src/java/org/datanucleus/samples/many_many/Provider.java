/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.many_many;

import java.util.ArrayList;
import java.util.List;

/**
 * Provider of some service.
 * M-N relation using a List (ordered).
 */
public class Provider
{
    long id;

    String name;

    List<Consumer> consumers = new ArrayList<Consumer>();

    public Provider(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public long getId()
    {
        return id;
    }

    public List<Consumer> getConsumers()
    {
        return consumers;
    }
}
