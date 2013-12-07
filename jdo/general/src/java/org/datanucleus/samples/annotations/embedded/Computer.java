/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved. 
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
package org.datanucleus.samples.annotations.embedded;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a Computer.
 */
@PersistenceCapable(table="JDO_AN_COMPUTER")
public class Computer
{
    @Persistent(persistenceModifier=PersistenceModifier.NONE)
    private long id; // Used by application identity

    @Persistent
    @Column(name="OPERATING_SYSTEM")
    private String operatingSystem;

    @Persistent(defaultFetchGroup="true")
    @Embedded(nullIndicatorColumn="GRAPHICS_MANUFACTURER", ownerMember="computer",
        members={
            @Persistent(name="manufacturer", columns={@Column(name="GRAPHICS_MANUFACTURER")}),
            @Persistent(name="type", columns={@Column(name="GRAPHICS_TYPE", allowsNull="true")})
        })
    private ComputerCard graphicsCard;

    @Persistent(defaultFetchGroup="true")
    @Embedded(nullIndicatorColumn="SOUND_MANUFACTURER", ownerMember="computer",
        members={
            @Persistent(name="manufacturer", columns={@Column(name="SOUND_MANUFACTURER")}),
            @Persistent(name="type", columns={@Column(name="SOUND_TYPE", allowsNull="true")})
        })
    private ComputerCard soundCard;

    public Computer(String osName, ComputerCard graphics, ComputerCard sound)
    {
        this.operatingSystem = osName;
        this.graphicsCard = graphics;
        this.soundCard = sound;
    }

    public long getId()
    {
        return id;
    }

    public String getOperatingSystem()
    {
        return operatingSystem;
    }

    public ComputerCard getGraphicsCard()
    {
        return graphicsCard;
    }

    public ComputerCard getSoundCard()
    {
        return soundCard;
    }

    public void setOperatingSystem(String os)
    {
        this.operatingSystem = os;
    }

    public void setGraphicsCard(ComputerCard graphics)
    {
        this.graphicsCard = graphics;
    }

    public void setSoundCard(ComputerCard sound)
    {
        this.soundCard = sound;
    }
}