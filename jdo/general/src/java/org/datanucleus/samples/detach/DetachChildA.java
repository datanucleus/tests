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
public class DetachChildA extends DetachParent
{
    private String fieldD;
    private String fieldE;
    private String fieldF;

    /**
     * @return Returns the fieldD.
     */
    public String getFieldD()
    {
        return fieldD;
    }
    /**
     * @param fieldD The fieldD to set.
     */
    public void setFieldD(String fieldD)
    {
        this.fieldD = fieldD;
    }
    /**
     * @return Returns the fieldE.
     */
    public String getFieldE()
    {
        return fieldE;
    }
    /**
     * @param fieldE The fieldE to set.
     */
    public void setFieldE(String fieldE)
    {
        this.fieldE = fieldE;
    }
    /**
     * @return Returns the fieldF.
     */
    public String getFieldF()
    {
        return fieldF;
    }
    /**
     * @param fieldF The fieldF to set.
     */
    public void setFieldF(String fieldF)
    {
        this.fieldF = fieldF;
    }
}
