/**********************************************************************
Copyright (c) 2018 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.jpa.criteria;

import javax.persistence.Entity;

@Entity
public class ConcreteEntity105 extends AbstractEntity105
{
    String property;

    public ConcreteEntity105(long id, String name, String property)
    {
        super(id, name);
        this.property = property;
    }

    public String getProperty()
    {
        return property;
    }
}