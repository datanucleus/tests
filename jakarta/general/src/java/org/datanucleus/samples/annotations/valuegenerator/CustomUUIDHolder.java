/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.valuegenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.datanucleus.api.jakarta.annotations.ValueGenerator;

/**
 * Class using custom UUID generator field.
 */
@Entity
@Table(name="JPA_ANN_CUSTOMUUIDHOLDER")
public class CustomUUIDHolder
{
    @Id
    @ValueGenerator(strategy="uuid")
    String uid;

    String name;

    public CustomUUIDHolder()
    {
        super();
    }

    public String getUid()
    {
        return uid;
    }

    public void setNameField(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }
}