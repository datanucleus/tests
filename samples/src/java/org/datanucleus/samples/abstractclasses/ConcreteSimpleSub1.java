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
package org.datanucleus.samples.abstractclasses;

/**
 * Concrete extension of an abstract class, with single identity field.
 * 
 * @version $Revision: 1.1 $
 */
public class ConcreteSimpleSub1 extends AbstractSimpleBase
{
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