/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.valuegeneration;

/**
 * Sample subclass of a parent using identity generation. This class has no table of its own.
 * 
 * @version $Revision: 1.1 $
 */
public class IdentityGeneratorItemSub extends IdentityGeneratorItem
{
    String extra;

    public IdentityGeneratorItemSub(String name, String extra)
    {
        super(name);
        this.extra = extra;
    }

    public String getExtra()
    {
        return extra;
    }

    public String toString()
    {
        return "IdentityGeneratorItemSub " + super.toString();
    }
}