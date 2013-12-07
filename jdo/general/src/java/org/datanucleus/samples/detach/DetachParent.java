/**********************************************************************
Copyright (c) Aug 20, 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.detach;

/**
 * @author erik
 * @version $Revision: 1.1 $
 */
public class DetachParent
{
    private String fieldA;
    private String fieldB;
    private String fieldC;
    
    /**
     * @return Returns the fieldA.
     */
    public String getFieldA()
    {
        return fieldA;
    }
    /**
     * @param fieldA The fieldA to set.
     */
    public void setFieldA(String fieldA)
    {
        this.fieldA = fieldA;
    }
    /**
     * @return Returns the fieldB.
     */
    public String getFieldB()
    {
        return fieldB;
    }
    /**
     * @param fieldB The fieldB to set.
     */
    public void setFieldB(String fieldB)
    {
        this.fieldB = fieldB;
    }
    /**
     * @return Returns the fieldC.
     */
    public String getFieldC()
    {
        return fieldC;
    }
    /**
     * @param fieldC The fieldC to set.
     */
    public void setFieldC(String fieldC)
    {
        this.fieldC = fieldC;
    }
}
