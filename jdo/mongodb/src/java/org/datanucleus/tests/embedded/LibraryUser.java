package org.datanucleus.tests.embedded;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class LibraryUser
{
    @PrimaryKey
    String name;

    Set<ViewedVideo> viewedVideos = new HashSet<ViewedVideo>();

    public LibraryUser()
    {
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public Set<ViewedVideo> getViewedVideos() 
    {
        return viewedVideos;
    }
}
