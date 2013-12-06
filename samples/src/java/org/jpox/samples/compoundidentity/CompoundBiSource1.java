/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.compoundidentity;

import java.io.Serializable;

/**
 * Model for 1-1 bidirectional with only one side having the FK as PK.
 * This side has an FK across to the other side, but not part of PK.
 *
 * @version $Revision: 1.1 $
 */
public class CompoundBiSource1
{
    private String id; // PK

    private CompoundBiTarget1 target; // FK only

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public CompoundBiTarget1 getTarget()
    {
        return target;
    }

    public void setTarget(CompoundBiTarget1 tgt)
    {
        this.target = tgt;
    }

    public static class Id implements Serializable
    {
        public String id;

        public Id()
        {
        }

        public Id(java.lang.String str)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer(str, "::");
            token.nextToken(); // Ignore first token
            this.id = new String(token.nextToken());
        }

        public java.lang.String toString()
        {
            java.lang.String str = CompoundBiSource1.class.getName() + "::";
            str += java.lang.String.valueOf(this.id);
            return str;
        }

        public int hashCode()
        {
            return (id != null ? id.hashCode() : 0);
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
            Id objToCompare = (Id) o;
            return ((this.id == null ? objToCompare.id == null : this.id.equals(objToCompare.id)));
        }
    }    
}