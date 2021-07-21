/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.one_many.collection;

/**
 * Subclass of element stored in 1-N uni FK Set relation.
 * @version $Revision: 1.1 $
 */
public class PCFKSetElementSub2 extends PCFKSetElement
{
    private static final long serialVersionUID = -7544543651402218952L;

    public PCFKSetElementSub2(String name)
    {
        super(name);
    }
}