/**********************************************************************
Copyright (c) 28-Aug-2004 Andy Jefferson and others.
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
 * Sample class that defines a String array (which JPOX doesn't support).
 *
 * @version $Revision: 1.1 $
 */
public class StringArray
{
    private String[] stringArray;
    
    /**
     * @return Returns the stringArray.
     */
    public String[] getStringArray()
    {
        return stringArray;
    }
    /**
     * @param stringArray The stringArray to set.
     */
    public void setStringArray(String[] stringArray)
    {
        this.stringArray = stringArray;
    }
}
