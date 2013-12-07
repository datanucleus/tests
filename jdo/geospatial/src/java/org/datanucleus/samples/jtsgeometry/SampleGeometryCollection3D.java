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
package org.datanucleus.samples.jtsgeometry;

import com.vividsolutions.jts.geom.GeometryCollection;

public class SampleGeometryCollection3D
{

    private long id;

    private String name;

    private GeometryCollection geom;

    public SampleGeometryCollection3D(long id, String name, GeometryCollection collection)
    {
        this.id = id;
        this.name = name;
        this.geom = collection;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public GeometryCollection getGeom()
    {
        return geom;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof SampleGeometryCollection3D))
            return false;

        SampleGeometryCollection3D other = (SampleGeometryCollection3D) obj;
        if (!(id == other.id))
            return false;
        if (!(name == null ? other.name == null : name.equals(other.name)))
            return false;
        geom.normalize();
        other.geom.normalize();
        return (geom == null ? other.geom == null : geom.equalsExact(other.geom));
    }

    public String toString()
    {
        return "id = " + id + " / name = " + name + " / geom = " + (geom == null ? "null" : geom.toString());
    }

}
