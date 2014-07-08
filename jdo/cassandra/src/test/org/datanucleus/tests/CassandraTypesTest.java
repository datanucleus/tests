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

Contributors :
 barisergun75@gmail.com
***********************************************************************/
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import org.datanucleus.samples.jdo.cassandra.Song;
import static org.datanucleus.tests.SampleCassandraData.SONG_IMAGE_1;
import static org.datanucleus.tests.SampleCassandraData.getSongImageAsByteArray;
import org.junit.Assert;

/**
 * 
 * @author bergun
 */
// TODO will move these tests under jdo/general
// When moving consider blob and uuid as PK tests. If there is an existing test case
// reuse them.
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
        Assert.assertArrayEquals(expectedImageByteArray, actualImageByteArray);
        
        
    }
    
    


    

}
