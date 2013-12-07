/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.typeconversion;

/**
 * Type that we are not persisting as an entity, and will persist using a converter.
 */
public class ComplicatedType2
{
    String name1;
    
    String name2;

    public ComplicatedType2(String str1, String str2)
    {
        this.name1 = str1;
        this.name2 = str2;
    }

    public String getName1()
    {
        return name1;
    }

    public String getName2()
    {
        return name2;
    }
}
