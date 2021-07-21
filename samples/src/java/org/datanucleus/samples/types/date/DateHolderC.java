/**********************************************************************
Copyright (c) 2009 Michael Brown and others.
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
package org.datanucleus.samples.types.date;

import java.util.Date;

/**
 * Object with java.util.Date fields.
 */
public class DateHolderC
{
    Date key;
    Date value;
    Date value2;

    public Date getValue()
    {
        return value;
    }

    public void setValue(Date value)
    {
        this.value = value;
    }

    public Date getValue2()
    {
        return value2;
    }

    public void setValue2(Date value)
    {
        this.value2 = value;
    }

    public Date getKey()
    {
        return key;
    }

    public void setKey(Date key)
    {
        this.key = key;
    }
}