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
package org.jpox.samples.identity.application;

/**
 * Class with identity using fields of types int, String, Double.
 * @version $Revision: 1.1 $
 */
public class ComposedMixedIDSub extends ComposedMixedIDBase
{
    private String childDescription;

    public void fillUpdateRandom()
    {
        super.fillUpdateRandom();
        childDescription = "Child Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble() * 1000);
    }

    public boolean compareTo(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof ComposedMixedIDSub) || !super.compareTo(obj))
            return false;

        ComposedMixedIDSub other = (ComposedMixedIDSub) obj;
        return childDescription.equals(other.childDescription);
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  childDescription = ").append(childDescription);
        s.append('\n');
        return s.toString();
    }
}