/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import org.datanucleus.samples.jdo.cassandra.Song;
import static org.datanucleus.tests.SampleCassandraData.SONG_IMAGE_1;
import static org.datanucleus.tests.SampleCassandraData.getSongImageAsByteArray;

/**
 * 
 * @author bergun
 */
public class CassandraTypesTest extends JDOPersistenceTestCase
{
    

    public CassandraTypesTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        SampleCassandraData.loadData();

    }

    protected void tearDown() throws Exception
    {
        SampleCassandraData.cleanupTables();
        super.tearDown();
    }

    public void testGetBlobDataFromCassandra() throws Exception
    {

        PersistenceManager pm = pmf.getPersistenceManager();
        Song retrieveSong = null;
        try
        {
            retrieveSong = pm.getObjectById(Song.class, SampleCassandraData.songId);
        }
        catch (Exception e)
        {
            fail("Failed to retrieve Song from Cassandra Store");
        }
        finally
        {
            pm.close();
        }

        assertEquals(SampleCassandraData.songId, retrieveSong.getId());
        assertEquals(SampleCassandraData.ALBUM_1, retrieveSong.getAlbum());                
        assertEquals(SampleCassandraData.ARTIST_1, retrieveSong.getArtist());
        assertEquals(SampleCassandraData.TITLE_1, retrieveSong.getTitle());
        String imgPath = SampleCassandraData.CLASSES_TARGET_DIRECTORY_PATH + SONG_IMAGE_1;        
        assertNotNull(retrieveSong.getData());
        byte[] expectedImageByteArray = getSongImageAsByteArray(imgPath);
        byte[] actualImageByteArray = retrieveSong.getData();        
        assertEquals(expectedImageByteArray, actualImageByteArray);
        
        
    }
    
    


    

}
