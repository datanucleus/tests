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

import java.io.IOException;
import java.util.List;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import org.datanucleus.samples.jdo.cassandra.Song;
import static org.datanucleus.tests.JDOPersistenceTestCase.pmf;
import static org.datanucleus.tests.SampleCassandraData.TITLES;
import org.junit.Assert;

public class CQLTest extends JDOPersistenceTestCase
{

    public CQLTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        SampleCassandraData.loadData();

    }

    @Override
    protected void tearDown() throws Exception
    {
        SampleCassandraData.cleanupTables();
        super.tearDown();
    }

    public void testShouldReturnListOfSongsWithSelect() throws IOException
    {
        PersistenceManager persistenceManager = pmf.getPersistenceManager();
        List<Song> results = null;
        try
        {
            Query query = persistenceManager.newQuery("CQL", "SELECT * FROM schema1.SONG");
            query.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
            query.setResultClass(Song.class);
            results = (List<Song>) query.execute();
            assertEquals(TITLES.size(), results.size());

        }
        catch (Exception e)
        {
            fail("Failed to retrieve Song from Cassandra Store");
        }
        finally
        {
            persistenceManager.close();
        }

        for (Song song : results)
        {
            Assert.assertNotNull(song.getAlbumName());
            Assert.assertNotNull(song.getArtistName());
            Assert.assertNotNull(song.getAlbumImage());
            Assert.assertNotNull(song.getId());
            Assert.assertNotNull(song.getSongTitle());
            Assert.assertEquals(SampleCassandraData.ALBUM_NAME, song.getAlbumName());
            Assert.assertArrayEquals(SampleCassandraData.ALBUM_IMAGE, song.getAlbumImage());
            Assert.assertEquals(SampleCassandraData.ARTIST_NAME, song.getArtistName());

        }
    }

    public void testShouldReturnListOfSongsWithSelectAndFetchSize() throws IOException
    {
        PersistenceManager persistenceManager = pmf.getPersistenceManager();
        List<Song> results = null;
        try
        {
            Query query = persistenceManager.newQuery("CQL", "SELECT * FROM schema1.SONG");
            query.getFetchPlan().setFetchSize(3);
            query.setResultClass(Song.class);
            results = (List<Song>) query.execute();
            assertEquals(TITLES.size(), results.size());

        }
        catch (Exception e)
        {
            fail("Failed to retrieve Song from Cassandra Store");
        }
        finally
        {
            persistenceManager.close();
        }

        for (Song song : results)
        {
            Assert.assertNotNull(song.getAlbumName());
            Assert.assertNotNull(song.getArtistName());
            Assert.assertNotNull(song.getAlbumImage());
            Assert.assertNotNull(song.getId());
            Assert.assertNotNull(song.getSongTitle());
            Assert.assertEquals(SampleCassandraData.ALBUM_NAME, song.getAlbumName());
            Assert.assertArrayEquals(SampleCassandraData.ALBUM_IMAGE, song.getAlbumImage());
            Assert.assertEquals(SampleCassandraData.ARTIST_NAME, song.getArtistName());

        }
    }

    public void testShouldReturnListOfSongsAsObjectsWithCqlshSelect() throws IOException
    {
        PersistenceManager persistenceManager = pmf.getPersistenceManager();
        List<Object> results = null;
        try
        {
            Query query = persistenceManager.newQuery("CQL", "SELECT * FROM schema1.SONG");
            query.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
            // No result class set so expecting results as Objects
            results = (List<Object>) query.execute();
            assertEquals(TITLES.size(), results.size());

        }
        catch (Exception e)
        {
            fail("Failed to retrieve Song from Cassandra Store");
        }
        finally
        {
            persistenceManager.close();
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
