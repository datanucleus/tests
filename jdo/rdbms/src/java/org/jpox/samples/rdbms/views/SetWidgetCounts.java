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
package org.jpox.samples.rdbms.views;

import org.datanucleus.tests.TestObject;
import org.jpox.samples.rdbms.views.SetWidgetCounts;
import org.jpox.samples.widget.SetWidget;

public class SetWidgetCounts extends TestObject
{
    private SetWidget sw;
    private int normalSetSize;
    private int inverseSetSize;

    protected SetWidgetCounts() {}

    public SetWidgetCounts(SetWidget sw)
    {
        this.sw = sw;
        this.normalSetSize = sw.getNormalSet().size();
        this.inverseSetSize = sw.getInverseSet().size();
    }

    public SetWidget getSetWidget()
    {
        return sw;
    }

    public void fillRandom()
    {
        throw new UnsupportedOperationException();
    }

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof SetWidgetCounts))
            return false;

        SetWidgetCounts swc = (SetWidgetCounts)obj;

        return sw.compareTo(swc.sw)
            && normalSetSize  == swc.normalSetSize
            && inverseSetSize == swc.inverseSetSize;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  sw = ").append(sw);
        s.append('\n');
        s.append("  normalSetSize = ").append(normalSetSize);
        s.append('\n');
        s.append("  inverseSetSize = ").append(inverseSetSize);
        s.append('\n');

        return s.toString();
    }
}