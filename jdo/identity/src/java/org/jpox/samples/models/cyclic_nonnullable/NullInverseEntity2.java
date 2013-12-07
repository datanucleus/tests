/**********************************************************************
Copyright (c) 2006 Michael Brown and others.
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
package org.jpox.samples.models.cyclic_nonnullable;

/**
 *
 */
public class NullInverseEntity2
{
    private NullInverseEntity  inverse = null;
    private NullInverseEntity3 forward = null;
    private CompoundType       values = null;
    
    public NullInverseEntity getInverse()
    {
        return inverse;
    }

    public void setInverse(NullInverseEntity inverse)
    {
        this.inverse = inverse;
    }

    public NullInverseEntity3 getForward()
    {
        return forward;
    }

    public void setForward(NullInverseEntity3 forward)
    {
        this.forward = forward;
    }

    public CompoundType getValues()
    {
        return values;
    }

    public void setValues(CompoundType values)
    {
        this.values = values;
    }
}