/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.annotations.one_one.bidir;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Representation of a heating system Timer.
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_TIMER")
@Inheritance(strategy=InheritanceType.JOINED)
public class Timer extends Equipment
{
    @Basic
    @Column(name="DIGITAL")
    private boolean digital=false;

    @OneToOne(cascade=CascadeType.ALL)
    @Column(name="BOILER_ID")
    private Boiler boiler;

    public Timer(String make, boolean digital, Boiler boiler)
    {
        super(make);
        this.digital = digital;
        this.boiler = boiler;
    }

    public Boiler getBoiler()
    {
        return boiler;
    }

    public boolean isDigital()
    {
        return digital;
    }

    public void setDigital(boolean digital)
    {
        this.digital = digital;
    }

    public void setBoiler(Boiler boiler)
    {
        this.boiler = boiler;
    }
}