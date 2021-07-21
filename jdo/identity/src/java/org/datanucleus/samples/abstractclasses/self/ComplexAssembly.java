/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.abstractclasses.self;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ComplexAssembly extends Assembly
{
    private Set subAssemblies = new HashSet();

    public ComplexAssembly()
    {
    }

    public ComplexAssembly(int levelNo)
    {
        // recursively create subassemblies for this complex assembly
        for (int i = 0; i < 3; i++)
        {
            if (levelNo < 4)
            {
                subAssemblies.add(new ComplexAssembly(levelNo + 1));
            }
        }
    }

    public void clearSubAssemblies()
    {
        for (Iterator i = subAssemblies.iterator(); i.hasNext();)
        {
            ComplexAssembly ap = (ComplexAssembly) i.next();
            ap.clearSubAssemblies();
        }
        subAssemblies.clear();
    }

    public int traverse(int op)
    {
        //     traverse each of the assembly's subassemblies
        int count = 0;
        for (Iterator i = subAssemblies.iterator(); i.hasNext();)
        {
            Assembly ap = (Assembly) i.next();
            count += ap.traverse(op);
        }

        return count;
    }
}