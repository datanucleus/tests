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
package org.datanucleus.samples.annotations.embedded;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a Music Player (eg MP3).
 */
@PersistenceCapable(detachable="true", table="JDO_AN_MUSIC_PLAYER")
@FetchGroup(name="fetch-battery", members=@Persistent(name="battery"))
public class MusicPlayer
{
    @NotPersistent
    long id; // Used by application identity

    String make;
    String model;

    @Persistent
    @Embedded(nullIndicatorColumn="BATTERY_MAKE", members={
            @Persistent(name="make", column="BATTERY_MAKE"),
            @Persistent(name="lifetime", column="BATTERY_LIFETIME")})
    Battery battery;

    public MusicPlayer(String make, String model, Battery battery)
    {
        this.make = make;
        this.model = model;
        this.battery = battery;
    }

    public long getId()
    {
        return id;
    }

    public Battery getBattery()
    {
        return battery;
    }

    public String getMake()
    {
        return make;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setBattery(Battery battery)
    {
        this.battery = battery;
    }

    public String toString()
    {
        return "MusicPlayer : " + make + " " + model + " " + battery;
    }
}