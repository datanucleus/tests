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
package org.datanucleus.samples.types.optional;

import java.util.Optional;

/**
 * Sample using Java8 Optional.
 */
public class OptionalSample1
{
    private long id;

    private Optional<String> stringField;
    private Optional<Double> doubleField;

    public OptionalSample1(long id, String str, Double dbl)
    {
        this.id = id;
        this.stringField = Optional.of(str);
        this.doubleField = Optional.of(dbl);
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Optional<String> getStringField()
    {
        return stringField;
    }
    public void setStringField(String str)
    {
        this.stringField = Optional.of(str);
    }

    public Optional<Double> getDoubleField()
    {
        return doubleField;
    }
    public void setDoubleField(Double dbl)
    {
        this.doubleField = Optional.of(dbl);
    }
}