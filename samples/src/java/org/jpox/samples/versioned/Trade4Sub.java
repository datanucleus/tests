/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.versioned;

import java.util.Date;

/**
 * Subclass of Trade4, where version is defined as stored in a field.
 */
public class Trade4Sub extends Trade4
{
    String subName;

    public Trade4Sub(String person, double value, Date date)
    {
        super(person, value, date);
    }

    public void setSubName(String name)
    {
        this.subName = name;
    }

    public String getSubName()
    {
        return subName;
    }
}