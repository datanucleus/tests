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
package org.jpox.samples.identity.application;

import java.io.Serializable;

/**
 * @version $Revision: 1.2 $
 */
public class Car
{
    String ownerID; // PK
    String carID; // PK

    String model;
    Car towedCar;

    public Car(String carID, String ownerID)
    {
        super();
        this.carID = carID;
        this.ownerID = ownerID;
    }

    public String getCarID()
    {
        return carID;
    }

    public void setCarID(String carID)
    {
        this.carID = carID;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getOwnerID()
    {
        return ownerID;
    }

    public void setOwnerID(String ownerID)
    {
        this.ownerID = ownerID;
    }

    public Car getTowedCar()
    {
        return towedCar;
    }

    public void setTowedCar(Car towedCar)
    {
        this.towedCar = towedCar;
    }

    public static class Id implements Serializable
    {
        public String ownerID;
        public String carID;

        public Id()
        {
        }

        public Id(java.lang.String str)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer(str, "::");
            this.ownerID = new String(token.nextToken());
            this.carID = new String(token.nextToken());
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.ownerID) + "::";
            str += java.lang.String.valueOf(this.carID);
            return str;
        }

        public int hashCode()
        {
            return ownerID.hashCode() ^ carID.hashCode();
        }

        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null)
            {
                return false;
            }
            if (o.getClass() != getClass())
            {
                return false;
            }
            Id objToCompare = (Id) o;
            return ((this.ownerID == null ? objToCompare.ownerID == null : this.ownerID.equals(objToCompare.ownerID)) && 
                    (this.carID == null ? objToCompare.carID == null : this.carID.equals(objToCompare.carID)));
        }
    }
}