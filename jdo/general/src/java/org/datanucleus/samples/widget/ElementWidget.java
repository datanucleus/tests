/*
 * The terms of the JPOX License are distributed with the software documentation
 */
package org.datanucleus.samples.widget;

import javax.jdo.JDOHelper;


public class ElementWidget extends Widget
{
    private SetWidget owner;


    /**
     * Default constructor required since this is a PersistenceCapable class.
     */
    protected ElementWidget() {}

    public ElementWidget(SetWidget owner)
    {
        super();

        this.owner = owner;
    }


    public SetWidget getOwner()
    {
        return owner;
    }


    public void setOwner(SetWidget owner)
    {
        this.owner = owner;
    }


    /**
     * Indicates whether some other object is "equal to" this one.  By comparing
     * against an original copy of the object, <code>compareTo()</code> can be
     * used to verify that the object has been written to a database and read
     * back correctly.
     *
     * @param   obj     the reference object with which to compare
     *
     * @return  <code>true</code> if this object is equal to the obj argument;
     *          <code>false</code> otherwise.
     */

    public boolean compareTo(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof ElementWidget) || !super.compareTo(obj))
            return false;
        else
            return true;
    }


    /**
     * Returns a string representation for this object.  All of the field
     * values are included in the string for debugging purposes.
     *
     * @return  a string representation for this object.
     */

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());

        s.append("  owner = ").append(JDOHelper.getObjectId(owner));
        s.append('\n');

        return s.toString();
    }
}
