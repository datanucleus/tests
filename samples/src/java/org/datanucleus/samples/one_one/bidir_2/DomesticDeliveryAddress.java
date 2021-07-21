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
package org.datanucleus.samples.one_one.bidir_2;

/**
 * Representation of an address where a Destinatary receives mails.
 *
 * @version $Revision: 1.1 $    
 **/
public class DomesticDeliveryAddress extends MailDeliveryAddress
{
    protected String zip=null;

    protected DomesticDeliveryAddress()
    {
    }

    public DomesticDeliveryAddress(String name)
    {
        super(name);
    }

    /**
     * Accessor for the zip code
     * @return zip code
     */
    public String getZip()
    {
        return zip;
    }
    
    /**
     * Accessor for the zip code
     * @param zip zip code
     */
    public void setZip(String zip)
    {
        this.zip = zip;
    }
    
    public String toString()
    {
        return super.toString() + " [zip : " + zip + "] ";
    }
}