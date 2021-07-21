/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others.
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
package org.datanucleus.samples.models.fitness;

/**
 * Piece of equipment in a Gym.
 */
public class GymEquipment
{
    String name;
    private Gym gym;
    private String stringKey;
    private String stringValue;

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public void setGym(Gym gym)
    {
        this.gym = gym;
    }

    public Gym getGym()
    {
        return gym;
    }

    public String getStringKey()
    {
        return stringKey;
    }
    
    public String getStringValue()
    {
        return stringValue;
    }
}