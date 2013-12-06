/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.embedded;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a film library.
 * 
 * @version $Revision: 1.1 $
 */
public class FilmLibrary
{
    private long id; // Used by application identity
    private String owner;
    private Map films = new HashMap();

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
        return (Film)films.get(alias);
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