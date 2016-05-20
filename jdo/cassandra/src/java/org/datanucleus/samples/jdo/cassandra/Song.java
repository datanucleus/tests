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

import java.util.UUID;
import javax.jdo.annotations.*;

@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class Song
{
    @PrimaryKey
    private UUID id;

    @Column(name = "song_title")
    private String songTitle;

    private String albumName;

    private String artistName;

    @Persistent(defaultFetchGroup = "true")
    @Serialized
    private byte[] albumImage;

    public UUID getId()
    {
        return id;
    }
    public void setId(UUID id)
    {
        this.id = id;
    }

    public String getSongTitle()
    {
        return songTitle;
    }
    public void setSongTitle(String songTitle)
    {
        this.songTitle = songTitle;
    }

    public String getAlbumName()
    {
        return albumName;
    }
    public void setAlbumName(String albumName)
    {
        this.albumName = albumName;
    }

    public String getArtistName()
    {
        return artistName;
    }
    public void setArtistName(String artistName)
    {
        this.artistName = artistName;
    }

    public byte[] getAlbumImage()
    {
        return albumImage;
    }
    public void setAlbumImage(byte[] albumImage)
    {
        this.albumImage = albumImage;
    }
}