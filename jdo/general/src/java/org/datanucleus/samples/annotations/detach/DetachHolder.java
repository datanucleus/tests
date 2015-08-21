package org.datanucleus.samples.annotations.detach;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable="true")
public class DetachHolder
{
    @Extension(vendorName="DataNucleus", key="attach", value="never")
    private Date dateNeverAttach;
    
    @Extension(vendorName="DataNucleus", key="attach", value="never")
    private DetachPC pcNeverAttach;
    
    @Persistent(dependent="true")
    private DetachPC pcDependent;
    
    private DetachPC pc;

    public Date getDateNeverAttach()
    {
        return dateNeverAttach;
    }

    public void setDateNeverAttach(Date dateNeverAttach)
    {
        this.dateNeverAttach = dateNeverAttach;
    }

    public DetachPC getPcNeverAttach()
    {
        return pcNeverAttach;
    }

    public void setPcNeverAttach(DetachPC pcNeverAttach)
    {
        this.pcNeverAttach = pcNeverAttach;
    }
    
    public DetachPC getPcDependent()
    {
        return pcDependent;
    }
    
    public void setPcDependent(DetachPC pcDependent)
    {
        this.pcDependent = pcDependent;
    }

    public DetachPC getPc()
    {
        return pc;
    }

    public void setPc(DetachPC pc)
    {
        this.pc = pc;
    }
}
