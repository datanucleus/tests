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
package org.datanucleus.samples.many_many;

/**
 * A Customer who only buys occasionally and doesnt need an account.
 *
 * @version $Revision: 1.1 $    
 **/
public class OneOffCustomer extends PetroleumCustomer
{
    /** Address of the customer. */
    protected String address = null;

    /** Constructor. */
    public OneOffCustomer(String name)
    {
        super(name);
    }

    /**
     * Accessor for the address of the customer.
     * @return The address
     */
    public String getAddress()
    {
        return address;
    }
    
    /**
     * Mutator for the customers address.
     * @param addr customer address
     */
    public void setAddress(String addr)
    {
        this.address = addr;
    }
    
    public String toString()
    {
        return super.toString() + " [address : " + address + "]";
    }
}