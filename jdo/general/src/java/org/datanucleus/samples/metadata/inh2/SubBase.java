package org.datanucleus.samples.metadata.inh2;

public class SubBase extends Base
{
    String extra;

    public SubBase(int id, String desc, String extra)
    {
        super(id, desc);
        this.extra = extra;
    }

    public void clear()
    {
    }

    public String toString()
    {
        return "S" + super.toString(); //$NON-NLS-1$
    }
}