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

package org.datanucleus.tests;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.jdo.*;
import org.datanucleus.samples.jdo.cassandra.*;
import static org.datanucleus.tests.JDOPersistenceTestCase.pmf;

public class SampleCassandraData
{
    // TODO wasted sometime to create a resources directory for test sources but
    // gave up and used
    // main resources directory for storing image file so replacing the
    // test-classes with classes
    // in the current classloaders path

    public static final String CLASSES_TARGET_DIRECTORY_PATH = SampleCassandraData.class.getResource("/").getPath()
            .replace("test-classes", "classes");

    public static List<UUID> SONG_ID_LIST = new LinkedList<UUID>();

    public static final void loadData() throws IOException
    {
        cleanupTables();

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            createSampleSongsInAPlayList(pm);

        }
        finally
        {
            tx.commit();
            pm.close();
        }
    }

    public static final String ARTIST_NAME = "Depeche Mode";

    public static final String ALBUM_NAME = "Delta Machine";

    public static final String TITLE_1 = "Welcome to My World";

    public static final String TITLE_2 = "Angel";

    public static final String TITLE_3 = "Heaven";

    public static final String TITLE_4 = "Secret to the End";

    public static final String TITLE_5 = "My Little Universe";

    public static final String TITLE_6 = "Slow";

    public static final String TITLE_7 = "Broken";

    public static final List<String> TITLES = Arrays.asList(TITLE_1, TITLE_2, TITLE_3, TITLE_4, TITLE_5, TITLE_6, TITLE_7);

    public static final String SONG_IMAGE = "soundsofuniverse.jpg";

    public static byte[] ALBUM_IMAGE;

    public static void createSampleSongsInAPlayList(PersistenceManager pm) throws IOException
    {

        int songOrder = 0;
        UUID playListId = UUID.randomUUID();
        String imgPath = CLASSES_TARGET_DIRECTORY_PATH + SONG_IMAGE;
        ALBUM_IMAGE = getSongImageAsByteArray(imgPath);
        for (Iterator<String> it = TITLES.iterator(); it.hasNext();)
        {
            String songTitle = it.next();
            Song song = new Song();
            UUID songId = UUID.randomUUID();
            SONG_ID_LIST.add(songId);
            song.setArtistName(ARTIST_NAME);
            song.setAlbumName(ALBUM_NAME);
            song.setSongTitle(songTitle);
            song.setAlbumImage(ALBUM_IMAGE);
            song.setId(songId);
            pm.makePersistent(song);
            Playlist playlist = new Playlist();
            playlist.setSongId(song.getId());
            playlist.setAlbumName(song.getAlbumName());
            playlist.setArtistName(song.getArtistName());
            playlist.setSongTitle(song.getSongTitle());
            playlist.setId(playListId);
            playlist.setSongOrder(songOrder);
            pm.makePersistent(playlist);
        }
    }

    public static final byte[] getSongImageAsByteArray(String filePath) throws IOException
    {
        File imgPath = new File(filePath);
        BufferedImage bufferedImage = ImageIO.read(imgPath);
        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

        return data.getData();

    }

    public static final void cleanupTables()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.newQuery(Song.class).deletePersistentAll();
            pm.newQuery(Playlist.class).deletePersistentAll();

        }
        finally
        {
            tx.commit();
            pm.close();
        }

    }

}
