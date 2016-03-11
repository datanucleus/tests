/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.jpa.criteria.embedded;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class C
{
    @Id
    private Long id;

    private BigDecimal amount;
    private String currency;

    protected C() 
    {
    }

    public C(long id, BigDecimal amount, String currency) 
    {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId()
    {
        return id;
    }
    public void setId(Long id)
    {
        this.id = id;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public String getCurrency() 
    {
        return currency;
    }
    public void setCurrency(String currency) 
    {
        this.currency = currency;
    }
}
