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
package org.datanucleus.samples.models.nullability;

/**
 * Root class that has a nullable (optional) 1-1 field.
 */
public class NullabilityOwner
{
    Long id;

    String name;

    NullabilityOptionalMember optionalMember;

    public NullabilityOwner(long id, String name, NullabilityOptionalMember optionalMember)
    {
        this.id = id;
        this.name = name;
        this.optionalMember = optionalMember;
    }

    public String getName()
    {
        return name;
    }

    public Long getId()
    {
        return id;
    }

    public NullabilityOptionalMember getOptionalMember()
    {
        return optionalMember;
    }
}
