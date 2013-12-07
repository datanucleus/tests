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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Concrete subclass of abstract class, with single identity field.
 * 
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_CONCRETESIMP_SUB2")
public class ConcreteSimpleSub2 extends AbstractSimpleBase
{
    @Basic
    @Column(name="SUB2_FIELD")
    private String sub2Field;

    public ConcreteSimpleSub2(int id)
    {
        super(id);
    }

    public String getSub2Field()
    {
        return sub2Field;
    }

    public void setSub2Field(String fld)
    {
        this.sub2Field = fld;
    }
}