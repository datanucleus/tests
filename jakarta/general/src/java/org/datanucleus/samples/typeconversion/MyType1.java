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
package org.datanucleus.samples.typeconversion;

public class MyType1
{
    String name1;
    String name2;

    public MyType1(String name1, String name2)
    {
        this.name1 = name1;
        this.name2 = name2;
    }
    public String getName1()
    {
        return name1;
    }
    public String getName2()
    {
        return name2;
    }
    public int hashCode()
    {
        return (int) (name1.hashCode() ^ name2.hashCode());
    }
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof MyType1))
        {
            return false;
        }
        MyType1 other = (MyType1)obj;
        return other.name1.equals(name1) && other.name2.equals(name2);
    }
}