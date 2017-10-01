 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.7 06/04/12
 */
package com.sun.ts.tests.ejb30.persistence.relationship.descriptors;

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
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.Properties;

public class Client extends PMClientBase {

    private static XAddress aRef[] = new XAddress[5];
    private static XAnnualReview rRef[] = new XAnnualReview[10];
    private static XCompany cRef[] = new XCompany[5];
    private static XInsurance insRef[] = new XInsurance[5];
    private static XPerson pRef[] = new XPerson[20];
    private static XProject projRef[] = new XProject[10];
    private static XTeam tRef[] = new XTeam[10];
    private Iterator iterator;

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
     * @testName: descriptorMappingTest1
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921; PERSISTENCE:SPEC:949;
     *                 PERSISTENCE:SPEC:950
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *			BiDirectional OneToOne Relationship
     *
     *			OnePerson to OneProjectLead
     *
     *			Entity Person mapped to table named PERSON references
     *				a single instance of Project.
     *			Entity Project mapped to table named PROJECT
     *				a single instance of Person.
     *			Entity Person is the owner of the relationship.
     *
     *			Table PERSON contains a foreign key to PROJECT.
     *			The foreign key is named PROJECT_PROJID.
     *               
     */

    public void descriptorMappingTest1() throws Fault
    {

      TLogger.log("Begin descriptorMappingTest1");
      boolean pass = false;

      try {
	createPeople();
	createProjects();
        getEntityTransaction().begin();

	pRef[0].setXProject(projRef[2]);
	projRef[2].setXProjectLead(pRef[0]);

	getEntityManager().merge(pRef[0]);
	getEntityManager().merge(projRef[2]);

	XPerson newPerson = getEntityManager().find(XPerson.class, 1);

	if (newPerson.getXProject().getXName().equals("Asp") ) {
                TLogger.log("descriptorMappingTest1: Expected results received");
		pass = true;
	}

        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }
      }

