/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others.
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
package org.datanucleus.samples.types.cls;

/**
 * Object with a Class field.
 */
public class ClassHolder
{
    Class cls;

    /**
     * Accessor for the class
     * @return class
     */    
    public Class getCls()
    {
        return cls;
    }

    /**
     * Mutator for the Class.
     * @param cls The class
     */
    public void setCls(Class cls)
    {
        this.cls = cls;
    }
}