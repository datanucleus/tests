package org.datanucleus.samples.metadata.inh2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Container
{
    Collection members = new ArrayList();

    public Container(Base[] members)
    {
        for (int i = 0; i < members.length; i++)
        {
            this.members.add(members[i]);
        }
    }

    public void clear()
    {
        for (Iterator iter = members.iterator(); iter.hasNext();)
        {
            iter.next();
            iter.remove();
        }
    }

    /**
     * @return Returns the members.
     */
    public Collection getMembers()
    {
        return members;
    }
    /**
     * @param members The members to set.
     */
    public void setMembers(Collection members)
    {
        this.members = members;
    }
    public String toString()
    {
        StringBuffer sb = new StringBuffer("C:");
        sb.append(super.toString());
        sb.append('[');
        for (Iterator iter = members.iterator(); iter.hasNext();)
        {
            Base element = (Base) iter.next();
            sb.append(element.toString());
            sb.append(';');
        }
        sb.append(']');
        return sb.toString();
    }
}