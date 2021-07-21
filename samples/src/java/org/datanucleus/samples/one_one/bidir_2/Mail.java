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
 * Definition of a Mail
 *
 * @version $Revision: 1.1 $    
 **/
public class Mail
{
    /** Name of the destination. */
    protected String name=null;

    /** Delivery address. */
    MailDeliveryAddress deliveryAddress = null;

    /** Constructor. */
    public Mail(String name)
    {
        this.name = name;
    }

    /**
     * Accessor for the name of the Destination.
     * @return Destination name
     */
    public String  getName()
    {
        return name;
    }

    /**
     * Accessor for the delivery address.
     * @return Delivery address.
     */
    public MailDeliveryAddress getDeliveryAddress()
    {
        return deliveryAddress;
    }

    /**
     * Mutator for the name of the destination.
     * @param name Name of the destination.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return "Mail : " + name + " [" + name +"]";
    }
}