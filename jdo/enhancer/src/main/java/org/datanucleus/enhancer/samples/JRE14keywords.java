/**********************************************************************
 Copyright (c) 2006 Scott Leschke and others. All rights reserved.
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
 2006 Scott Leschke (Motorola) - initially contributed as test case
 ...
 **********************************************************************/
package org.datanucleus.enhancer.samples;

/**
 * test class using assert JDK 1.4 keyword
 */
public class JRE14keywords
{
    public JRE14keywords(String id)
    {
        super();
        /*assert (id != null);*/
        this.id = id;
    }

    /**
     * @return
     */
    public String getID()
    {
        return id;
    }

    // Attributes
    private String id;
}
