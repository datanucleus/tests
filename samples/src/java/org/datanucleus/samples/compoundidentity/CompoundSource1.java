/**********************************************************************
Copyright (c) 2006 Rick Moore and others. All rights reserved. 
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
package org.datanucleus.samples.compoundidentity;

/**
 * Example user-provided source class with 1-1 uni relation.
 *
 * @version $Revision: 1.1 $
 */
public class CompoundSource1
{
    private String idAttr; // PK

    private String address1Attr;
    private String address2Attr;
    private Boolean billingFlagAttr;
    private String stateAttr;
    private String cityAttr;
    private String zipAttr;
    private CompoundTarget1 target;

    public String getAddress1Attr()
    {
        return address1Attr;
    }

    public void setAddress1Attr(String inVal)
    {
        address1Attr = inVal;
    }

    public String getAddress2Attr()
    {
        return address2Attr;
    }

    public void setAddress2Attr(String inVal)
    {
        address2Attr = inVal;
    }

    public Boolean getBillingFlagAttr()
    {
        return billingFlagAttr;
    }

    public void setBillingFlagAttr(Boolean inVal)
    {
        billingFlagAttr = inVal;
    }

    public String getCityAttr()
    {
        return cityAttr;
    }

    public void setCityAttr(String inVal)
    {
        cityAttr = inVal;
    }

    public String getStateAttr()
    {
        return stateAttr;
    }

    public void setStateAttr(String inVal)
    {
        stateAttr = inVal;
    }

    public String getZipAttr()
    {
        return zipAttr;
    }

    public void setZipAttr(String inVal)
    {
        zipAttr = inVal;
    }

    public String getIdAttr()
    {
        return idAttr;
    }

    public void setIdAttr(String inVal)
    {
        idAttr = inVal;
    }

    public CompoundTarget1 getTarget()
    {
        return target;
    }

    public synchronized void setTarget(CompoundTarget1 target)
    {
        this.target = target;
        target.setSource(this);
    }

    public static class Id implements java.io.Serializable
    {
        private static final long serialVersionUID = 4155011195939063058L;
        public String idAttr;

        public Id()
        {
        }

        public Id(String astrValue)
        {
            java.util.StringTokenizer lST = new java.util.StringTokenizer(astrValue, "::");
            this.idAttr = new String(lST.nextToken());
        }

        public boolean equals(Object aObject)
        {
            if (aObject == null)
            {
                return false;
            }

            if (aObject == this)
            {
                return true;
            }

            if (!(aObject instanceof Id))
            {
                return false;
            }
            Id lAddressCDOCcKey = (Id) aObject;
            if (idAttr == null || lAddressCDOCcKey.idAttr == null)
            {
                return false;
            }
            return idAttr.equals(lAddressCDOCcKey.idAttr);
        }

        public int hashCode()
        {
            if (idAttr == null)
            {
                return super.hashCode();
            }
            return this.idAttr.hashCode();
        }

        public String toString()
        {
            StringBuffer lSB = new StringBuffer();
            if (idAttr == null)
            {
                lSB.append("null");
            }
            else
            {
                lSB.append(this.idAttr.toString());
            }

            return lSB.toString();
        }
    }

}