/**********************************************************************
Copyright (c) 2006 Rick Moore and others. All rights reserved. 
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

/**
 * Example user-provided target class with 1-1 uni relation.
 *
 * @version $Revision: 1.1 $
 */
public class CompoundTarget1
{
    private CompoundSource1 source; // PK

    private String zipPlusFourAttr;

    public String getZipPlusFourAttr()
    {
        return zipPlusFourAttr;
    }

    public void setZipPlusFourAttr(String inVal)
    {
        zipPlusFourAttr = inVal;
    }

    public CompoundSource1 getSource()
    {
        return source;
    }

    public synchronized void setSource(CompoundSource1 inVal)
    {
        source = inVal;
    }

    public synchronized void addSource(CompoundSource1 inVal)
    {
        setSource(inVal);
    }

    public synchronized void clearAddressAssoc()
    {
        setSource(null);
    }

    public synchronized void removeAddressAssoc(CompoundSource1 inVal)
    {
        clearAddressAssoc();
    }

    public synchronized void clearAllAssocs()
    {
        clearAddressAssoc();
    }

    public static class Id implements java.io.Serializable
    {
        private static final long serialVersionUID = 7640849414291159466L;
        public CompoundSource1.Id source;

        public Id()
        {
        }

        public Id(String astrValue)
        {
            java.util.StringTokenizer lST = new java.util.StringTokenizer(astrValue, "::");
            this.source = new CompoundSource1.Id(lST.nextToken());
        }

        public boolean equals(Object aObject)
        {
            if (aObject == null)
            {
                return false;
            }

            if (aObject == this)
            {
                return true;
            }

            if (!(aObject instanceof Id))
            {
                return false;
            }
            Id lAddressExtensionCDOCcKey = (Id) aObject;
            if (source == null || lAddressExtensionCDOCcKey.source == null)
            {
                return false;
            }
            return source.equals(lAddressExtensionCDOCcKey.source);
        }

        public int hashCode()
        {
            if (source == null)
            {
                return super.hashCode();
            }
            return this.source.hashCode();
        }

        public String toString()
        {
            StringBuffer lSB = new StringBuffer();
            if (source == null)
            {
                lSB.append("null");
            }
            else
            {
                lSB.append(this.source.toString());
            }

            return lSB.toString();
        }
    }
}