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
package org.datanucleus.samples.rdbms.views;

/**
 * This view depends on fields in ReliedOnView.  If these views are
 * created out of order, creation will fail.
 */
public class DependentView
{
    private int inverseInverseInt;
    private Integer inverseInverseIntObj;

    protected DependentView() {}

    public int getInverseInverseInt()
    {
        return this.inverseInverseInt;
    }

    public Integer getInverseInverseIntObj()
    {
        return this.inverseInverseIntObj;
    }
}