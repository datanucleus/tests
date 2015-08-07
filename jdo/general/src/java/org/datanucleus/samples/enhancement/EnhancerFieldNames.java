/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.enhancement;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Class using fields starting "dn" but not enhancer fields.
 */
@PersistenceCapable
public class EnhancerFieldNames
{
    @PrimaryKey
    long id;

    String name;

    String dnId;

    String dnsName;

    public EnhancerFieldNames(long id, String name, String dnId, String dnsName)
    {
        this.id = id;
        this.name = name;
        this.dnId = dnId;
        this.dnsName = dnsName;
    }

    public String getName()
    {
        return name;
    }
    public String getDnId()
    {
        return dnId;
    }
    public String getDnsName()
    {
        return dnsName;
    }
}