/**********************************************************************
Copyright (c) 2004 Ralf Ulrich and others. All rights reserved.
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
package org.datanucleus.samples.models.leftright;

/**
 * Base class on right-hand-side of relation (with the relation to the left-hand-side).
 * Has 1-1 uni relation with LeftBase.
 * @version $Revision: 1.1 $
 */
public class RightBase
{
    private int id;
    private LeftBase base;

    public RightBase(int id, LeftBase base)
    {
        this.id = id;
        this.base = base;
    }

    public final LeftBase getBase()
    {
        return base;
    }

    public final int getId()
    {
        return id;
    }
}