/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.identity.application;

import java.io.Serializable;

/**
 * No idea WHAT this class name means. What is "unordered" here ?
 *
 * @version $Revision: 1.1 $
 */
public class UnorderedPrimaryKeyFields
{
    // persistent member variables
    private String firstField;
    private int id;
    private String mediumField;
    private String composed;
    private String lastField;

    public UnorderedPrimaryKeyFields()
    {
    }

    public UnorderedPrimaryKeyFields(int id,String composed,String firstField,String mediumField, String lastField)
    {
        this.id = id;
        this.composed = composed;
        this.firstField = firstField;
        this.mediumField = mediumField;
        this.lastField = lastField;
    }

    public boolean equals(Object o)
    {
        if (o == null || !o.getClass().equals(this.getClass()))
        {
            return false;
        }
        UnorderedPrimaryKeyFields trs = (UnorderedPrimaryKeyFields)o;
        return id == trs.id && composed.equals(trs.composed) && firstField.equals(trs.firstField) && mediumField.equals(trs.mediumField) && lastField.equals(trs.lastField);
    }

    public String toString()
    {
        return "{UnorderedPrimaryKeyFields firstField=" + firstField + "; mediumField=" + mediumField + "; lastField=" + lastField + "}";
    }

    public static class Oid implements Serializable
    {
        public int id;
        public String composed;
        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer toke = new java.util.StringTokenizer (s, "::");
            //ignore first token
            s = toke.nextToken ();
            s = toke.nextToken ();
            this.id = Integer.valueOf(s).intValue();
            s = toke.nextToken ();
            this.composed = s;
        }

        public String toString()
        {
            return this.getClass().getName() + "::"  + id + "::" + composed;
        }

        public int hashCode()
        {
            return id ^ composed.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.id == this.id && k.composed.equals(this.composed);
            }
            return false;
        }
    }    

    /**
     * @return Returns the composed.
     */
    public String getComposed()
    {
        return composed;
    }

    /**
     * @param composed The composed to set.
     */
    public void setComposed(String composed)
    {
        this.composed = composed;
    }

    /**
     * @return Returns the firstField.
     */
    public String getFirstField()
    {
        return firstField;
    }

    /**
     * @param firstField The firstField to set.
     */
    public void setFirstField(String firstField)
    {
        this.firstField = firstField;
    }

    /**
     * @return Returns the id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return Returns the lastField.
     */
    public String getLastField()
    {
        return lastField;
    }

    /**
     * @param lastField The lastField to set.
     */
    public void setLastField(String lastField)
    {
        this.lastField = lastField;
    }

    /**
     * @return Returns the mediumField.
     */
    public String getMediumField()
    {
        return mediumField;
    }

    /**
     * @param mediumField The mediumField to set.
     */
    public void setMediumField(String mediumField)
    {
        this.mediumField = mediumField;
    }
}
