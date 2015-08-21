package org.datanucleus.samples.annotations.detach;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(detachable="true")
public class DetachPC
{
    private String value;
    
    public DetachPC(String value)
    {
        super();
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
