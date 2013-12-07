/**********************************************************************
 Copyright (c) 2006 Thomas Marti, Stefan Schmid and others. All rights reserved.
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
package org.datanucleus.samples.pggeometry;

import org.postgis.PGbox2d;
import org.postgis.PGbox3d;

public class SampleBox
{

    private long id;

    private String name;

    private PGbox2d pgbox2d;

    private PGbox3d pgbox3d;

    public SampleBox(long id, String name, PGbox2d pgbox2d, PGbox3d pgbox3d)
    {
        this.id = id;
        this.name = name;
        this.pgbox2d = pgbox2d;
        this.pgbox3d = pgbox3d;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public PGbox2d getPgbox2d()
    {
        return pgbox2d;
    }

    public PGbox3d getPgbox3d()
    {
        return pgbox3d;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof SampleBox))
            return false;

        SampleBox other = (SampleBox) obj;
        if (!(id == other.id))
            return false;
        if (!(name == null ? other.name == null : name.equals(other.name)))
            return false;
        return (pgbox2d == null ? other.pgbox2d == null : pgbox2d.equals(other.pgbox2d)) && (pgbox3d == null ? other.pgbox3d == null : pgbox3d
                .equals(other.pgbox3d));

    }

    public String toString()
    {
        return "id = " + id + " / name = " + name + " / pgbox2d = " + (pgbox2d == null ? "null" : pgbox2d.toString()) + " / pgbox3d = " + (pgbox3d == null ? "null" : pgbox3d
                .toString());
    }

}
