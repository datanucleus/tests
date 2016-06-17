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
public class OptionalSample3
{
    private long id;

    private String name;

    private Optional<OptionalSample3> sample3;

    public OptionalSample3(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public Optional<OptionalSample3> getSample3()
    {
        return sample3;
    }
    public void setSample3(OptionalSample3 s3)
    {
        this.sample3 = s3!=null ? Optional.of(s3) : Optional.empty();
    }
}