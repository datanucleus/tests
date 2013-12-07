/**********************************************************************
Copyright (c) Jan 18, 2005 erik and others.
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
package org.datanucleus.enhancer.samples.id;

import java.io.Serializable;


/**
 * test overloaded constructor and id class in separate path
 * @version $Revision: 1.1 $
 */
public class OverloadConstructorId implements Serializable
{

    public int fieldA;

    public OverloadConstructorId()
    {
        //default constructor
    }

    public OverloadConstructorId(java.lang.String str)
    {
        java.util.StringTokenizer token = new java.util.StringTokenizer(str, "::");
        this.fieldA = new java.lang.Integer(token.nextToken()).intValue();
    }

    public java.lang.String toString()
    {
        java.lang.String str = "";
        str += java.lang.String.valueOf(this.fieldA);
        return str;
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (o.getClass() != getClass())
        {
            return false;
        }
        OverloadConstructorId objToCompare = (OverloadConstructorId) o;
        return ((this.fieldA == objToCompare.fieldA));
    }
}