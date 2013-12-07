/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.annotations.models.company;

import javax.persistence.Embeddable;

/**
 * Embeddedable PK class.
 */
@Embeddable
public class DepartmentPK implements java.io.Serializable
{
    private Integer idInteger;
    private String idString;

    public DepartmentPK()
    {
    }

    public DepartmentPK(int intID, String strID)
    {
        this.idInteger = new Integer(intID);
        this.idString = strID;
    }

    public Integer getIdInteger()
    {
        return idInteger;
    }

    public void setIdInteger(Integer idInteger)
    {
        this.idInteger = idInteger;
    }

    public String getIdString()
    {
        return idString;
    }

    public void setIdString(String idString)
    {
        this.idString = idString;
    }

    public int hashCode()
    {
        return getIdInteger().hashCode() + getIdString().hashCode();
    }

    public boolean equals(Object o)
    {
        DepartmentPK other;
        boolean equals = true;

        if (!(o instanceof DepartmentPK))
        {
            return false;
        }
        other = (DepartmentPK) o;

        equals &= getIdInteger().equals(other.getIdInteger());
        equals &= getIdString().equals(other.getIdString());

        return equals;
    }

    public String toString()
    {
        return "DepartmentPK [ " + getIdInteger() + ", " + getIdString() + "] ";
    }
}