/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others.
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
package org.jpox.samples.types.sqltimestamp;

import java.sql.Timestamp;

/**
 * Object with java.sql.Timestamp fields.
 */
public class SqlTimestampHolder
{
    Timestamp key;
    Timestamp value;
    Timestamp value2;

    public Timestamp getValue()
    {
        return value;
    }

    public void setValue(Timestamp value)
    {
        this.value = value;
    }

    public Timestamp getValue2()
    {
        return value2;
    }

    public void setValue2(Timestamp value)
    {
        this.value2 = value;
    }

    public Timestamp getKey()
    {
        return key;
    }

    public void setKey(Timestamp key)
    {
        this.key = key;
    }
}