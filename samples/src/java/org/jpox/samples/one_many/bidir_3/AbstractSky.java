/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.one_many.bidir_3;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract container.
 */
public abstract class AbstractSky
{
    long id;

    List<Cloud> clouds;

    public void setId(long id)
    {
        this.id = id;
    }

    public List<Cloud> getClouds()
    {
        if (this.clouds == null)
        {
            this.clouds = new ArrayList<Cloud>();
        }
        return java.util.Collections.unmodifiableList(clouds);
    }

    public void addCloud(Cloud cl)
    {
        if (this.clouds == null)
        {
            this.clouds = new ArrayList();
        }
        this.clouds.add(cl);
    }

    public void removeCloud(Cloud cl)
    {
        this.clouds.remove(cl);
    }
}