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

package org.jpox.samples.models.nightlabs_prices;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PriceConfigName
implements Serializable
{
	private String organisationID;

	private long priceConfigID;

	private PriceConfig priceConfig;

	protected Map names = new HashMap();

	protected PriceConfigName()
	{
	}

	public PriceConfigName(PriceConfig priceConfig)
	{
		this.priceConfig = priceConfig;
		this.organisationID = priceConfig.getOrganisationID();
		this.priceConfigID = priceConfig.getPriceConfigID();
	}

	public String getText(String languageID)
	{
		return (String) names.get(languageID);
	}

	public void setText(String languageID, String text)
	{
		names.put(languageID, text);
	}

    /**
     * @return Returns the names.
     */
    public Map getNames()
    {
        return names;
    }

    /**
     * @return Returns the organisationID.
     */
    public String getOrganisationID()
    {
        return organisationID;
    }

    /**
     * @param organisationID The organisationID to set.
     */
    public void setOrganisationID(String organisationID)
    {
        this.organisationID = organisationID;
    }

    /**
     * @return Returns the priceConfig.
     */
    public PriceConfig getPriceConfig()
    {
        return priceConfig;
    }

    /**
     * @param priceConfig The priceConfig to set.
     */
    public void setPriceConfig(PriceConfig priceConfig)
    {
        this.priceConfig = priceConfig;
    }

    /**
     * @return Returns the priceConfigID.
     */
    public long getPriceConfigID()
    {
        return priceConfigID;
    }

    /**
     * @param priceConfigID The priceConfigID to set.
     */
    public void setPriceConfigID(long priceConfigID)
    {
        this.priceConfigID = priceConfigID;
    }
}