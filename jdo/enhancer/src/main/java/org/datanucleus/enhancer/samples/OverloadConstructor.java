/**********************************************************************
Copyright (c) Jan 18, 2005 erik and others.
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
package org.datanucleus.enhancer.samples;

import java.io.Serializable;

import org.datanucleus.enhancer.samples.id.OverloadConstructorId;


/**
 * test overloaded constructor and id class in separate path
 * @version $Revision: 1.1 $
 */
public class OverloadConstructor implements Serializable
{
    private static final long serialVersionUID = -8733861163939320532L;
    private int fieldA;
    private int fieldB;
    
    public OverloadConstructor()
    {
        fieldA = 0;
    }
    
    public OverloadConstructor(int v)
    {
        fieldA = v;
    }
    
    public OverloadConstructor(String v)
    {
        fieldA = 1;
    }
    public OverloadConstructor(OverloadConstructorId id)
    {
        this.fieldA = id.fieldA;
    }
    
    /**
     * @return Returns the fieldA.
     */
    public int getFieldA()
    {
        return fieldA;
    }
    /**
     * @param fieldA The fieldA to set.
     */
    public void setFieldA(int fieldA)
    {
        this.fieldA = fieldA;
    }
    /**
     * @return Returns the fieldB.
     */
    public int getFieldB()
    {
        return fieldB;
    }
    /**
     * @param fieldB The fieldB to set.
     */
    public void setFieldB(int fieldB)
    {
        this.fieldB = fieldB;
    }
}
