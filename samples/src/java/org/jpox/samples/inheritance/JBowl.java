/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others.
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
package org.jpox.samples.inheritance;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for inheritance tests - sample "J".
 * This sample has 2 subclasses - one a Container, and one an Element. The Element has 2 subclasses.
 * @version $Revision: 1.1 $
 */
public class JBowl extends JBase
{
    private String name;

    private HashSet spottedMarbles;

    private HashSet transparentMarbles;

    public JBowl()
    {
        spottedMarbles = new HashSet();
        transparentMarbles = new HashSet();
    }

    public JBowl(final String name)
    {
        setName(name);
        spottedMarbles = new HashSet();
        transparentMarbles = new HashSet();
    }

    public JBowl(int id,final String name)
    {
        setId(new Integer(id));
        setName(name);
        spottedMarbles = new HashSet();
        transparentMarbles = new HashSet();
    }

    public void addMarble(JMarble marble)
    {
        if (marble instanceof JSpottedMarble)
        {
            spottedMarbles.add(marble);
        }
        else if (marble instanceof JTransparentMarble)
        {
            transparentMarbles.add(marble);
        }
    }

    public int getNumberOfSpottedMarbles()
    {
        return spottedMarbles.size();
    }

    public Set getSpottedMarbles()
    {
        return spottedMarbles;
    }

    public int getNumberOfTransparentMarbles()
    {
        return transparentMarbles.size();
    }

    public Set getTransparentMarbles()
    {
        return transparentMarbles;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return getName();
    }
}