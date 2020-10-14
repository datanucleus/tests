/**********************************************************************
Copyright (c) Aug 17, 2004 Erik Bengtson and others.
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
package org.datanucleus.enhancer.samples;

/**
 * @author Erik Bengtson
 * @version $Revision: 1.1 $
 */
public class MutuallyExclusiveJDOTags1
{
    private Object theField;

    /**
     * @return Returns the theField.
     */
    public Object getTheField()
    {
        return theField;
    }
    /**
     * @param theField The theField to set.
     */
    public void setTheField(Object theField)
    {
        this.theField = theField;
    }
}
