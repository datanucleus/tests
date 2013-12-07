/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others.
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

import java.io.Serializable;

/**
 * @version $Revision: 1.1 $
 */
public class FieldAccess
{
    public String thisField;
    public int id;
    
    public String getFieldValueNav1(DataHolder data)
    {
        this.thisField = "";
        return data.temp;
    }
    
    public String getFieldValueNav2(DataHolder data)
    {
        this.thisField = "";
        String text = data.temp;
        return text;
    }

    public String getFieldValueNav3(DataHolder data)
    {
        this.thisField = "";
        this.thisField = data.temp;
        return this.thisField;
    }
    
    public static int getFieldValueNav4(Id id)
    {
        return id.id;
    }
    
    public static int getFieldValueNav5(Id id)
    {
        return id.getId().id;
    }

    public static int getFieldValueNav6(Id id)
    {
        int oldId = id.getId().id;
        id.setId(-1);
        return id.setId(oldId).getId().id;
    }
    
    public static int getFieldValueNav7(FieldAccess fa, Id id)
    {
        fa.id = -1;
        fa.id = id.id;
        return fa.id;
    }
    
    public int getFieldValueNav8(Id id)
    {
        this.id = -1;
        return id.id;
    }    

    public int getFieldValueNav9(Id id)
    {
        this.id = -1;
        this.id = id.id;
        return this.id;
    }    

    public int getFieldValueNav10(Id id)
    {
        this.id = -1;
        this.id = id.id;
        return getThis().id;
    }     

    public int getFieldValueNav11(Id id)
    {
        this.id = -1;
        this.id = id.id;
        return this.getThis().id;
    }     
    
    public int getFieldValueNav12(Id id2)
    {
        this.id = -1;
        this.id = id2.id;
        return id;
    }
    
    public static int getX()
    {
        return 0;
    }
    
    public static void setX(int x)
    {
        //do nothing
    }
    
    public static long test()
    {
        setX(getX()+1);
        return getX();
    }
    public int getFieldValueNav13(Id id2)
    {
        this.id = -1;
        this.id = id2.getId().id;
        return id;
    }    
    
    public int getFieldValueNav14(Id id2)
    {
        this.id = -1;
        this.id = id2.getId(id2).id;
        return id;
    }    
    
    public int getFieldValueNav15(Id id)
    {
        return new Integer(new Integer(id.id).toString()).intValue();
    }  
    
    public int getFieldValueNav16(Id id)
    {
        this.id = id.id;
        return new Integer(new Integer(this.id).toString()).intValue();
    }    
    
	public synchronized long getFieldValueNav17()
	{
		id += 1;
		return id;
	}
	public synchronized long getFieldValueNav18()
	{
		return ++id;
	}

    private FieldAccess getThis()
    {
        return this;
    }
    
    /**
     * used for holding data and checking enhancement with objects navigating
     * @version $Revision: 1.1 $
     */
    public static class DataHolder
    {
        public String temp;
    }
    
    /**
     * @version $Revision: 1.1 $
     */
    public static class Id implements Serializable
    {

        public int id;

        public Id()
        {
            //default constructor
        }

        public Id(java.lang.String str)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer(str, "::");
            this.id = new java.lang.Integer(token.nextToken()).intValue();
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.id);
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
            Id objToCompare = (Id) o;
            return ((this.id == objToCompare.id));
        }
        
        public Id getId()
        {
            return this;
        }
        
        public Id getId(Id id)
        {
            return this;
        }     
        
        /**
         * @param id The id to set.
         */
        public Id setId(int id)
        {
            this.id = id;
            return this;
        }
    }
    
}
