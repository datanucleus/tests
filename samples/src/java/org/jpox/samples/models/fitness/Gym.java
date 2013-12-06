/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.models.fitness;

import java.util.HashMap;
import java.util.Map;

/**
 * Gymnasium.
 * @version $Revision: 1.1 $
 */
public class Gym
{
    private Map codes;
    private String location;
    private String name;

    //this must be initialized in the constructor. dont change it
    private Map wardrobes; // store Wardrobe in values
    private Map wardrobes2; // store Wardrobe in keys
    private Map wardrobesInverse; // store Wardrobe in values
    private Map wardrobesInverse2; // store Wardrobe in keys

    private Map equipments; // store Equipments in values
    private Map equipments2; // store Equipments in keys
    private Map equipmentsInverse; // store Equipments in values
    private Map equipmentsInverse2; // store Equipments in keys

    private Map partners; // store Gym in values
    private Map partners2; // store Gym in keys
    private Map partnersInverse; // store Gym in values
    private Map partnersInverse2; // store Gym in keys

    private Gym gym;
    private Gym gym2;
    private String stringKey;
    private String stringValue;

    public Gym()
    {
        //this must be initialized in the constructor. dont change it
        wardrobes = new HashMap();
        equipments = new HashMap();
        partners = new HashMap();
        wardrobes2 = new HashMap();
        equipments2 = new HashMap();
        partners2 = new HashMap();
        wardrobesInverse = new HashMap();
        equipmentsInverse = new HashMap();
        partnersInverse = new HashMap();
        wardrobesInverse2 = new HashMap();
        equipmentsInverse2 = new HashMap();
        partnersInverse2 = new HashMap();
        codes = new HashMap();
    }
    
    /**
     * @return Returns the location.
     */
    public String getLocation()
    {
        return location;
    }
    /**
     * @param location The location to set.
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    /**
     * @return Returns the wardrobes.
     */
    public Map getWardrobes()
    {
        return wardrobes;
    }
    /**
     * @param wardrobes The wardrobes to set.
     */
    public void setWardrobes(Map wardrobes)
    {
        this.wardrobes = wardrobes;
    }
    
    public Map getEquipments()
    {
        return equipments;
    }
    
    public void setEquipments(Map equipments)
    {
        this.equipments = equipments;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setPartners(Map partners)
    {
        this.partners = partners;
    }
    
    public Map getPartners()
    {
        return partners;
    }
    
    public Map getPartners2()
    {
        return partners2;
    }
    
    public Map getEquipments2()
    {
        return equipments2;
    }
    
    public Map getWardrobes2()
    {
        return wardrobes2;
    }
    
    public Map getWardrobesInverse()
    {
        return wardrobesInverse;
    }
    public Map getWardrobesInverse2()
    {
        return wardrobesInverse2;
    }
    
    public Map getEquipmentsInverse()
    {
        return equipmentsInverse;
    }
    
    public Map getEquipmentsInverse2()
    {
        return equipmentsInverse2;
    }
    
    public Map getPartnersInverse()
    {
        return partnersInverse;
    }
    
    public Map getPartnersInverse2()
    {
        return partnersInverse2;
    }
    
    public void setGym(Gym gym)
    {
        this.gym = gym;
    }
    public Gym getGym()
    {
        return gym;
    }

    public void setGym2(Gym gym)
    {
        this.gym2 = gym;
    }
    public Gym getGym2()
    {
        return gym2;
    }    

    public String getStringKey()
    {
        return stringKey;
    }
    
    public String getStringValue()
    {
        return stringValue;
    }
    
    public Map getCodes()
    {
        return codes;
    }
    
    public void setCodes(Map codes)
    {
        this.codes = codes;
    }
}