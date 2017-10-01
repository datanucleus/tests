 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
  * @(#)Client.java	1.9 06/07/27
 */

package com.sun.ts.tests.ejb30.persistence.versioning;

import com.sun.javatest.Status;
import com.sun.ts.lib.util.*;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.harness.ServiceEETest;
import com.sun.ts.lib.harness.EETest.Fault;
import com.sun.ts.tests.ejb30.common.helper.ServiceLocator;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.common.PMClientBase;
import com.sun.ts.tests.common.vehicle.ejb3share.EntityTransactionWrapper;

import java.util.List;
import java.util.Properties;
import java.math.BigInteger;

public class Client extends PMClientBase {


    public Client() {
    }
    
    public static void main(String[] args) {
        Client theTests = new Client();
        Status s=theTests.run(args, System.out, System.err);
        s.exit();
    }
    
  public void setup(String[] args, Properties p) throws Fault
    {
        TLogger.log("setup");
        try {
	   
	   super.setup(args, p);

        }  catch (Exception e) {
            TLogger.log("Exception: " + e.getMessage());
            throw new Fault("Setup failed:", e);
        }
    }


    /*
     * @testName: versionTest1
     * @assertion_ids: PERSISTENCE:SPEC:1068; PERSISTENCE:SPEC:690;
     *			PERSISTENCE:SPEC:666
     * @test_Strategy: The version annotation specifies the version field or
     *			property of an entity class that serves as an optimistic lock
     *			value.   The version is used to ensure integrity when
     *			performing the merge operation and for optimistic
     *			concurrency control.
     *
     *			positive test with sequential tx
     *			
     */

   public void versionTest1() throws Fault
   {

     TLogger.log("Begin versionTest1");
     boolean pass1 = true;
     boolean pass2 = true;
     boolean pass3 = true;
     BigInteger donation = new BigInteger("5000000");
     List c = null;

     try {
       getEntityTransaction().begin();
        Member m = new Member(1, "Jie Leng", true);
	getEntityManager().persist(m);
	getEntityManager().flush();
        getEntityTransaction().commit();
        
        //prior to writing to database, Member may not have any version value.
        //After writing to database, version must have a value.

	Member newMember = getEntityManager().find(Member.class, 1);
        if (newMember.getVersion() == null) {
           TLogger.log("version after persistence is null.");
	   pass1 = false;
	} else {
           TLogger.log("Correct non-null version after create: " + newMember.getVersion());
	}

        // update member
        getEntityTransaction().begin();
	Member newMember2 = getEntityManager().find(Member.class, 1);
        int oldVersion = newMember2.getVersion();
        newMember2.setDonation(donation);
	getEntityManager().merge(newMember2);
	getEntityManager().flush();
        getEntityTransaction().commit();

	Member newMember3 = getEntityManager().find(Member.class, 1);
        if (newMember3.getVersion() <= oldVersion) {
           TLogger.log("Wrong version after update: " +newMember3.getVersion() +
                   ", old version: " + oldVersion);
	   pass2 = false;
	} else {
           TLogger.log("Correct version after update: " +newMember3.getVersion() +
                   ", old version: " + oldVersion);
	}

        oldVersion = newMember3.getVersion();
        // select member
       getEntityTransaction().begin();
       List  result = getEntityManager().createQuery(
		"SELECT m FROM Member m where m.memberName = :name")
	        .setParameter("name", "Jie Leng")
		.getResultList();
	getEntityManager().flush();
       getEntityTransaction().commit();

      Member newMember4 = getEntityManager().find(Member.class, 1);
        if (newMember4.getVersion() != oldVersion) {
           TLogger.log("Wrong version after query, expected " + oldVersion
                   + ", got " +newMember4.getVersion());
	   pass3 = false;
	} else {
           TLogger.log("Correct version after query, expected " + oldVersion
                   + ", got:" +newMember4.getVersion());
	}

     } catch (Exception e) {
         TLogger.log("Unexpection Exception :" + e );
         e.printStackTrace();
	 pass1 = false;
	 pass2 = false;
	 pass3 = false;
     } finally {
       try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
       } catch (Exception re) {
         TLogger.log("Unexpection Exception in rollback:" + re );
         re.printStackTrace();
       }
     }

      if (!pass1 || !pass2 || !pass3)
            throw new Fault( "versionTest1 failed");
    }


    public void cleanup()  throws Fault
    {
	try {
	  getEntityTransaction().begin();
          getEntityManager().remove(getEntityManager().find(Member.class, 1));
	getEntityTransaction().commit();
  	} catch (Exception e) {
	  TLogger.log("Unexpected Exception caught while cleaning up:" + e);
	  e.printStackTrace();
        } finally {
          try {
             if ( getEntityTransaction().isActive() ) {
                  getEntityTransaction().rollback();
             }
          } catch (Exception re) {
            TLogger.log("Unexpection Exception in rollback:" + re );
            re.printStackTrace();
          }
      }
        TLogger.log("cleanup complete, calling super.cleanup");
	super.cleanup();
    }

}

