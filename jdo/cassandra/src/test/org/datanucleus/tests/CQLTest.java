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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.jdo.cassandra.Song;
import org.junit.Assert;

public class CQLTest extends JDOPersistenceTestCase
{
    static final String ARTIST_NAME = "Depeche Mode";
    static final String ALBUM_NAME = "Delta Machine";
    static final List<String> TITLES = Arrays.asList("Welcome to My World", "Angel", "Heaven", "Secret to the End", "My Little Universe", "Slow", "Broken");
    static final String SONG_IMAGE = "soundsofuniverse.jpg";
    static byte[] ALBUM_IMAGE;

    public CQLTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        // Read in album image
        File imgPath = new File(CQLTest.class.getResource("/").getPath().replace("test-classes", "classes") + SONG_IMAGE);
        BufferedImage bufferedImage = ImageIO.read(imgPath);
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
        ALBUM_IMAGE = data.getData();

        // Create songs
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            for (Iterator<String> it = TITLES.iterator(); it.hasNext();)
            {
                String songTitle = it.next();
                Song song = new Song();
                UUID songId = UUID.randomUUID();
                song.setArtistName(ARTIST_NAME);
                song.setAlbumName(ALBUM_NAME);
                song.setSongTitle(songTitle);
                song.setAlbumImage(ALBUM_IMAGE);
                song.setId(songId);
                pm.makePersistent(song);
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
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

    @Override
    protected void tearDown() throws Exception
    {
        clean(Song.class);
        super.tearDown();
    }

    public void testShouldReturnListOfSongsWithSelect() throws IOException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Song> results = null;
        try
        {
            Query query = pm.newQuery("CQL", "SELECT * FROM schema1.SONG");
            query.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
            query.setResultClass(Song.class);
            results = (List<Song>) query.execute();
            assertEquals(TITLES.size(), results.size());
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown from CQL query", e);
            fail("Failed to retrieve Song from Cassandra Store : " + e.getMessage());
        }
        finally
        {
            pm.close();
        }

        for (Song song : results)
        {
            Assert.assertNotNull(song.getAlbumName());
            Assert.assertNotNull(song.getArtistName());
            Assert.assertNotNull(song.getAlbumImage());
            Assert.assertNotNull(song.getId());
            Assert.assertNotNull(song.getSongTitle());
            Assert.assertEquals(ALBUM_NAME, song.getAlbumName());
            Assert.assertArrayEquals(ALBUM_IMAGE, song.getAlbumImage());
            Assert.assertEquals(ARTIST_NAME, song.getArtistName());
        }
    }

    public void testShouldReturnListOfSongsWithSelectAndFetchSize() throws IOException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Song> results = null;
        try
        {
            Query query = pm.newQuery("CQL", "SELECT * FROM schema1.SONG");
            query.getFetchPlan().setFetchSize(3);
            query.setResultClass(Song.class);
            results = (List<Song>) query.execute();
            assertEquals(TITLES.size(), results.size());
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown from CQL query", e);
            fail("Failed to retrieve Song from Cassandra Store : " + e.getMessage());
        }
        finally
        {
            pm.close();
        }

        for (Song song : results)
        {
            Assert.assertNotNull(song.getAlbumName());
            Assert.assertNotNull(song.getArtistName());
            Assert.assertNotNull(song.getAlbumImage());
            Assert.assertNotNull(song.getId());
            Assert.assertNotNull(song.getSongTitle());
            Assert.assertEquals(ALBUM_NAME, song.getAlbumName());
            Assert.assertArrayEquals(ALBUM_IMAGE, song.getAlbumImage());
            Assert.assertEquals(ARTIST_NAME, song.getArtistName());
        }
    }

    public void testShouldReturnListOfSongsAsObjectsWithCqlshSelect() throws IOException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        List<Object> results = null;
        try
        {
            Query query = pm.newQuery("CQL", "SELECT * FROM schema1.SONG");
            query.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
            // No result class set so expecting results as Objects
            results = (List<Object>) query.execute();
            assertEquals(TITLES.size(), results.size());
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown from CQL query", e);
            fail("Failed to retrieve Song from Cassandra Store : " + e.getMessage());
        }
        finally
        {
            pm.close();
        }

        for (Object result : results)
        {
            assertEquals(Object[].class, result.getClass());
            Object[] resultColumns = (Object[]) result;
            for (Object resultColumn : resultColumns)
            {
                assertNotNull(resultColumn);
            }
        }
    }
}
