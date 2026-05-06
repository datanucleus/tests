/**********************************************************************
Copyright (c) 2026 Contributors. All rights reserved.
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
package org.datanucleus.samples.rdbms.scostore;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;

/**
 * Element class for join-table List, for testing bulk shift ordering.
 */
@PersistenceCapable
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
public class ListElement
{
    String value;

    public ListElement(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
