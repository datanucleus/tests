 /*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)Client.java	1.13 06/04/12
 */
package com.sun.ts.tests.ejb30.persistence.relationship.defaults;

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

    private static Address aRef[] = new Address[5];
    private static AnnualReview rRef[] = new AnnualReview[10];
    private static Company cRef[] = new Company[5];
    private static Insurance insRef[] = new Insurance[5];
    private static Person pRef[] = new Person[20];
    private static Project projRef[] = new Project[10];
    private static Team tRef[] = new Team[10];
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
     * @testName: mappingTest1
     * @assertion_ids: PERSISTENCE:SPEC:578; PERSISTENCE:SPEC:943;
     *			PERSISTENCE:SPEC:961; PERSISTENCE:SPEC:1031;
     *			PERSISTENCE:SPEC:1041; PERSISTENCE:SPEC:1042;
     *			PERSISTENCE:SPEC:1043; PERSISTENCE:SPEC:1044
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest1() throws Fault
    {

      TLogger.log("Begin mappingTest1");
      boolean pass = false;

      try {
	createPeople();
	createProjects();
        getEntityTransaction().begin();

	pRef[0].setProject(projRef[2]);
	projRef[2].setProjectLead(pRef[0]);

	getEntityManager().merge(pRef[0]);
	getEntityManager().merge(projRef[2]);

	Person newPerson = getEntityManager().find(Person.class, 1);

	if (newPerson.getProject().getName().equals("Asp") ) {
                TLogger.log("mappingTest1: Expected results received");
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
            throw new Fault( "mappingTest1 failed");
    }


    /*
     * @testName: mappingTest2
     * @assertion_ids: PERSISTENCE:SPEC:579
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest2() throws Fault
    {

      TLogger.log("Begin mappingTest2");
      boolean pass1 = true;
      boolean pass2 = false;
      Vector<Team> v1 = null;
      Vector<Team> v2 = null;
      int foundTeam1 = 0;
      int foundTeam2 = 0;
      String[] expectedTeam1 = new String[] {"Engineering", "Marketing", "Sales"};
      String[] expectedTeam2 = new String[] {"Accounting", "Training"};


      try {
	createTeams();
	createCompany();

        getEntityTransaction().begin();

	tRef[0].setCompany(cRef[0]);
	tRef[1].setCompany(cRef[0]);
	tRef[2].setCompany(cRef[0]);

	v1 = new Vector<Team>();
	v1.add(tRef[0]);
	v1.add(tRef[1]);
	v1.add(tRef[2]);
	cRef[0].setTeams(v1);

	tRef[3].setCompany(cRef[1]);
	tRef[4].setCompany(cRef[1]);

	v2 = new Vector<Team>();
	v2.add(tRef[3]);
	v2.add(tRef[4]);
	cRef[1].setTeams(v2);

	getEntityManager().merge(tRef[0]);
	getEntityManager().merge(tRef[1]);
	getEntityManager().merge(tRef[2]);
	getEntityManager().merge(tRef[3]);
	getEntityManager().merge(tRef[4]);

	getEntityManager().merge(cRef[0]);
	getEntityManager().merge(cRef[1]);
	getEntityManager().flush();

	Company c1 = getEntityManager().find(Company.class, (long)25501);
	Collection<Team> t1 = c1.getTeams();
	
	Company c2 = getEntityManager().find(Company.class, (long)37560);
	Collection<Team> t2 = c2.getTeams();


	if( (t1.size() != 3) || (t2.size() != 2) ) {
                TLogger.log("ERROR:  mappingTest2: Did not get expected results." 
				+ "Team1 Collection Expected 3 references, got: "
                                + t1.size()
				+ ", Team2 Collection Expected 2 references, got: "
                                + t2.size() );
              pass1 = false;
            } else if (pass1) {

              Iterator i1 = t1.iterator();
              while (i1.hasNext()) {
                        TLogger.log("Check Team 1 Collection for expected Teams");
			Team o1 = (Team)i1.next();

                        for(int l=0; l<3; l++) {
                        if (expectedTeam1[l].equals((String)o1.getName()) ) {
                          TLogger.log("Found Team 1:"  + (String)o1.getName() );
                          foundTeam1++;
                          break;
                          }
                      }
              }

              Iterator i2 = t2.iterator();
              while (i2.hasNext()) {
                        TLogger.log("Check Team 2 Collection for expected Teams");
			Team o2 = (Team)i2.next();

                        for(int l=0; l<2; l++) {
                        if (expectedTeam2[l].equals((String)o2.getName()) ) {
                          TLogger.log("Found Team 2:" + (String)o2.getName() );
                          foundTeam2++;
                          break;
                          }
                      }
             }
	   }


           if ((foundTeam1 != 3) || (foundTeam2 != 2)) {
                      TLogger.log("ERROR: mappingTest2: Did not get expected results");
                      pass2 = false;
           } else {
                  TLogger.log(
                    "mappingTest2: Expected results received");
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
            throw new Fault( "mappingTest2 failed");
    }


    /*
     * @testName: mappingTest3
     * @assertion_ids: PERSISTENCE:SPEC:579
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest3() throws Fault
    {

      TLogger.log("Begin mappingTest3");
      boolean pass = false;
      Vector v1 = null;
      Vector v2 = null;

      try {
        createTeams();
        createCompany();

        getEntityTransaction().begin();

        tRef[0].setCompany(cRef[0]);
        tRef[1].setCompany(cRef[0]);

        v1 = new Vector();
        v1.add(tRef[0]);
        v1.add(tRef[1]);
        cRef[0].setTeams(v1);

        tRef[2].setCompany(cRef[1]);
        tRef[3].setCompany(cRef[1]);
        tRef[4].setCompany(cRef[1]);

        v2 = new Vector();
        v2.add(tRef[2]);
        v2.add(tRef[3]);
        v2.add(tRef[4]);

        cRef[1].setTeams(v2);

        getEntityManager().merge(tRef[0]);
        getEntityManager().merge(tRef[1]);
        getEntityManager().merge(tRef[2]);
        getEntityManager().merge(tRef[3]);
        getEntityManager().merge(tRef[4]);

        getEntityManager().merge(cRef[0]);
        getEntityManager().merge(cRef[1]);

        getEntityManager().flush();

        Team t1 = getEntityManager().find(Team.class, 1);
        Team t2 = getEntityManager().find(Team.class, 2);
        Team t3 = getEntityManager().find(Team.class, 3);
        Team t4 = getEntityManager().find(Team.class, 4);
        Team t5 = getEntityManager().find(Team.class, 5);


	if ( ( t1.getCompany().getCompanyId() == (long)25501) &&
	     ( t2.getCompany().getCompanyId() == (long)25501) &&
	     ( t3.getCompany().getCompanyId() == (long)37560) &&
	     ( t4.getCompany().getCompanyId() == (long)37560) &&
	     ( t5.getCompany().getCompanyId() == (long)37560) )
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
            throw new Fault( "mappingTest3 failed");
    }


    /*
     * @testName: mappingTest4
     * @assertion_ids: PERSISTENCE:SPEC:580; PERSISTENCE:SPEC:581
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest4() throws Fault
    {

    TLogger.log("Begin mappingTest4");
    boolean pass = false;


	try {

    	  createAddress();
    	  createCompany();

          getEntityTransaction().begin();

          cRef[0].setAddress(aRef[0]);
          cRef[1].setAddress(aRef[1]);

          getEntityManager().merge(cRef[0]);
          getEntityManager().merge(cRef[1]);
 
	  getEntityManager().flush();

          Company c1 = getEntityManager().find(Company.class, (long)25501);
          Company c2 = getEntityManager().find(Company.class, (long)37560);

	  if (c1.getAddress().getCity().equals("Burlington") &&
		c2.getAddress().getCity().equals("Santa Clara") ) {
    		TLogger.log("Expected results received");
		pass = true;
	  } else {
    		TLogger.log("mappingTest4: Did not get expected results" +
			"Expected: Burlington and Santa Clara, got: " +
			c1.getAddress().getCity() + c2.getAddress().getCity() );
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
            throw new Fault( "mappingTest4 failed");
    }

    /*
     * @testName: mappingTest5
     * @assertion_ids: PERSISTENCE:SPEC:580; PERSISTENCE:SPEC:582
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest5() throws Fault
    {
      TLogger.log("Begin mappingTest5");
      boolean pass = false;

      try {
        createTeams();
        createPeople();

        getEntityTransaction().begin();

        pRef[1].setTeam(tRef[0]);
        pRef[3].setTeam(tRef[1]);
        pRef[5].setTeam(tRef[2]);
        pRef[7].setTeam(tRef[3]);
        pRef[9].setTeam(tRef[4]);

        pRef[2].setTeam(tRef[4]);
        pRef[4].setTeam(tRef[3]);
        pRef[6].setTeam(tRef[2]);
        pRef[8].setTeam(tRef[1]);
        pRef[10].setTeam(tRef[0]);


	for (int i=1; i<11; i++) {
             getEntityManager().merge(pRef[i]);
	}

        getEntityManager().flush();

            if ( ( pRef[1].getTeam() == tRef[0] )   && ( pRef[10].getTeam() == tRef[0] )   &&
               ( pRef[3].getTeam() == tRef[1] )  && ( pRef[8].getTeam() == tRef[1] )   &&
               ( pRef[5].getTeam() == tRef[2] )  && ( pRef[6].getTeam() == tRef[2] )   &&
               ( pRef[7].getTeam() == tRef[3] )  && ( pRef[4].getTeam() == tRef[3] )   &&
               ( pRef[9].getTeam() == tRef[4] )  && ( pRef[2].getTeam() == tRef[4] ) ) {

             TLogger.log(
                    "mappingTest5: Expected results received");
              pass = true;
           } else {
                  TLogger.log(
                    "mappingTest5: Did not get expected results ");
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
            throw new Fault( "mappingTest5 failed");
    }


    /*
     * @testName: mappingTest6
     * @assertion_ids: PERSISTENCE:SPEC:583; PERSISTENCE:SPEC:1045
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest6() throws Fault
    {
      TLogger.log("Begin mappingTest6");
      boolean pass1 = true;
      boolean pass2 = false;
      Vector<Team> v1 = null;
      Vector<Team> v2 = null;
      int foundProject1 = 0;
      int foundProject2 = 0;
      Long[] expectedResults1 = new Long[] {new Long(123456789), new Long(345678901), new Long(567890123) };
      Long[] expectedResults2 = new Long[] {new Long(234567890), new Long(345678901), new Long(456789012) };

      try {
        createPeople();
        createProjects();

        getEntityTransaction().begin();


	pRef[5].getProjects().add(projRef[0]);
	pRef[5].getProjects().add(projRef[2]);
	pRef[5].getProjects().add(projRef[4]);
	getEntityManager().merge(pRef[5]);


	pRef[8].getProjects().add(projRef[1]);
	pRef[8].getProjects().add(projRef[2]);
	pRef[8].getProjects().add(projRef[3]);
	getEntityManager().merge(pRef[8]);

	projRef[0].getPersons().add(pRef[5]);
	getEntityManager().merge(projRef[0]);

	projRef[1].getPersons().add(pRef[8]);
	getEntityManager().merge(projRef[1]);

	projRef[2].getPersons().add(pRef[5]);
	getEntityManager().merge(projRef[2]);

	projRef[2].getPersons().add(pRef[8]);
	getEntityManager().merge(projRef[2]);

	projRef[3].getPersons().add(pRef[8]);
	getEntityManager().merge(projRef[3]);


	projRef[4].getPersons().add(pRef[5]);
	getEntityManager().merge(projRef[4]);


	getEntityManager().flush();

	Person p1 = getEntityManager().find(Person.class, 6);
	Person p2 = getEntityManager().find(Person.class, 9);

	Collection<Project> projCol1 = p1.getProjects();
	Collection<Project> projCol2 = p2.getProjects();

        if( (projCol1.size() != 3) || (projCol2.size() != 3 ) ) {
                TLogger.log("ERROR:  mappingTest6: Did not get expected results."
                                + "Expected 3 Projects for Karen Tegan (PK 6) , got: "
                                + projCol1.size()
                                + ", Expected 2 Projects for William Keaton (PK 9), got: "
                                + projCol2.size() );
              pass1 = false;
        } else if (pass1) {

           Iterator i1 = projCol1.iterator();
           while (i1.hasNext()) {
                     TLogger.log("Check Collection for Karen Tegan Projects");
                     Project o1 = (Project)i1.next();

                     for(int l=0; l<3; l++) {
                     if (expectedResults1[l].equals((Long)o1.getProjId()) ) {
                         TLogger.log("Found Project for Karen Tegan: "
                                 + (String)o1.getName() );
                          foundProject1++;
                          break;
                          }
                      }
            }


            Iterator i2 = projCol2.iterator();
            while (i2.hasNext()) {
                     TLogger.log("Check Collection for William Keaton Projects");
                     Project o2 = (Project)i2.next();

                     for(int l=0; l<3; l++) {
                     if (expectedResults2[l].equals((Long)o2.getProjId()) ) {
                         TLogger.log("Found Project for William Keaton: "
                                 + (String)o2.getName() );
                         foundProject2++;
                         break;
                         }
                      }
            }

	}

          if ( (foundProject1 != 3) || (foundProject2 != 3) ) {
                TLogger.log("ERROR: mappingTest6: Did not get expected results");
                pass2 = false;
           } else {
                  TLogger.log(
                    "mappingTest6: Expected results received");
                  pass2 = true;
           }


        Collection<Person> nullPersonCol = new Vector<Person>();
        projRef[0].setPersons(nullPersonCol);
        getEntityManager().merge(projRef[0]);

        projRef[1].setPersons(nullPersonCol);
        getEntityManager().merge(projRef[1]);

        projRef[2].setPersons(nullPersonCol);
        getEntityManager().merge(projRef[2]);

        projRef[3].setPersons(nullPersonCol);
        getEntityManager().merge(projRef[3]);

        projRef[4].setPersons(nullPersonCol);
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
            throw new Fault( "mappingTest6 failed");
      }


    /*
     * @testName: mappingTest7
     * @assertion_ids: PERSISTENCE:SPEC:584; PERSISTENCE:SPEC:585
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest7() throws Fault
    {
    TLogger.log("Begin mappingTest7");
      boolean pass1 = true;
      boolean pass2 = false;

      Vector<AnnualReview> v1 = null;
      Vector<AnnualReview> v2 = null;
      Vector<AnnualReview> v3 = null;
      Vector<AnnualReview> v4 = null;
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

        v1 = new Vector<AnnualReview>();
        v1.add(rRef[0]);
        v1.add(rRef[1]);
        v1.add(rRef[2]);
        v1.add(rRef[3]);

        v2 = new Vector<AnnualReview>();
        v2.add(rRef[4]);
        v2.add(rRef[5]);

        v3 = new Vector<AnnualReview>();
        v3.add(rRef[2]);
        v3.add(rRef[7]);

        v4 = new Vector<AnnualReview>();
        v4.add(rRef[3]);
        v4.add(rRef[6]);


        pRef[11].setAnnualReviews(v1);
        pRef[13].setAnnualReviews(v2);
        pRef[15].setAnnualReviews(v3);
        pRef[17].setAnnualReviews(v4);
        pRef[19].setAnnualReviews(v1);


        getEntityManager().merge(pRef[11]);
        getEntityManager().merge(pRef[13]);
        getEntityManager().merge(pRef[15]);
        getEntityManager().merge(pRef[17]);
        getEntityManager().merge(pRef[19]);

        getEntityManager().flush();

        Person p1 = getEntityManager().find(Person.class, 12);
        Person p2 = getEntityManager().find(Person.class, 14);
        Person p3 = getEntityManager().find(Person.class, 16);
        Person p4 = getEntityManager().find(Person.class, 18);
        Person p5 = getEntityManager().find(Person.class, 20);


	Collection<AnnualReview> col1 = p1.getAnnualReviews();
	Collection<AnnualReview> col2 = p2.getAnnualReviews();
	Collection<AnnualReview> col3 = p3.getAnnualReviews();
	Collection<AnnualReview> col4 = p4.getAnnualReviews();
	Collection<AnnualReview> col5 = p5.getAnnualReviews();

        if( (col1.size() != 4) || (col2.size() != 2 ||
		col3.size() != 2 || col4.size() != 2 || col5.size() != 4 ) ) {
                TLogger.log("ERROR:  mappingTest7: Did not get expected results."
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
                        AnnualReview o1 = (AnnualReview)i1.next();

                        for(int l=0; l<5; l++) {
                        if (expectedCol1[l].equals((Integer)o1.getService()) ) {
                          TLogger.log("Found Mary Macy Annual Review for Service Year: "
				 + (Integer)o1.getService() );
                          foundCol1++;
                          break;
                          }
                      }
              }


              Iterator i2 = col2.iterator();
              while (i2.hasNext()) {
                        TLogger.log("Check Collection for Julie OClaire Reviews");
                        AnnualReview o2 = (AnnualReview)i2.next();

                        for(int l=0; l<2; l++) {
                        if (expectedCol2[l].equals((Integer)o2.getService()) ) {
                          TLogger.log("Found Julie OClaire Annual Review for Service Year: "
				 + (Integer)o2.getService() );
                          foundCol2++;
                          break;
                          }
                      }
              }

              Iterator i3 = col3.iterator();
              while (i3.hasNext()) {
                        TLogger.log("Check Collection for Kellie Lee Reviews");
                        AnnualReview o3 = (AnnualReview)i3.next();

                        for(int l=0; l<2; l++) {
                        if (expectedCol3[l].equals((Integer)o3.getService()) ) {
                          TLogger.log("Found Kellie Lee Annual Review for Service Year: " 
				+ (Integer)o3.getService() );
                          foundCol3++;
                          break;
                          }
                      }
              }


              Iterator i4 = col4.iterator();
              while (i4.hasNext()) {
                        TLogger.log("Check Collection for Mark Francis Reviews");
                        AnnualReview o4 = (AnnualReview)i4.next();

                        for(int l=0; l<2; l++) {
                        if (expectedCol4[l].equals((Integer)o4.getService()) ) {
                          TLogger.log("Found Mark Francis Annual Review for Service Year: "
				+ (Integer)o4.getService() );
                          foundCol4++;
                          break;
                          }
                      }
              }


              Iterator i5 = col5.iterator();
              while (i5.hasNext()) {
                        TLogger.log("Check Collection for Katy Hughes Reviews");
                        AnnualReview o5 = (AnnualReview)i5.next();

                        for(int l=0; l<5; l++) {
                        if (expectedCol5[l].equals((Integer)o5.getService()) ) {
                          TLogger.log("Found Katy Hughes Annual Review for Service Year: "
				+ (Integer)o5.getService() );
                          foundCol5++;
                          break;
                          }
                      }
              }

           }

          if ( (foundCol1 != 4) || (foundCol2 != 2) ||
 		(foundCol3 != 2) || (foundCol4 != 2) || (foundCol5 != 4 ) ) {

                TLogger.log("ERROR: mappingTest7: Did not get expected results");
                pass2 = false;
           } else {
                  TLogger.log(
                    "mappingTest7: Expected results received");
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
            throw new Fault( "mappingTest7 failed");
    }



    /*
     * @testName: mappingTest8
     * @assertion_ids: PERSISTENCE:SPEC:584; PERSISTENCE:SPEC:586
     * @test_Strategy: RelationShip Mapping Defaults
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

    public void mappingTest8() throws Fault
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


	pRef[2].getInsurance().add(insRef[0]);
	pRef[2].getInsurance().add(insRef[2]);
	getEntityManager().merge(pRef[2]);

	pRef[12].getInsurance().add(insRef[1]);
	pRef[12].getInsurance().add(insRef[2]);
	getEntityManager().merge(pRef[12]);

	pRef[16].getInsurance().add(insRef[0]);
	pRef[16].getInsurance().add(insRef[1]);
	pRef[16].getInsurance().add(insRef[2]);
	getEntityManager().merge(pRef[16]);


	getEntityManager().flush();

	Person p1 = getEntityManager().find(Person.class, 3);
	Person p2 = getEntityManager().find(Person.class, 13);
	Person p3 = getEntityManager().find(Person.class, 17);

	Collection<Insurance> insCol1 = p1.getInsurance();
	Collection<Insurance> insCol2 = p2.getInsurance();
	Collection<Insurance> insCol3 = p3.getInsurance();

        if( (insCol1.size() != 2) || (insCol2.size() != 2 ) || (insCol3.size() != 3) ) {
                TLogger.log("ERROR:  mappingTest8: Did not get expected results."
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
                     Insurance o1 = (Insurance)i1.next();

                     for(int l=0; l<2; l++) {
                     if (expectedResults1[l].equals((Integer)o1.getInsId()) ) {
                         TLogger.log("Found Insurance Carrier for Shelly McGowan: "
                                 + (String)o1.getCarrier() );
                          foundInsurance1++;
                          break;
                          }
                      }
            }

           Iterator i2 = insCol2.iterator();
           while (i2.hasNext()) {
                     TLogger.log("Check Insurance Carriers for Cheng Fang");
                     Insurance o2 = (Insurance)i2.next();

                     for(int l=0; l<2; l++) {
                     if (expectedResults2[l].equals((Integer)o2.getInsId()) ) {
                         TLogger.log("Found Insurance Carrier for Cheng Fang: "
                                 + (String)o2.getCarrier() );
                          foundInsurance2++;
                          break;
                          }
                      }
            }

           Iterator i3 = insCol3.iterator();
           while (i3.hasNext()) {
                     TLogger.log("Check Insurance Carriers for Nicole Martin");
                     Insurance o3 = (Insurance)i3.next();

                     for(int l=0; l<3; l++) {
                     if (expectedResults3[l].equals((Integer)o3.getInsId()) ) {
                         TLogger.log("Found Insurance Carrier for Nicole Martin: "
                                 + (String)o3.getCarrier() );
                          foundInsurance3++;
                          break;
                          }
                      }
            }

	}

        if ( (foundInsurance1 != 2) || (foundInsurance2 != 2) || (foundInsurance3 != 3) ) {
                TLogger.log("ERROR: mappingTest8: Did not get expected results");
                pass2 = false;
        } else {
               TLogger.log(
                    "mappingTest8: Expected results received");
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
            throw new Fault( "mappingTest8 failed");
      }



   /*
    *  Clean up
    */

    public void cleanup()  throws Fault
    {
	try {
	  getEntityTransaction().begin();

	  List projResults = getEntityManager().createQuery(
			"Select p from Project p")
			.getResultList();

	   if ( projResults.size() != 0 ) {
             TLogger.log("Projects found: cleaning up ");
             iterator = projResults.iterator();
             while(iterator.hasNext()) {
             Project projRef = (Project) iterator.next();
             Project newProj = getEntityManager().find(Project.class, (long)projRef.getProjId() );
                if (newProj != null ) {
                    getEntityManager().remove(newProj);
                }
             }
          }

          for (int i=1; i<21; i++ ) {
		Person newP = getEntityManager().find(Person.class, i);
		if (newP != null ) {
                    getEntityManager().remove(newP);
                    TLogger.log("removed person " + newP);
		}
          }

          for (int i=1; i<9; i++ ) {
		AnnualReview newR = getEntityManager().find(AnnualReview.class, new Integer(i));
		if (newR != null ) {
                    getEntityManager().remove(newR);
                    TLogger.log("removed annual review  " + newR);
		}
          }

          for (int i=1; i<6; i++ ) {
		Team newTeam = getEntityManager().find(Team.class, i);
		if (newTeam != null ) {
                    getEntityManager().remove(newTeam);
                    TLogger.log("removed team " + newTeam);
		}
          }


	  List cResults = getEntityManager().createQuery(
			"Select c from Company c")
			.getResultList();

	   if ( cResults.size() != 0 ) {
             TLogger.log("Companies found: cleaning up ");
             iterator = cResults.iterator();
             while(iterator.hasNext()) {
             Company compRef = (Company) iterator.next();
             Company newCompany = getEntityManager().find(Company.class, (long)compRef.getCompanyId() );
                if (newCompany != null ) {
                    getEntityManager().remove(newCompany);
                }
             }
          }

          for (int i=1; i<4; i++ ) {
		Insurance newIns = getEntityManager().find(Insurance.class, i);
		if (newIns != null ) {
                    getEntityManager().remove(newIns);
                    TLogger.log("removed insurance " + newIns);
		}
          }


	  if (getEntityManager().find(Address.class, "100") != null ) {
              getEntityManager().remove(getEntityManager().find(Address.class, "100"));
              TLogger.log("removed address with PK 100");
	  }

	  if (getEntityManager().find(Address.class, "200") != null ) {
              getEntityManager().remove(getEntityManager().find(Address.class, "200"));
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
        pRef[0] = new Person(1, "Alan", "Frechette");
        pRef[1] = new Person(2, "Arthur", "Frechette");
        pRef[2] = new Person(3, "Shelly", "McGowan");
        pRef[3] = new Person(4, "Robert", "Bissett");
        pRef[4] = new Person(5, "Stephen", "DMilla");
        pRef[5] = new Person(6, "Karen", "Tegan");
        pRef[6] = new Person(7, "Stephen", "Cruise");
        pRef[7] = new Person(8, "Irene", "Caruso");
        pRef[8] = new Person(9, "William", "Keaton");
        pRef[9] = new Person(10, "Kate", "Hudson");
        pRef[10] = new Person(11, "Jonathan", "Smith");
        pRef[11] = new Person(12, "Mary", "Macy");
        pRef[12] = new Person(13, "Cheng", "Fang");
        pRef[13] = new Person(14, "Julie", "OClaire");
        pRef[14] = new Person(15, "Steven", "Rich");
        pRef[15] = new Person(16, "Kellie", "Lee");
        pRef[16] = new Person(17, "Nicole", "Martin");
        pRef[17] = new Person(18, "Mark", "Francis");
        pRef[18] = new Person(19, "Will", "Forrest");
        pRef[19] = new Person(20, "Katy", "Hughes");

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
        tRef[0] = new Team(1, "Engineering");
        tRef[1] = new Team(2, "Marketing");
        tRef[2] = new Team(3, "Sales");
        tRef[3] = new Team(4, "Accounting");
        tRef[4] = new Team(5, "Training");

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
        insRef[0] = new Insurance(1, "Prudential");
        insRef[1] = new Insurance(2, "Cigna");
        insRef[2] = new Insurance(3, "Sentry");

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
        projRef[0] = new Project((long)123456789, "Sidewinder", new BigDecimal("20500.0"));
        projRef[1] = new Project((long)234567890, "Boa", new BigDecimal("75000.0"));
        projRef[2] = new Project((long)345678901, "Asp", new BigDecimal("500000.0"));
        projRef[3] = new Project((long)456789012, "King Cobra", new BigDecimal("250000.0"));
        projRef[4] = new Project((long)567890123, "Python", new BigDecimal("1000.0"));

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
        cRef[0] = new Company((long)25501, "American Gifts");
        cRef[1] = new Company((long)37560, "Planet Earth");

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
        aRef[0] = new Address("100", "1 Network Drive", "Burlington", "MA", "01803");
        aRef[1] = new Address("200", "4150 Network Drive", "Santa Clara", "CA", "95054");

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
        rRef[0] = new AnnualReview(new Integer(1), new Integer(1));
        rRef[1] = new AnnualReview(new Integer(2), new Integer(2));
        rRef[2] = new AnnualReview(new Integer(3), new Integer(3));
        rRef[3] = new AnnualReview(new Integer(4), new Integer(4));
        rRef[4] = new AnnualReview(new Integer(5), new Integer(5));
        rRef[5] = new AnnualReview(new Integer(6), new Integer(6));
        rRef[6] = new AnnualReview(new Integer(7), new Integer(7));
        rRef[7] = new AnnualReview(new Integer(8), new Integer(8));

        TLogger.log("Start to persist annual reviews ");
        for (int i=0; i<8; i++ ) {
	    getEntityTransaction().begin();
            getEntityManager().persist(rRef[i]);
	    getEntityTransaction().commit();
            TLogger.log("persisted annual reviews " + rRef[i]);
        }

    }


}

