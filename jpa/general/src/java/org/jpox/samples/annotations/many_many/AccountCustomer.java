/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.many_many;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Customer with an account.
 *
 * @version $Revision: 1.1 $    
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="JPA_AN_ACCTCUST")
public class AccountCustomer extends PetroleumCustomer
{
    protected String accountNumber = null;

    public AccountCustomer(long id, String name)
    {
        super(id, name);
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setCardNumber(String number)
    {
        this.accountNumber = number;
    }
    
    public String toString()
    {
        return super.toString() + " [card number : " + accountNumber + "]";
    }
}