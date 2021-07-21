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

package org.datanucleus.samples.compoundidentity;

import java.io.Serializable;

/**
 * Classes using identifying relationship with 4 levels depth.
 * @version $Revision: 1.1 $
 */
public class CompoundSourceL1
{
    private String id; // PK

    public CompoundSourceL1(String id)
    {
        super();
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public static class Id implements Serializable
    {
        private static final long serialVersionUID = -5616155855510301560L;
        public String id;

        public Id()
        {
        }

        public Id(java.lang.String str)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer(str, "::");
            this.id = new String(token.nextToken());
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.id) + "::";
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