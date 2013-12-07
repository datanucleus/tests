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

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Concrete extension of an abstract class, with single identity field.
 * 
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_CONCRETESIMP_SUB1")
@AttributeOverride(name="baseField", column=@Column(name="BASE_FIELD_OR"))
public class ConcreteSimpleSub1 extends AbstractSimpleBase
{
    @Basic
    @Column(name="SUB1_FIELD")
    private String sub1Field;

    public ConcreteSimpleSub1(int id)
    {
        super(id);
    }

    public String getSub1Field()
    {
        return sub1Field;
    }

    public void setSub1Field(String fld)
    {
        this.sub1Field = fld;
    }
}