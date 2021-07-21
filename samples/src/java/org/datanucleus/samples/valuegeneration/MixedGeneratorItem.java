/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved. 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 


Contributions
    ...
***********************************************************************/
package org.datanucleus.samples.valuegeneration;

/**
 * Test case for use of multiple strategy value fields.
 *
 * @version $Revision: 1.1 $  
 **/
public class MixedGeneratorItem
{
    protected int identifier;

    protected String baseField;

    public MixedGeneratorItem()
    {
    }

    public int getIdentifier()
    {
        return identifier;
    }

    public String getBaseField()
    {
        return baseField;
    }

    public String toString()
    {
        return "MixedGeneratorItem : " + identifier;
    }
}