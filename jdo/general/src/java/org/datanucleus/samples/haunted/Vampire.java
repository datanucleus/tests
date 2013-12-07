/**********************************************************************
Copyright (c) 2007 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.haunted;

/**
 * This is a pc class with the scenario:
 * -Fields starting with "_" are marked transient in metadat
 * -Properties are declared persistent in metadata 
 */
public class Vampire
{
    private String _name;
    private int _age;
    
    public String getName()
    {
        return _name;
    }
    
    public void setName(String name)
    {
        this._name = name;
    }
    
    public int getAge()
    {
        return _age;
    }
    
    public void setAge(int age)
    {
        this._age = age;
    }
}
