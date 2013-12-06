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
package org.jpox.samples.types.sqltime;

import java.sql.Time;

/**
 * Object with java.sql.Time fields.
 */
public class SqlTimeHolder
{
    Time key;
    Time value;
    Time value2;

    public Time getValue()
    {
        return value;
    }

    public void setValue(Time value)
    {
        this.value = value;
    }

    public Time getValue2()
    {
        return value2;
    }

    public void setValue2(Time value)
    {
        this.value2 = value;
    }

    public Time getKey()
    {
        return key;
    }

    public void setKey(Time key)
    {
        this.key = key;
    }
}