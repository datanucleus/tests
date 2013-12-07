package org.datanucleus.samples.jfire.organisation;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 */
public class JFireOrganisation implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String organisationID;
    private Date createDT;
    private Date changeDT;

    protected JFireOrganisation()
    {
    }

    public JFireOrganisation(String _organisationID)
    {
        this.organisationID = _organisationID;
        this.createDT = new Date();
        this.changeDT = new Date();
    }

    public String getOrganisationID()
    {
        return organisationID;
    }

    public Date getCreateDT()
    {
        return createDT;
    }

    public Date getChangeDT()
    {
        return changeDT;
    }

    public void setChangeDT(Date changeDT)
    {
        this.changeDT = changeDT;
    }

    public void setChangeDT()
    {
        this.changeDT = new Date();
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((organisationID == null) ? 0 : organisationID.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final JFireOrganisation other = (JFireOrganisation) obj;
        if (this.organisationID == null)
            return other.organisationID == null;
        return this.organisationID.equals(other.organisationID);
    }

    public String toString()
    {
        return this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + organisationID + ']';
    }
}