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

import org.jpox.samples.widget.Widget;

/**
 * This view is a part of a circular references of views.
 * CircularReferenceView1 -> CircularReferenceView2 -> 
 * CircularReferenceView3 -> CircularReferenceView1
 */
public class CircularReferenceView1
{
    private Widget widget;
    private int myInt;

    protected CircularReferenceView1() {}

    public Widget getWidget()
    {
        return this.widget;
    }

    public int getMyInt()
    {
        return this.myInt;
    }
}