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
package org.datanucleus.samples.jgeometry;

import java.util.Arrays;

import oracle.spatial.geometry.JGeometry;

public class SampleGeometryM
{

    private long id;

    private String name;

    private JGeometry geom;

    public SampleGeometryM(long id, String name, JGeometry geom)
    {
        this.id = id;
        this.name = name;
        this.geom = geom;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public JGeometry getGeom()
    {
        return geom;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof SampleGeometryM))
            return false;

        SampleGeometryM other = (SampleGeometryM) obj;
        if (!(id == other.id))
            return false;
        if (!(name == null ? other.name == null : name.equals(other.name)))
            return false;
        if (geom == null && other.geom == null)
            return true;
        if (!(geom.getType() == other.geom.getType()))
            return false;
        if (!(geom.getDimensions() == other.geom.getDimensions()))
            return false;
        if (!(geom.getSRID() == other.geom.getSRID()))
            return false;
        if (geom.getElemInfo() != null)
        {
            if (!(Arrays.equals(geom.getElemInfo(), other.geom.getElemInfo())))
                return false;
            return (Arrays.equals(geom.getOrdinatesArray(), other.geom.getOrdinatesArray()));
        }
        else
        {
            return (geom.getPoint() == null ? other.geom.getPoint() == null : Arrays.equals(geom.getPoint(), other.geom.getPoint()));
        }
    }

    public String toString()
    {
        return "id = " + id + " / name = " + name + " / geom = " + (geom == null ? "null" : geom.toString());
    }

}
