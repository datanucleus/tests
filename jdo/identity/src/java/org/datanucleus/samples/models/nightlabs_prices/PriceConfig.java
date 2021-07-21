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

public abstract class PriceConfig
{
	private String organisationID;

	private long priceConfigID;

	private PriceConfigName name;

	protected PriceConfig() { }

	public PriceConfig(String organisationID, long priceConfigID)
	{
		this.organisationID = organisationID;
		this.priceConfigID = priceConfigID;
		this.name = new PriceConfigName(this);
	}
	
	/**
	 * @return Returns the organisationID.
	 */
	public String getOrganisationID()
	{
		return organisationID;
	}
	/**
	 * @return Returns the priceConfigID.
	 */
	public long getPriceConfigID()
	{
		return priceConfigID;
	}
	/**
	 * @return Returns the name.
	 */
	public PriceConfigName getName()
	{
		return name;
	}
}
