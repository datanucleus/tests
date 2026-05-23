package org.datanucleus.tests;

import java.net.PasswordAuthentication;

import jakarta.persistence.EntityManagerFactory;

/**
 * Tests for DN ValueGenerator extension
 */
public class StoredProcedureTest extends JakartaPersistenceTestCase
{
    public StoredProcedureTest(String name)
    {
        super(name, false);
    }

    public void testParsingMappings()
    {
        try
        {
            @SuppressWarnings("unused")
            EntityManagerFactory emf = getEMF("TEST");            
        }
        catch (Exception e)
        {
            fail("Error in bootstrapping EMF");
        }
    }
}