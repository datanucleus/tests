package org.datanucleus.samples.models.inheritance_mapped_collection;

import java.util.HashSet;
import java.util.Set;

public class ContainerInheritanceSub extends ContainerInheritanceRoot
{
    private static final long serialVersionUID = 2388513150003011745L;
    private Set es;
    private ElementE e;

	protected ContainerInheritanceSub()
    {
    }

    public ContainerInheritanceSub(String organisationID, String bbbID)
    {
        super(organisationID, bbbID);
        this.es = new HashSet();
    }

    public Set getEs()
    {
        return es;
    }

    public ElementE getE()
    {
        return e;
    }

    public void setE(ElementE e)
    {
        this.e = e;
    }
}