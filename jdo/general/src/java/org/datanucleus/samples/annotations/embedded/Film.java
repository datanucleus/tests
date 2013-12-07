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

import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a film, using JDO annotations.
 */
@PersistenceCapable(detachable="true", embeddedOnly="true")
@FetchGroup(name="film_all",members={@Persistent(name="name"), @Persistent(name="director"), @Persistent(name="description")})
public class Film
{
    @Persistent
    private String name;

    @Persistent
    private String director;

    @Persistent
    private String description;

    public Film(String name, String director, String description)
    {
        this.name = name;
        this.director = director;
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDirector()
    {
        return director;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name + " directed by " + director;
    }
}