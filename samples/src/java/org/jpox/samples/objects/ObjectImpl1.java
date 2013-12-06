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
package org.jpox.samples.objects;

/**
 * Sample implementation of a class that is represented in a relation as java.lang.Object.
 * 
 * @version $Revision: 1.1 $
 */
public class ObjectImpl1
{
    long id; // Used for application identity
    private String name;

    public ObjectImpl1(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}