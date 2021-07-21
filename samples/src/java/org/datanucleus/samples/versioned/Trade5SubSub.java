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

Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.versioned;

import java.util.Date;

/**
 * Subclass of subclass of trade, to test inheritance of versioned objects.
 * @version $Revision: 1.1 $
 */
public class Trade5SubSub extends Trade5Sub
{
    String name;

    public Trade5SubSub(long id, String person, double value, Date date)
    {
        super(id, person, value, date);
    }

    public void setName(String name)
    {
        this.name = name;
    }
}