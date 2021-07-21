/**********************************************************************
 Copyright (c) 2005 Marco Schulze (NightLabs) and others.
 Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 

 Contributions
 ...
 ***********************************************************************/

package org.datanucleus.samples.models.nightlabs_prices;

import java.io.Serializable;

public class PriceConfigNameID implements Serializable
{
    private static final long serialVersionUID = 6249718559977745218L;

    public String organisationID;

    public long priceConfigID;

    public PriceConfigNameID()
    {
    }

    public PriceConfigNameID(String key)
    {
        String[] parts = key.split("/");
        if (parts.length != 2)
            throw new IllegalArgumentException("key \"" + key + "\" is malformed!");

        this.organisationID = parts[0];
        this.priceConfigID = Long.parseLong(parts[1]);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return organisationID + '/' + priceConfigID;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return toString().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof PriceConfigNameID))
            return false;

        PriceConfigNameID other = (PriceConfigNameID) obj;

        return this.organisationID.equals(other.organisationID) && this.priceConfigID == other.priceConfigID;
    }

}
