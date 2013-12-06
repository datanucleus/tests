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
 * Sample subclass of a parent using autoincrement.
 * This class has its own table.
 * 
 * @version $Revision: 1.1 $
 */
public class IdentityGeneratorItemSub1 extends IdentityGeneratorItemSub
{
    String extra1;

    public IdentityGeneratorItemSub1(String name, String extra, String extra1)
    {
        super(name, extra);
        this.extra1 = extra1;
    }

    public String getExtra1()
    {
        return extra1;
    }

    public String toString()
    {
        return "IdentityGeneratorItemSub1 " + super.toString();
    }
}