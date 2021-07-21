/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.compoundidentity;

/**
 * Concrete sub class using compound identity.
 */
public class CompoundConcreteSub extends CompoundAbstractBase
{
    private static final long serialVersionUID = 3540595722625648459L;
    private String  value;

    public CompoundConcreteSub(CompoundRelated rel, String name, String value)
    {
        super(rel, name);
        this.value = value;
    }

    public String getValue()
    {
        return (value);
    }
}