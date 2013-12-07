/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests.directory.embedded;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractComputer
{
    private String name;

    private String serialNumber;

    private Set<ComputerCard> cards = new HashSet<ComputerCard>();

    private OperatingSystem operatingSystem;

    public AbstractComputer()
    {
    }

    public AbstractComputer(String serialNumber, String name)
    {
        this.serialNumber = serialNumber;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public Set<ComputerCard> getCards()
    {
        return cards;
    }

    public void setCards(Set<ComputerCard> cards)
    {
        this.cards = cards;
    }

    public OperatingSystem getOperatingSystem()
    {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem)
    {
        this.operatingSystem = operatingSystem;
    }

}
