/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.one_one.bidir_2;

/**
 * Representation of an address where a Destinatary receives mails.
 *
 * @version $Revision: 1.1 $    
 **/
public class InternationalDeliveryAddress extends MailDeliveryAddress
{
    protected String country=null;

    protected InternationalDeliveryAddress()
    {
    }

    public InternationalDeliveryAddress(String name)
    {
        super(name);
    }

    /**
     * Accessor for the country
     * @return country
     */
    public String getCountry()
    {
        return country;
    }
    
    /**
     * Accessor for the country
     * @param country country
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    public String toString()
    {
        return super.toString() + " [country : " + country + "] ";
    }
}