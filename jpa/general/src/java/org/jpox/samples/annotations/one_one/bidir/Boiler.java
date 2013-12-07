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

import javax.jdo.annotations.Column;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Representation of a Boiler in a heating system
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name="JPA_AN_BOILER")
@Inheritance(strategy=InheritanceType.JOINED)
public class Boiler extends Equipment
{
    @Basic
    @Column(name="MODEL")
    private String model;

    @OneToOne(mappedBy="boiler", cascade=CascadeType.ALL)
    private Timer timer;

    public Boiler(String make, String model)
    {
        super(make);
        this.model = model;
    }

    public void setTimer(Timer timer)
    {
        this.timer = timer;
    }

    public Timer getTimer()
    {
        return timer;
    }

    /**
     * @return Returns the model.
     */
    public String getModel()
    {
        return model;
    }

    /**
     * @param model The model to set.
     */
    public void setModel(String model)
    {
        this.model = model;
    }
}