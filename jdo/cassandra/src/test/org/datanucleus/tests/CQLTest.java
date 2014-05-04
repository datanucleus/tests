/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.datanucleus.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import org.datanucleus.samples.jdo.cassandra.Playlist;
import org.datanucleus.samples.jdo.cassandra.Song;

/**
 * 
 * @author bergun
 */
public class CQLTest extends JDOPersistenceTestCase
{

    Object id;

    public CQLTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loadData();

    }

    protected void tearDown() throws Exception
    {
        clean(Song.class);
        clean(Playlist.class);
        System.out.println("teardown");
        super.tearDown();
    }

    public void testNull() throws Exception
    {

    }

    private void loadData() throws SQLException, IOException, URISyntaxException
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
        }
        
        // TODO load sample data
    }

}
