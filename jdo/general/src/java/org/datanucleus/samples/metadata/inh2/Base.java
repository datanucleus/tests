package org.datanucleus.samples.metadata.inh2;

import java.io.Serializable;

public class Base
{
    private int id;
    private String desc;

    protected Base()
    {
    }

    public Base(int id, String desc)
    {
        this.id = id;
        this.desc = desc;
    }

    public String toString()
    {
        return "B:" + Integer.toString(id);
    }
    
    
    /**
     * @return Returns the desc.
     */
    public String getDesc()
    {
        return desc;
    }
    /**
     * @param desc The desc to set.
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
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
    public static class BaseId implements Serializable
    {
        public int id;

        public BaseId()
        {
        }

        public BaseId(String id)
        {
            this.id = Integer.valueOf(id).intValue();
        }

        public boolean equals(Object obj)
        {
            return (obj instanceof BaseId) && (id == ((BaseId) obj).id);
        }

        public int hashCode()
        {
            return new Integer(id).hashCode();
        }

        public String toString()
        {
            return Integer.toString(id);
        }
    }
}