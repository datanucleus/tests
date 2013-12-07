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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Key;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Value;

/**
 * Representation of a film library, using JDO annotations.
 */
@PersistenceCapable(detachable="true", table="JDO_AN_FILM_LIBRARY")
@FetchGroup(name="film_all",members={@Persistent(name="owner"), @Persistent(name="films")})
public class FilmLibrary
{
    @NotPersistent
    private long id; // Used by application identity

    @Persistent
    @Column(name="OWNER", length=40, jdbcType="VARCHAR")
    private String owner;

    @Persistent(table="JDO_AN_FILM_LIBRARY_FILMS")
    @Key(types=java.lang.String.class, column="FILM_ALIAS")
    @Value(types=Film.class,
        embeddedMapping=@Embedded(nullIndicatorColumn="FILM_NAME",
            members={
                @Persistent(name="name", column="FILM_NAME"),
                @Persistent(name="director", columns=@Column(name="FILM_DIRECTOR", allowsNull="true"))}))
    @Join(column="FILM_LIBRARY_ID")
    private Map<String, Film> films = new HashMap<String, Film>();

    public FilmLibrary(String owner)
    {
        this.owner = owner;
    }

    public long getId()
    {
        return id;
    }

    public String getOwner()
    {
        return owner;
    }

    public void addFilm(String alias, Film film)
    {
        films.put(alias, film);
    }

    public void removeFilm(String alias)
    {
        films.remove(alias);
    }

    public Film getFilm(String alias)
    {
        return films.get(alias);
    }

    public int getNumberOfFilms()
    {
        return films.size();
    }

    public boolean containsFilm(String alias)
    {
        return (getFilm(alias) != null);
    }

    public Collection getFilms()
    {
        return films.values();
    }

    public String toString()
    {
        return "FilmLibrary of " + owner + " (" + films.size() + " films)";
    }
}