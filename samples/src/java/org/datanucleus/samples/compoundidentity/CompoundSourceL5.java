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
public class CompoundSourceL5
{
    private CompoundSourceL4 source; // PK
    private CompoundSourceL1 source2; // PK
    
    public CompoundSourceL5(CompoundSourceL4 src, CompoundSourceL1 src2)
    {
        super();
        this.source = src;
        this.source2 = src2;
    }

    public CompoundSourceL4 getSource()
    {
        return source;
    }

    public void setSource(CompoundSourceL4 src)
    {
        this.source = src;
    }

    public CompoundSourceL1 getSource2()
    {
        return source2;
    }

    public void setSource2(CompoundSourceL1 src2)
    {
        this.source2 = src2;
    }

    public static class Id implements Serializable
    {
        private static final long serialVersionUID = 5411898841429793480L;
        public CompoundSourceL4.Id source;
        public CompoundSourceL1.Id source2;

        public Id()
        {
        }

        public Id(java.lang.String str)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer(str, "::");
            this.source = new CompoundSourceL4.Id(token.nextToken());
            this.source2 = new CompoundSourceL1.Id(token.nextToken());
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.source) + "::";
            str += java.lang.String.valueOf(this.source2) + "::";
            return str;
        }

        public int hashCode()
        {
            return source.hashCode() ^ source2.hashCode();
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
            return ((this.source == null ? objToCompare.source == null : 
                this.source.equals(objToCompare.source)) &&
                (this.source2 == null ? objToCompare.source2 == null : this.source2.equals(objToCompare.source2)));
        }
    }
}