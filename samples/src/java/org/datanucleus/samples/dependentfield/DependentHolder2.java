/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.dependentfield;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Class that has a field that can be used as a test to test dependent fields being used as PKs.
 */
public class DependentHolder2
{
    SimpleDependentElement element;

    public DependentHolder2()
    {
        super();
    }

    public DependentHolder2(SimpleDependentElement element)
    {
        super();
        this.element = element;
    }

    public SimpleDependentElement getElement()
    {
        return element;
    }

    public void setElement(SimpleDependentElement element)
    {
        this.element = element;
    }

    public boolean equals(Object arg0)
    {
        if (arg0 == null || !(arg0 instanceof DependentHolder2) || this.element == null)
        {
            return false;
        }
        DependentHolder2 df = (DependentHolder2) arg0;
        return this.element.equals(df.element);
    }

    /**
     * Inner class representing Primary Key
     */
    public static class PK implements Serializable
    {
        private static final long serialVersionUID = 2314016605544186107L;
        public SimpleDependentElement.PK element; // Use same name as the real field above

        public PK()
        {
        }

        public PK(String s)
        {
            StringTokenizer token = new StringTokenizer(s,"::");

            this.element = new SimpleDependentElement.PK(token.nextToken());
        }

        public String toString()
        {
            return "" + this.element.toString();
        }

        public int hashCode()
        {
            return element.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof PK))
            {
                PK otherPK = (PK)other;
                return this.element.equals(otherPK.element);
            }
            return false;
        }
    }
}