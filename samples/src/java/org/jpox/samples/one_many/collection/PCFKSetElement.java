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
 

Contributors:
    ...
**********************************************************************/
package org.jpox.samples.one_many.collection;

import java.io.Serializable;

/**
 * Sample element stored in a collection/list using a foreign key.
 * 
 * @version $Revision: 1.1 $
 */
public class PCFKSetElement implements Serializable
{
    private static final long serialVersionUID = 5906396604075699262L;
    String name;
    public PCFKSetElement(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}