      if (!pass)
            throw new Fault( "descriptorMappingTest1 failed");
    }


    /*
     * @testName: descriptorMappingTest2
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *			BiDirectional ManyToOne Relationship
     *
     *			ManyTeams to OneCompany
     *
     *			Entity Team is mapped to a table named TEAM references
     *				a single instance of Company.
     *			Entity Company mapped to table named COMPANY references
     *				a collection of Entity Team.
     *			Entity Team is the owner of the relationship.
     *
     *			Table TEAM contains a foreign key to COMPANY.
     *			The foreign key is named COMPANY_COMPANYID.
     *               
     */

    public void descriptorMappingTest2() throws Fault
    {

      TLogger.log("Begin descriptorMappingTest2");
      boolean pass1 = true;
      boolean pass2 = false;
      Vector<XTeam> v1 = null;
      Vector<XTeam> v2 = null;
      int foundTeam1 = 0;
      int foundTeam2 = 0;
      String[] expectedTeam1 = new String[] {"Engineering", "Marketing", "Sales"};
      String[] expectedTeam2 = new String[] {"Accounting", "Training"};


      try {
	createTeams();
	createCompany();

        getEntityTransaction().begin();

	tRef[0].setXCompany(cRef[0]);
	tRef[1].setXCompany(cRef[0]);
	tRef[2].setXCompany(cRef[0]);

	v1 = new Vector<XTeam>();
	v1.add(tRef[0]);
	v1.add(tRef[1]);
	v1.add(tRef[2]);
	cRef[0].setXTeams(v1);

	tRef[3].setXCompany(cRef[1]);
	tRef[4].setXCompany(cRef[1]);

	v2 = new Vector<XTeam>();
	v2.add(tRef[3]);
	v2.add(tRef[4]);
	cRef[1].setXTeams(v2);

	getEntityManager().merge(tRef[0]);
	getEntityManager().merge(tRef[1]);
	getEntityManager().merge(tRef[2]);
	getEntityManager().merge(tRef[3]);
	getEntityManager().merge(tRef[4]);

	getEntityManager().merge(cRef[0]);
	getEntityManager().merge(cRef[1]);
	getEntityManager().flush();

	XCompany c1 = getEntityManager().find(XCompany.class, (long)25501);
	Collection<XTeam> t1 = c1.getXTeams();
	
	XCompany c2 = getEntityManager().find(XCompany.class, (long)37560);
	Collection<XTeam> t2 = c2.getXTeams();


	if( (t1.size() != 3) || (t2.size() != 2) ) {
                TLogger.log("ERROR:  descriptorMappingTest2: Did not get expected results." 
				+ "Team1 Collection Expected 3 references, got: "
                                + t1.size()
				+ ", Team2 Collection Expected 2 references, got: "
                                + t2.size() );
              pass1 = false;
            } else if (pass1) {

              Iterator i1 = t1.iterator();
              while (i1.hasNext()) {
                        TLogger.log("Check Team 1 Collection for expected Teams");
			XTeam o1 = (XTeam)i1.next();

                        for(int l=0; l<3; l++) {
                        if (expectedTeam1[l].equals((String)o1.getXName()) ) {
                          TLogger.log("Found Team 1:"  + (String)o1.getXName() );
                          foundTeam1++;
                          break;
                          }
                      }
              }

              Iterator i2 = t2.iterator();
              while (i2.hasNext()) {
                        TLogger.log("Check Team 2 Collection for expected Teams");
			XTeam o2 = (XTeam)i2.next();

                        for(int l=0; l<2; l++) {
                        if (expectedTeam2[l].equals((String)o2.getXName()) ) {
                          TLogger.log("Found Team 2:" + (String)o2.getXName() );
                          foundTeam2++;
                          break;
                          }
                      }
             }
	   }


           if ((foundTeam1 != 3) || (foundTeam2 != 2)) {
                      TLogger.log("ERROR: descriptorMappingTest2: Did not get expected results");
                      pass2 = false;
           } else {
                  TLogger.log(
                    "descriptorMappingTest2: Expected results received");
		  pass2 = true;
           }


        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }

      }

      if (!pass2)
            throw new Fault( "descriptorMappingTest2 failed");
    }


    /*
     * @testName: descriptorMappingTest3
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *			BiDirectional OneToMany Relationship
     *
     *                  OneCompany To ManyTeams
     *
     *                  Entity Company mapped to table named COMPANY references
     *                          a collection of Entity Team.
     *                  Entity Team is mapped to a table named TEAM references
     *                          a single instance of Entity Company.
     *                  Entity Team is the owner of the relationship.
     *
     *                  Table TEAM contains a foreign key to COMPANY.
     *                  The foreign key is named COMPANY_COMPANYID.
     *
     */

    public void descriptorMappingTest3() throws Fault
    {

      TLogger.log("Begin descriptorMappingTest3");
      boolean pass = false;
      Vector v1 = null;
      Vector v2 = null;

      try {
        createTeams();
        createCompany();

        getEntityTransaction().begin();

        tRef[0].setXCompany(cRef[0]);
        tRef[1].setXCompany(cRef[0]);

        v1 = new Vector();
        v1.add(tRef[0]);
        v1.add(tRef[1]);
        cRef[0].setXTeams(v1);

        tRef[2].setXCompany(cRef[1]);
        tRef[3].setXCompany(cRef[1]);
        tRef[4].setXCompany(cRef[1]);

        v2 = new Vector();
        v2.add(tRef[2]);
        v2.add(tRef[3]);
        v2.add(tRef[4]);

        cRef[1].setXTeams(v2);

        getEntityManager().merge(tRef[0]);
        getEntityManager().merge(tRef[1]);
        getEntityManager().merge(tRef[2]);
        getEntityManager().merge(tRef[3]);
        getEntityManager().merge(tRef[4]);

        getEntityManager().merge(cRef[0]);
        getEntityManager().merge(cRef[1]);

        getEntityManager().flush();

        XTeam t1 = getEntityManager().find(XTeam.class, 1);
        XTeam t2 = getEntityManager().find(XTeam.class, 2);
        XTeam t3 = getEntityManager().find(XTeam.class, 3);
        XTeam t4 = getEntityManager().find(XTeam.class, 4);
        XTeam t5 = getEntityManager().find(XTeam.class, 5);


	if ( ( t1.getXCompany().getXCompanyId() == (long)25501) &&
	     ( t2.getXCompany().getXCompanyId() == (long)25501) &&
	     ( t3.getXCompany().getXCompanyId() == (long)37560) &&
	     ( t4.getXCompany().getXCompanyId() == (long)37560) &&
	     ( t5.getXCompany().getXCompanyId() == (long)37560) )
	{
	
		pass = true;
	}

        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }
      }

      if (!pass)
            throw new Fault( "descriptorMappingTest3 failed");
    }


    /*
     * @testName: descriptorMappingTest4
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *                  UniDirectional OneToOne Relationship
     *
     *                  OneCompany To OneAddress
     *
     *                  Entity Company mapped to table named COMPANY references
     *                          a single instance of Entity Address.
     *                  Entity Address is mapped to a table named ADDRESS which
     *                          does not reference Entity Company.
     *                  Entity Company is the owner of the relationship.
     *
     *                  Table COMPANY contains a foreign key to ADDRESS.
     *                  The foreign key is named ADDRESS_ID.
     *
     */

    public void descriptorMappingTest4() throws Fault
    {

    TLogger.log("Begin descriptorMappingTest4");
    boolean pass = false;


	try {

    	  createAddress();
    	  createCompany();

          getEntityTransaction().begin();

          cRef[0].setXAddress(aRef[0]);
          cRef[1].setXAddress(aRef[1]);

          getEntityManager().merge(cRef[0]);
          getEntityManager().merge(cRef[1]);
 
	  getEntityManager().flush();

          XCompany c1 = getEntityManager().find(XCompany.class, (long)25501);
          XCompany c2 = getEntityManager().find(XCompany.class, (long)37560);

	  if (c1.getXAddress().getXCity().equals("Burlington") &&
		c2.getXAddress().getXCity().equals("Santa Clara") ) {
    		TLogger.log("Expected results received");
		pass = true;
	  } else {
    		TLogger.log("descriptorMappingTest4: Did not get expected results" +
			"Expected: Burlington and Santa Clara, got: " +
			c1.getXAddress().getXCity() + c2.getXAddress().getXCity() );
	  }

        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }

      }

		
    if (!pass)
            throw new Fault( "descriptorMappingTest4 failed");
    }

    /*
     * @testName: descriptorMappingTest5
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *                  UniDirectional ManyToOne Relationship
     *
     *                  ManyPersons To OneTeam
     *
     *                  Entity Person mapped to table named PERSON references
     *                          a single instance of Entity Team.
     *                  Entity Team is mapped to a table named TEAM and
     *                          does not reference Entity Person.
     *                  Entity Person is the owner of the relationship.
     *
     *                  Table PERSON contains a foreign key to TEAM.
     *                  The foreign key is named TEAM_TEAMID.
     *
     */

    public void descriptorMappingTest5() throws Fault
    {
      TLogger.log("Begin descriptorMappingTest5");
      boolean pass = false;

      try {
        createTeams();
        createPeople();

        getEntityTransaction().begin();

        pRef[1].setXTeam(tRef[0]);
        pRef[3].setXTeam(tRef[1]);
        pRef[5].setXTeam(tRef[2]);
        pRef[7].setXTeam(tRef[3]);
        pRef[9].setXTeam(tRef[4]);

        pRef[2].setXTeam(tRef[4]);
        pRef[4].setXTeam(tRef[3]);
        pRef[6].setXTeam(tRef[2]);
        pRef[8].setXTeam(tRef[1]);
        pRef[10].setXTeam(tRef[0]);


	for (int i=1; i<11; i++) {
             getEntityManager().merge(pRef[i]);
	}

        getEntityManager().flush();

            if ( ( pRef[1].getXTeam() == tRef[0] )   && ( pRef[10].getXTeam() == tRef[0] )   &&
               ( pRef[3].getXTeam() == tRef[1] )  && ( pRef[8].getXTeam() == tRef[1] )   &&
               ( pRef[5].getXTeam() == tRef[2] )  && ( pRef[6].getXTeam() == tRef[2] )   &&
               ( pRef[7].getXTeam() == tRef[3] )  && ( pRef[4].getXTeam() == tRef[3] )   &&
               ( pRef[9].getXTeam() == tRef[4] )  && ( pRef[2].getXTeam() == tRef[4] ) ) {

             TLogger.log(
                    "descriptorMappingTest5: Expected results received");
              pass = true;
           } else {
                  TLogger.log(
                    "descriptorMappingTest5: Did not get expected results ");
                  pass = false;
           }


        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }

      }

    if (!pass)
            throw new Fault( "descriptorMappingTest5 failed");
    }


    /*
     * @testName: descriptorMappingTest6
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *                  BiDirectional ManyToMany Relationship
     *
     *                  ManyProjects To ManyPersons
     *
     *                  Entity Project mapped to table named PROJECT 
     *                          references a collection of Entity Project.
     *                  Entity Person is mapped to a table named PERSON and
     *                          references a collection of Entity Project.
     *                  Entity Project is the owner of the relationship.
     *
     *			There is a join table named PROJECT_PERSON (owner
     *			named first.  One foreign key column refers to table
     *			PROJECT.  The name of the foreign key column is
     *			PROJECTS_PROJID.  The other foreign key column is
     *			refers to the PERSON table.  The name of this foreign
     *			key is PERSONS_PERSONID. 
     *
     */

    public void descriptorMappingTest6() throws Fault
    {
      TLogger.log("Begin descriptorMappingTest6");
      boolean pass1 = true;
      boolean pass2 = false;
      Vector<XTeam> v1 = null;
      Vector<XTeam> v2 = null;
      int foundProject1 = 0;
      int foundProject2 = 0;
      Long[] expectedResults1 = new Long[] {new Long(123456789), new Long(345678901), new Long(567890123) };
      Long[] expectedResults2 = new Long[] {new Long(234567890), new Long(345678901), new Long(456789012) };

      try {
        createPeople();
        createProjects();

        getEntityTransaction().begin();


	pRef[5].getXProjects().add(projRef[0]);
	pRef[5].getXProjects().add(projRef[2]);
	pRef[5].getXProjects().add(projRef[4]);
	getEntityManager().merge(pRef[5]);


	pRef[8].getXProjects().add(projRef[1]);
	pRef[8].getXProjects().add(projRef[2]);
	pRef[8].getXProjects().add(projRef[3]);
	getEntityManager().merge(pRef[8]);

	projRef[0].getXPersons().add(pRef[5]);
	getEntityManager().merge(projRef[0]);

	projRef[1].getXPersons().add(pRef[8]);
	getEntityManager().merge(projRef[1]);

	projRef[2].getXPersons().add(pRef[5]);
	getEntityManager().merge(projRef[2]);

	projRef[2].getXPersons().add(pRef[8]);
	getEntityManager().merge(projRef[2]);

	projRef[3].getXPersons().add(pRef[8]);
	getEntityManager().merge(projRef[3]);


	projRef[4].getXPersons().add(pRef[5]);
	getEntityManager().merge(projRef[4]);


	getEntityManager().flush();

	XPerson p1 = getEntityManager().find(XPerson.class, 6);
	XPerson p2 = getEntityManager().find(XPerson.class, 9);

	Collection<XProject> projCol1 = p1.getXProjects();
	Collection<XProject> projCol2 = p2.getXProjects();

        if( (projCol1.size() != 3) || (projCol2.size() != 3 ) ) {
                TLogger.log("ERROR:  descriptorMappingTest6: Did not get expected results."
                                + "Expected 3 Projects for Karen Tegan (PK 6) , got: "
                                + projCol1.size()
                                + ", Expected 2 Projects for William Keaton (PK 9), got: "
                                + projCol2.size() );
              pass1 = false;
        } else if (pass1) {

           Iterator i1 = projCol1.iterator();
           while (i1.hasNext()) {
                     TLogger.log("Check Collection for Karen Tegan Projects");
                     XProject o1 = (XProject)i1.next();

                     for(int l=0; l<3; l++) {
                     if (expectedResults1[l].equals((Long)o1.getXProjId()) ) {
                         TLogger.log("Found Project for Karen Tegan: "
                                 + (String)o1.getXName() );
                          foundProject1++;
                          break;
                          }
                      }
            }


            Iterator i2 = projCol2.iterator();
            while (i2.hasNext()) {
                     TLogger.log("Check Collection for William Keaton Projects");
                     XProject o2 = (XProject)i2.next();

                     for(int l=0; l<3; l++) {
                     if (expectedResults2[l].equals((Long)o2.getXProjId()) ) {
                         TLogger.log("Found Project for William Keaton: "
                                 + (String)o2.getXName() );
                         foundProject2++;
                         break;
                         }
                      }
            }

	}

          if ( (foundProject1 != 3) || (foundProject2 != 3) ) {
                TLogger.log("ERROR: descriptorMappingTest6: Did not get expected results");
                pass2 = false;
           } else {
                  TLogger.log(
                    "descriptorMappingTest6: Expected results received");
                  pass2 = true;
           }

        Collection<XPerson> nullPersonCol = new Vector<XPerson>();
        projRef[0].setXPersons(nullPersonCol);
        getEntityManager().merge(projRef[0]);

        projRef[1].setXPersons(nullPersonCol);
        getEntityManager().merge(projRef[1]);

        projRef[2].setXPersons(nullPersonCol);
        getEntityManager().merge(projRef[2]);

        projRef[3].setXPersons(nullPersonCol);
        getEntityManager().merge(projRef[3]);

        projRef[4].setXPersons(nullPersonCol);
        getEntityManager().merge(projRef[4]);

        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }
      }


      if (!pass1 || !pass2)
            throw new Fault( "descriptorMappingTest6 failed");
      }


    /*
     * @testName: descriptorMappingTest7
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *                 UniDirectional OneToMany Relationship
     *
     *                  OnePerson To ManyReviews
     *
     *                  Entity Person mapped to table named PERSON
     *                          references a collection of Entity AnnualReview.
     *                  Entity AnnualReview is mapped to a table named
     *				ANNUALREVIEW which does not reference Entity Person.
     *                  Entity Person is the owner of the relationship.
     *
     *                  There is a join table named PERSON_ANNUALREVIEW (owner
     *                  named first.  One foreign key column refers to table
     *                  PERSON.  The name of the foreign key column is
     *                  PERSON_PERSONID.  The other foreign key column is
     *                  refers to the ANNUALREVIEW table.  The name of this foreign
     *                  key is ANNUALREVIEWS_AID.
     *
     */

    public void descriptorMappingTest7() throws Fault
    {
    TLogger.log("Begin descriptorMappingTest7");
      boolean pass1 = true;
      boolean pass2 = false;

      Vector<XAnnualReview> v1 = null;
      Vector<XAnnualReview> v2 = null;
      Vector<XAnnualReview> v3 = null;
      Vector<XAnnualReview> v4 = null;
      int foundCol1 = 0;
      int foundCol2 = 0;
      int foundCol3 = 0;
      int foundCol4 = 0;
      int foundCol5 = 0;
      Integer[] expectedCol1 = new Integer[] {new Integer(1), new Integer(2), new Integer(3), new Integer(4)};
      Integer[] expectedCol2 = new Integer[] {new Integer(5), new Integer(6)};
      Integer[] expectedCol3 = new Integer[] {new Integer(3), new Integer(8)};
      Integer[] expectedCol4 = new Integer[] {new Integer(4), new Integer(7)};
      Integer[] expectedCol5 = new Integer[] {new Integer(1), new Integer(2), new Integer(3), new Integer(4)};

      try {
	createReviews();
        createPeople();

        getEntityTransaction().begin();

        v1 = new Vector<XAnnualReview>();
        v1.add(rRef[0]);
        v1.add(rRef[1]);
        v1.add(rRef[2]);
        v1.add(rRef[3]);

        v2 = new Vector<XAnnualReview>();
        v2.add(rRef[4]);
        v2.add(rRef[5]);

        v3 = new Vector<XAnnualReview>();
        v3.add(rRef[2]);
        v3.add(rRef[7]);

        v4 = new Vector<XAnnualReview>();
        v4.add(rRef[3]);
        v4.add(rRef[6]);


        pRef[11].setXAnnualReviews(v1);
        pRef[13].setXAnnualReviews(v2);
        pRef[15].setXAnnualReviews(v3);
        pRef[17].setXAnnualReviews(v4);
        pRef[19].setXAnnualReviews(v1);


        getEntityManager().merge(pRef[11]);
        getEntityManager().merge(pRef[13]);
        getEntityManager().merge(pRef[15]);
        getEntityManager().merge(pRef[17]);
        getEntityManager().merge(pRef[19]);

        getEntityManager().flush();

        XPerson p1 = getEntityManager().find(XPerson.class, 12);
        XPerson p2 = getEntityManager().find(XPerson.class, 14);
        XPerson p3 = getEntityManager().find(XPerson.class, 16);
        XPerson p4 = getEntityManager().find(XPerson.class, 18);
        XPerson p5 = getEntityManager().find(XPerson.class, 20);


	Collection<XAnnualReview> col1 = p1.getXAnnualReviews();
	Collection<XAnnualReview> col2 = p2.getXAnnualReviews();
	Collection<XAnnualReview> col3 = p3.getXAnnualReviews();
	Collection<XAnnualReview> col4 = p4.getXAnnualReviews();
	Collection<XAnnualReview> col5 = p5.getXAnnualReviews();

        if( (col1.size() != 4) || (col2.size() != 2 ||
		col3.size() != 2 || col4.size() != 2 || col5.size() != 4 ) ) {
                TLogger.log("ERROR:  descriptorMappingTest7: Did not get expected results."
                                + "Expected 4 reviews for Mary Macy (PK 12) , got: "
                                + col1.size()
                                + ", Expected 2 reviews for Julie OClaire (PK 14), got: "
                                + col2.size() 
                                + ", Expected 2 reviews for Kellie Lee (PK 16), got: "
                                + col3.size() 
                                + ", Expected 2 reviews for Mark Francis (PK 18), got: "
                                + col4.size() 
                                + ", Expected 4 reviews for Katy Hughes (PK 20), got: "
                                + col5.size() );
              pass1 = false;
            } else if (pass1) {

              Iterator i1 = col1.iterator();
              while (i1.hasNext()) {
                        TLogger.log("Check Collection for Mary Macy Reviews");
                        XAnnualReview o1 = (XAnnualReview)i1.next();

                        for(int l=0; l<5; l++) {
                        if (expectedCol1[l].equals((Integer)o1.getXService()) ) {
                          TLogger.log("Found Mary Macy Annual Review for Service Year: "
				 + (Integer)o1.getXService() );
                          foundCol1++;
                          break;
                          }
                      }
              }


              Iterator i2 = col2.iterator();
              while (i2.hasNext()) {
                        TLogger.log("Check Collection for Julie OClaire Reviews");
                        XAnnualReview o2 = (XAnnualReview)i2.next();

                        for(int l=0; l<2; l++) {
                        if (expectedCol2[l].equals((Integer)o2.getXService()) ) {
                          TLogger.log("Found Julie OClaire Annual Review for Service Year: "
				 + (Integer)o2.getXService() );
                          foundCol2++;
                          break;
                          }
                      }
              }

              Iterator i3 = col3.iterator();
              while (i3.hasNext()) {
                        TLogger.log("Check Collection for Kellie Lee Reviews");
                        XAnnualReview o3 = (XAnnualReview)i3.next();

                        for(int l=0; l<2; l++) {
                        if (expectedCol3[l].equals((Integer)o3.getXService()) ) {
                          TLogger.log("Found Kellie Lee Annual Review for Service Year: " 
				+ (Integer)o3.getXService() );
                          foundCol3++;
                          break;
                          }
                      }
              }


              Iterator i4 = col4.iterator();
              while (i4.hasNext()) {
                        TLogger.log("Check Collection for Mark Francis Reviews");
                        XAnnualReview o4 = (XAnnualReview)i4.next();

                        for(int l=0; l<2; l++) {
                        if (expectedCol4[l].equals((Integer)o4.getXService()) ) {
                          TLogger.log("Found Mark Francis Annual Review for Service Year: "
				+ (Integer)o4.getXService() );
                          foundCol4++;
                          break;
                          }
                      }
              }


              Iterator i5 = col5.iterator();
              while (i5.hasNext()) {
                        TLogger.log("Check Collection for Katy Hughes Reviews");
                        XAnnualReview o5 = (XAnnualReview)i5.next();

                        for(int l=0; l<5; l++) {
                        if (expectedCol5[l].equals((Integer)o5.getXService()) ) {
                          TLogger.log("Found Katy Hughes Annual Review for Service Year: "
				+ (Integer)o5.getXService() );
                          foundCol5++;
                          break;
                          }
                      }
              }

           }

          if ( (foundCol1 != 4) || (foundCol2 != 2) ||
 		(foundCol3 != 2) || (foundCol4 != 2) || (foundCol5 != 4 ) ) {

                TLogger.log("ERROR: descriptorMappingTest7: Did not get expected results");
                pass2 = false;
           } else {
                  TLogger.log(
                    "descriptorMappingTest7: Expected results received");
                  pass2 = true;
           }


        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }
      }

    if ( !pass1 || !pass2 )
            throw new Fault( "descriptorMappingTest7 failed");
    }



    /*
     * @testName: descriptorMappingTest8
     * @assertion_ids: PERSISTENCE:SPEC:1144; PERSISTENCE:SPEC:919;
     *                 PERSISTENCE:SPEC:921
     * @test_Strategy: RelationShip Mapping Using Descriptors
     *                  UniDirectional ManyToMany Relationship
     *
     *                  ManyPersons To ManyInsurances
     *
     *                  Entity Person mapped to table named PERSON
     *                          references a collection of Entity Insurance.
     *                  Entity Insurance is mapped to a table named INSURANCE and
     *                          which does not reference Entity Person.
     *                  Entity Person is the owner of the relationship.
     *
     *                  There is a join table named PERSON_INSURANCE (owner
     *                  named first.  One foreign key column refers to table
     *                  PERSON.  The name of the foreign key column is
     *                  PERSON_PERSONID.  The other foreign key column is
     *                  refers to the INSURANCE table.  The name of this foreign
     *                  key is INSURANCES_INSID.
     *
     */

    public void descriptorMappingTest8() throws Fault
    {
      boolean pass1 = true;
      boolean pass2 = false;
      int foundInsurance1 = 0;
      int foundInsurance2 = 0;
      int foundInsurance3 = 0;
      Integer[] expectedResults1 = new Integer[] { new Integer(1), new Integer(3) };
      Integer[] expectedResults2 = new Integer[] { new Integer(2), new Integer(3) };
      Integer[] expectedResults3 = new Integer[] { new Integer(1), new Integer(2), new Integer(3) };

      try {
        createPeople();
        createInsurance();

        getEntityTransaction().begin();


	pRef[2].getXInsurance().add(insRef[0]);
	pRef[2].getXInsurance().add(insRef[2]);
	getEntityManager().merge(pRef[2]);

	pRef[12].getXInsurance().add(insRef[1]);
	pRef[12].getXInsurance().add(insRef[2]);
	getEntityManager().merge(pRef[12]);

	pRef[16].getXInsurance().add(insRef[0]);
	pRef[16].getXInsurance().add(insRef[1]);
	pRef[16].getXInsurance().add(insRef[2]);
	getEntityManager().merge(pRef[16]);


	getEntityManager().flush();

	XPerson p1 = getEntityManager().find(XPerson.class, 3);
	XPerson p2 = getEntityManager().find(XPerson.class, 13);
	XPerson p3 = getEntityManager().find(XPerson.class, 17);

	Collection<XInsurance> insCol1 = p1.getXInsurance();
	Collection<XInsurance> insCol2 = p2.getXInsurance();
	Collection<XInsurance> insCol3 = p3.getXInsurance();

        if( (insCol1.size() != 2) || (insCol2.size() != 2 ) || (insCol3.size() != 3) ) {
                TLogger.log("ERROR:  descriptorMappingTest8: Did not get expected results."
                                + "Expected 2 Insurance Carriers for Shelly McGowan (PK 3) , got: "
                                + insCol1.size()
                                + ", Expected 2 Insurance Carriers for Cheng Fang (PK 13) , got: "
                                + insCol2.size()
                                + ", Expected 3 Insurance Carriers for Nicole Martin (PK 17), got: "
                                + insCol3.size() );
              pass1 = false;
        } else if (pass1) {

           Iterator i1 = insCol1.iterator();
           while (i1.hasNext()) {
                     TLogger.log("Check Insurance Carriers for Shelly McGowan");
                     XInsurance o1 = (XInsurance)i1.next();

                     for(int l=0; l<2; l++) {
                     if (expectedResults1[l].equals((Integer)o1.getXInsId()) ) {
                         TLogger.log("Found Insurance Carrier for Shelly McGowan: "
                                 + (String)o1.getXCarrier() );
                          foundInsurance1++;
                          break;
                          }
                      }
            }

           Iterator i2 = insCol2.iterator();
           while (i2.hasNext()) {
                     TLogger.log("Check Insurance Carriers for Cheng Fang");
                     XInsurance o2 = (XInsurance)i2.next();

                     for(int l=0; l<2; l++) {
                     if (expectedResults2[l].equals((Integer)o2.getXInsId()) ) {
                         TLogger.log("Found Insurance Carrier for Cheng Fang: "
                                 + (String)o2.getXCarrier() );
                          foundInsurance2++;
                          break;
                          }
                      }
            }

           Iterator i3 = insCol3.iterator();
           while (i3.hasNext()) {
                     TLogger.log("Check Insurance Carriers for Nicole Martin");
                     XInsurance o3 = (XInsurance)i3.next();

                     for(int l=0; l<3; l++) {
                     if (expectedResults3[l].equals((Integer)o3.getXInsId()) ) {
                         TLogger.log("Found Insurance Carrier for Nicole Martin: "
                                 + (String)o3.getXCarrier() );
                          foundInsurance3++;
                          break;
                          }
                      }
            }

	}

        if ( (foundInsurance1 != 2) || (foundInsurance2 != 2) || (foundInsurance3 != 3) ) {
                TLogger.log("ERROR: descriptorMappingTest8: Did not get expected results");
                pass2 = false;
        } else {
               TLogger.log(
                    "descriptorMappingTest8: Expected results received");
               pass2 = true;
        }

        getEntityTransaction().commit();

      } catch (Exception e) {
          TLogger.log("Unexpection Exception :" + e );
          e.printStackTrace();
      } finally {
        try {
           if ( getEntityTransaction().isActive() ) {
                getEntityTransaction().rollback();
           }
        } catch (Exception re) {
          TLogger.log("Unexpection Exception in rollback:" + re );
        }
      }

      if (!pass1 || !pass2 )
            throw new Fault( "descriptorMappingTest8 failed");
      }



   /*
    *  Clean up
    */

    public void cleanup()  throws Fault
    {
	try {
	  getEntityTransaction().begin();

	  List projResults = getEntityManager().createQuery(
			"Select p from XProject p")
			.getResultList();

	   if ( projResults.size() != 0 ) {
             TLogger.log("Projects found: cleaning up ");
             iterator = projResults.iterator();
             while(iterator.hasNext()) {
             XProject projRef = (XProject) iterator.next();
             XProject newProj = getEntityManager().find(XProject.class, (long)projRef.getXProjId() );
                if (newProj != null ) {
                    getEntityManager().remove(newProj);
                }
             }
          }

          for (int i=1; i<21; i++ ) {
		XPerson newP = getEntityManager().find(XPerson.class, i);
		if (newP != null ) {
                    getEntityManager().remove(newP);
                    TLogger.log("removed person " + newP);
		}
          }

          for (int i=1; i<9; i++ ) {
		XAnnualReview newR = getEntityManager().find(XAnnualReview.class, new Integer(i));
		if (newR != null ) {
                    getEntityManager().remove(newR);
                    TLogger.log("removed annual review  " + newR);
		}
          }

          for (int i=1; i<6; i++ ) {
		XTeam newTeam = getEntityManager().find(XTeam.class, i);
		if (newTeam != null ) {
                    getEntityManager().remove(newTeam);
                    TLogger.log("removed team " + newTeam);
		}
          }


	  List cResults = getEntityManager().createQuery(
			"Select c from XCompany c")
			.getResultList();

	   if ( cResults.size() != 0 ) {
             TLogger.log("Companies found: cleaning up ");
             iterator = cResults.iterator();
             while(iterator.hasNext()) {
             XCompany compRef = (XCompany) iterator.next();
             XCompany newCompany = getEntityManager().find(XCompany.class, (long)compRef.getXCompanyId() );
                if (newCompany != null ) {
                    getEntityManager().remove(newCompany);
                }
             }
          }

          for (int i=1; i<4; i++ ) {
		XInsurance newIns = getEntityManager().find(XInsurance.class, i);
		if (newIns != null ) {
                    getEntityManager().remove(newIns);
                    TLogger.log("removed insurance " + newIns);
		}
          }


	  if (getEntityManager().find(XAddress.class, "100") != null ) {
              getEntityManager().remove(getEntityManager().find(XAddress.class, "100"));
              TLogger.log("removed address with PK 100");
	  }

	  if (getEntityManager().find(XAddress.class, "200") != null ) {
              getEntityManager().remove(getEntityManager().find(XAddress.class, "200"));
              TLogger.log("removed address with PK 200");
	  }

          getEntityTransaction().commit();

        } catch (Exception e) {
          TLogger.log("Exception caught cleaning up entities", e);
          e.printStackTrace();
        } finally {
          try {
              if (getEntityTransaction().isActive() ) {
                  getEntityTransaction().rollback();
              }
        } catch (Exception re) {
          TLogger.log("ERROR: Unexpected exception rolling back transaction");
          re.printStackTrace();
        }
        TLogger.log("Cleanup Complete, calling super.cleanup");
	super.cleanup();
       }

    }


   /* 
    *  Business Methods to set up data for Test Cases
    */

    private void createPeople() throws Exception
    {
        TLogger.log("createPeople");

        TLogger.log("Create 20 People");
        pRef[0] = new XPerson(1, "Alan", "Frechette");
        pRef[1] = new XPerson(2, "Arthur", "Frechette");
        pRef[2] = new XPerson(3, "Shelly", "McGowan");
        pRef[3] = new XPerson(4, "Robert", "Bissett");
        pRef[4] = new XPerson(5, "Stephen", "DMilla");
        pRef[5] = new XPerson(6, "Karen", "Tegan");
        pRef[6] = new XPerson(7, "Stephen", "Cruise");
        pRef[7] = new XPerson(8, "Irene", "Caruso");
        pRef[8] = new XPerson(9, "William", "Keaton");
        pRef[9] = new XPerson(10, "Kate", "Hudson");
        pRef[10] = new XPerson(11, "Jonathan", "Smith");
        pRef[11] = new XPerson(12, "Mary", "Macy");
        pRef[12] = new XPerson(13, "Cheng", "Fang");
        pRef[13] = new XPerson(14, "Julie", "OClaire");
        pRef[14] = new XPerson(15, "Steven", "Rich");
        pRef[15] = new XPerson(16, "Kellie", "Lee");
        pRef[16] = new XPerson(17, "Nicole", "Martin");
        pRef[17] = new XPerson(18, "Mark", "Francis");
        pRef[18] = new XPerson(19, "Will", "Forrest");
        pRef[19] = new XPerson(20, "Katy", "Hughes");

                TLogger.log("Start to persist people ");
        	for (int i=0; i<20; i++ ) {
		    getEntityTransaction().begin();
                    getEntityManager().persist(pRef[i]);
		    getEntityTransaction().commit();
                    TLogger.log("persisted person " + pRef[i]);
                }
    }

    private void createTeams() throws Exception
    {

        TLogger.log("Create 5 Teams");
        tRef[0] = new XTeam(1, "Engineering");
        tRef[1] = new XTeam(2, "Marketing");
        tRef[2] = new XTeam(3, "Sales");
        tRef[3] = new XTeam(4, "Accounting");
        tRef[4] = new XTeam(5, "Training");

        TLogger.log("Start to persist teams ");
        for (int i=0; i<5; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(tRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted team " + tRef[i]);
        }

     }

    private void createInsurance() throws Exception
    {
        TLogger.log("Create 3 Insurance Carriers");
        insRef[0] = new XInsurance(1, "Prudential");
        insRef[1] = new XInsurance(2, "Cigna");
        insRef[2] = new XInsurance(3, "Sentry");

        TLogger.log("Start to persist insurance ");
        for (int i=0; i<3; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(insRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted insurance " + insRef[i]);
        }

    }

    private void createProjects() throws Exception
    {
        TLogger.log("Create 5 Projects");
        projRef[0] = new XProject((long)123456789, "Sidewinder", new BigDecimal("20500.0"));
        projRef[1] = new XProject((long)234567890, "Boa", new BigDecimal("75000.0"));
        projRef[2] = new XProject((long)345678901, "Asp", new BigDecimal("500000.0"));
        projRef[3] = new XProject((long)456789012, "King Cobra", new BigDecimal("250000.0"));
        projRef[4] = new XProject((long)567890123, "Python", new BigDecimal("1000.0"));

        TLogger.log("Start to persist projects ");
        for (int i=0; i<5; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(projRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted project " + projRef[i]);
        }

    }

    private void createCompany() throws Exception
    {

        TLogger.log("Create 2 Companies");
        cRef[0] = new XCompany((long)25501, "American Gifts");
        cRef[1] = new XCompany((long)37560, "Planet Earth");

        TLogger.log("Start to persist companies ");
        for (int i=0; i<2; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(cRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted company " + cRef[i]);
        }

    }

    private void createAddress() throws Exception
    {
        TLogger.log("Create 2 Addresses");
        aRef[0] = new XAddress("100", "1 Network Drive", "Burlington", "MA", "01803");
        aRef[1] = new XAddress("200", "4150 Network Drive", "Santa Clara", "CA", "95054");

        TLogger.log("Start to persist addresses ");
        for (int i=0; i<2; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(aRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted address " + aRef[i]);
        }

    }

    private void createReviews() throws Exception
    {
        TLogger.log("Create 5 Addresses");
        rRef[0] = new XAnnualReview(new Integer(1), new Integer(1));
        rRef[1] = new XAnnualReview(new Integer(2), new Integer(2));
        rRef[2] = new XAnnualReview(new Integer(3), new Integer(3));
        rRef[3] = new XAnnualReview(new Integer(4), new Integer(4));
        rRef[4] = new XAnnualReview(new Integer(5), new Integer(5));
        rRef[5] = new XAnnualReview(new Integer(6), new Integer(6));
        rRef[6] = new XAnnualReview(new Integer(7), new Integer(7));
        rRef[7] = new XAnnualReview(new Integer(8), new Integer(8));

        TLogger.log("Start to persist annual reviews ");
        for (int i=0; i<8; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(rRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted annual reviews " + rRef[i]);
        }

    }


}

