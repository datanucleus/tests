/**********************************************************************
Copyright (c) 2014 Baris ERGUN and others. All rights reserved.
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
 
 ***********************************************************************/
package org.datanucleus.samples.jdo.cassandra;

import java.util.*;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Playlist
{

    @PrimaryKey
    private UUID id;

    @PrimaryKey
    @Column(name = "song_order")
    private int songOrder;

    @Persistent
    @Column(name = "song_id")
    private UUID songId;

    private String songTitle;

    private String albumName;

    private String artistName;

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public int getSongOrder()
    {
        return songOrder;
    }

    public void setSongOrder(int songOrder)
    {
        this.songOrder = songOrder;
    }

    public UUID getSongId()
    {
        return songId;
    }

    public void setSongId(UUID songId)
    {
        this.songId = songId;
    }

    public String getSongTitle()
    {
        return songTitle;
    }

    public void setSongTitle(String title)
    {
        this.songTitle = title;
    }

    public String getAlbumName()
    {
        return albumName;
    }

    public void setAlbumName(String album)
    {
        this.albumName = album;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public void setArtistName(String artist)
    {
        this.artistName = artist;
    }

}
