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
public class DetachChildB extends DetachParent
{
    private String fieldG;
    private String fieldH;
    private String fieldI;
    
    /**
     * @return Returns the fieldG.
     */
    public String getFieldG()
    {
        return fieldG;
    }
    /**
     * @param fieldG The fieldG to set.
     */
    public void setFieldG(String fieldG)
    {
        this.fieldG = fieldG;
    }
    /**
     * @return Returns the fieldH.
     */
    public String getFieldH()
    {
        return fieldH;
    }
    /**
     * @param fieldH The fieldH to set.
     */
    public void setFieldH(String fieldH)
    {
        this.fieldH = fieldH;
    }
    /**
     * @return Returns the fieldI.
     */
    public String getFieldI()
    {
        return fieldI;
    }
    /**
     * @param fieldI The fieldI to set.
     */
    public void setFieldI(String fieldI)
    {
        this.fieldI = fieldI;
    }
}
