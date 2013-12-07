/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.abstractclasses;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Sample Abstract class with single identity field. Extended by ConcreteSimpleSub1, ConcreteSimpleSub2.
 * 
 * @version $Revision: 1.1 $
 */
@MappedSuperclass
public abstract class AbstractSimpleBase
{
    @Id
    @Column(name="ID")
    public int id; // identity

    @Column(name="BASE_FIELD")
    public String baseField;

    public AbstractSimpleBase(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getBaseField()
    {
        return baseField;
    }

    public void setBaseField(String baseField)
    {
        this.baseField = baseField;
    }
}