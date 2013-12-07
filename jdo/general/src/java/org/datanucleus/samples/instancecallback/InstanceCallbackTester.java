/*
 * The terms of the JPOX License are distributed with the software documentation.
 */
package org.datanucleus.samples.instancecallback;

import javax.jdo.InstanceCallbacks;


public class InstanceCallbackTester implements InstanceCallbacks
{
    private String persistentValue;
    private String transientValue;
    public static final String POST_LOAD_VALUE="loaded";

    public void setTransientValue(String s)
    {
        this.transientValue = s;
    }

    public String getTransientValue()
    {
        return this.transientValue;
    }

    public void setPersistentValue(String s)
    {
        this.persistentValue = s;
    }

    public String getPersistentValue()
    {
        return this.persistentValue;
    }

    public void jdoPostLoad()
    {
        setTransientValue(POST_LOAD_VALUE);
    }

    public void jdoPreStore()
    {
        setPersistentValue(getTransientValue());
    }

    public void jdoPreClear()
    {
    }

    public void jdoPreDelete()
    {
        if (this.transientValue == null)
            throw new PreDeleteException();
    }
}

