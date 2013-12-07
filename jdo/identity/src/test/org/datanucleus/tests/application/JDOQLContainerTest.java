/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 

Contributors:
2003 Mike Martin - original tests in JDOQLQueryTest
2004 Erik Bengtson - added many many tests
    ...
***********************************************************************/
package org.datanucleus.tests.application;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.nightlabs_payments.ModeOfPayment;
import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour;
import org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor;
import org.jpox.samples.models.nightlabs_product.Product;
import org.jpox.samples.models.nightlabs_product.ProductTransfer;
import org.jpox.samples.models.nightlabs_product.ProductType;
import org.jpox.samples.models.nightlabs_product.Transfer;

/**
 * Tests for JDOQL queries of collections and maps.
 */
public class JDOQLContainerTest extends JDOPersistenceTestCase
{
    public JDOQLContainerTest(String name)
    {
        super(name);
    }

    /**
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsValueRangeVariableAlreadyInQuery()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ModeOfPayment cash;
                ModeOfPaymentFlavour dollar;
                ServerPaymentProcessor cashManual;
                String organisationID = "orga00.jfire.org";

                // create Cash
                cash = new ModeOfPayment(organisationID, "Cash");
                dollar = new ModeOfPaymentFlavour(organisationID, "Dollar");
                cash.addModeOfPaymentFlavour(dollar);
                cashManual = new ServerPaymentProcessor(organisationID, "CashManualPayment");
                cashManual.addModeOfPayment(cash);
                pm.makePersistent(cashManual);

                // create CreditCard with VISA and MasterCard
                ModeOfPayment creditCard = new ModeOfPayment(organisationID, "CreditCard");
                ServerPaymentProcessor clientSide = new ServerPaymentProcessor(organisationID, "ClientSidedCreditCardPayment");
                clientSide.addModeOfPayment(creditCard);
                pm.makePersistent(clientSide);
                ServerPaymentProcessor saferPay = new ServerPaymentProcessor(organisationID, "SaferPay");
                saferPay.addModeOfPayment(creditCard);
                pm.makePersistent(saferPay);
                ServerPaymentProcessor wireCard = new ServerPaymentProcessor(organisationID, "WireCard");
                wireCard.addModeOfPayment(creditCard);
                pm.makePersistent(wireCard);

                ModeOfPaymentFlavour visa = new ModeOfPaymentFlavour(organisationID, "Visa");
                creditCard.addModeOfPaymentFlavour(visa);
                ServerPaymentProcessor cardVisaProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_Visa");
                cardVisaProcessor.addModeOfPayment(creditCard);
                pm.makePersistent(cardVisaProcessor);

                ModeOfPaymentFlavour masterCard = new ModeOfPaymentFlavour(organisationID, "MasterCard");
                creditCard.addModeOfPaymentFlavour(masterCard);
                ServerPaymentProcessor cardMasterCardProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_MasterCard");
                cardMasterCardProcessor.addModeOfPayment(creditCard);
                pm.makePersistent(cardMasterCardProcessor);

                // create DebitNote
                ServerPaymentProcessor debitNoteProcessor = new ServerPaymentProcessor(organisationID, "DebitNotePayment");
                ModeOfPayment debit = new ModeOfPayment(organisationID, "Debit");
                ModeOfPaymentFlavour debitNote = new ModeOfPaymentFlavour(organisationID, "DebitNote");
                debit.addModeOfPaymentFlavour(debitNote);
                debitNoteProcessor.addModeOfPayment(debit);
                pm.makePersistent(debitNoteProcessor);

                tx.commit();
                
                tx.begin();
                
                Query q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPayments.containsValue(modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPayment"
                    );
                Collection c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  this.modeOfPayments.containsValue(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());

                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPayments.containsValue(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
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
        finally
        {
            clean(ServerPaymentProcessor.class);
            clean(ModeOfPaymentFlavour.class);
            clean(ModeOfPayment.class);
        }
    }

    /**
     * Test for the Map.containsKey() method.
     */
    public void testQueryUsesContainsKeyRangeVariableAlreadyInQuery()
    {
        try
        {
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ModeOfPayment cash;
                ModeOfPaymentFlavour dollar;
                ServerPaymentProcessor cashManual;
                String organisationID = "orga00.jfire.org";
                
                // create Cash
                cash = new ModeOfPayment(organisationID, "Cash");
                dollar = new ModeOfPaymentFlavour(organisationID, "Dollar");
                cash.addModeOfPaymentFlavourKey(dollar);
                cashManual = new ServerPaymentProcessor(organisationID, "CashManualPayment");
                cashManual.addModeOfPaymentKey(cash);
                pm.makePersistent(cashManual);

                // create CreditCard with VISA and MasterCard
                ModeOfPayment creditCard = new ModeOfPayment(organisationID, "CreditCard");
                ServerPaymentProcessor clientSide = new ServerPaymentProcessor(organisationID, "ClientSidedCreditCardPayment");
                clientSide.addModeOfPaymentKey(creditCard);
                pm.makePersistent(clientSide);
                ServerPaymentProcessor saferPay = new ServerPaymentProcessor(organisationID, "SaferPay");
                saferPay.addModeOfPaymentKey(creditCard);
                pm.makePersistent(saferPay);
                ServerPaymentProcessor wireCard = new ServerPaymentProcessor(organisationID, "WireCard");
                wireCard.addModeOfPaymentKey(creditCard);
                pm.makePersistent(wireCard);

                ModeOfPaymentFlavour visa = new ModeOfPaymentFlavour(organisationID, "Visa");
                creditCard.addModeOfPaymentFlavourKey(visa);
                ServerPaymentProcessor cardVisaProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_Visa");
                cardVisaProcessor.addModeOfPaymentKey(creditCard);
                pm.makePersistent(cardVisaProcessor);

                ModeOfPaymentFlavour masterCard = new ModeOfPaymentFlavour(organisationID, "MasterCard");
                creditCard.addModeOfPaymentFlavourKey(masterCard);
                ServerPaymentProcessor cardMasterCardProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_MasterCard");
                cardMasterCardProcessor.addModeOfPaymentKey(creditCard);
                pm.makePersistent(cardMasterCardProcessor);

                // create DebitNote
                ServerPaymentProcessor debitNoteProcessor = new ServerPaymentProcessor(organisationID, "DebitNotePayment");
                ModeOfPayment debit = new ModeOfPayment(organisationID, "Debit");
                ModeOfPaymentFlavour debitNote = new ModeOfPaymentFlavour(organisationID, "DebitNote");
                debit.addModeOfPaymentFlavourKey(debitNote);
                debitNoteProcessor.addModeOfPaymentKey(debit);
                pm.makePersistent(debitNoteProcessor);

                tx.commit();
                
                tx.begin();
                
                Query q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsKey.containsKey(modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPayment"
                    );
                Collection c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  this.modeOfPaymentsKey.containsKey(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());

                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsKey.containsKey(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
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
        finally
        {
            clean(ServerPaymentProcessor.class);
            clean(ModeOfPaymentFlavour.class);
            clean(ModeOfPayment.class);
        }
    }

    /**
     * Test for the Map.containsEntry() method.
     */
    public void testQueryUsesContainsEntryRangeVariableAlreadyInQuery()
    {
        try
        {
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ModeOfPayment cash;
                ModeOfPaymentFlavour dollar;
                ServerPaymentProcessor cashManual;
                String organisationID = "orga00.jfire.org";
                
                // create Cash
                cash = new ModeOfPayment(organisationID, "Cash");
                dollar = new ModeOfPaymentFlavour(organisationID, "Dollar");
                cash.addModeOfPaymentFlavourKey(dollar);
                cashManual = new ServerPaymentProcessor(organisationID, "CashManualPayment");
                cashManual.addModeOfPaymentKey(cash);
                pm.makePersistent(cashManual);

                // create CreditCard with VISA and MasterCard
                ModeOfPayment creditCard = new ModeOfPayment(organisationID, "CreditCard");
                ServerPaymentProcessor clientSide = new ServerPaymentProcessor(organisationID, "ClientSidedCreditCardPayment");
                clientSide.addModeOfPaymentKey(creditCard);
                pm.makePersistent(clientSide);
                ServerPaymentProcessor saferPay = new ServerPaymentProcessor(organisationID, "SaferPay");
                saferPay.addModeOfPaymentKey(creditCard);
                pm.makePersistent(saferPay);
                ServerPaymentProcessor wireCard = new ServerPaymentProcessor(organisationID, "WireCard");
                wireCard.addModeOfPaymentKey(creditCard);
                pm.makePersistent(wireCard);

                ModeOfPaymentFlavour visa = new ModeOfPaymentFlavour(organisationID, "Visa");
                creditCard.addModeOfPaymentFlavourKey(visa);
                ServerPaymentProcessor cardVisaProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_Visa");
                cardVisaProcessor.addModeOfPaymentKey(creditCard);
                pm.makePersistent(cardVisaProcessor);

                ModeOfPaymentFlavour masterCard = new ModeOfPaymentFlavour(organisationID, "MasterCard");
                creditCard.addModeOfPaymentFlavourKey(masterCard);
                ServerPaymentProcessor cardMasterCardProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_MasterCard");
                cardMasterCardProcessor.addModeOfPaymentKey(creditCard);
                pm.makePersistent(cardMasterCardProcessor);

                // create DebitNote
                ServerPaymentProcessor debitNoteProcessor = new ServerPaymentProcessor(organisationID, "DebitNotePayment");
                ModeOfPayment debit = new ModeOfPayment(organisationID, "Debit");
                ModeOfPaymentFlavour debitNote = new ModeOfPaymentFlavour(organisationID, "DebitNote");
                debit.addModeOfPaymentFlavourKey(debitNote);
                debitNoteProcessor.addModeOfPaymentKey(debit);
                pm.makePersistent(debitNoteProcessor);

                tx.commit();
                
                tx.begin();
                
                Query q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsKey.containsEntry(modeOfPayment,modeOfPayment.primaryKey)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPayment"
                    );
                Collection c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  this.modeOfPaymentsKey.containsEntry(modeOfPaymentFlavour.modeOfPayment,modeOfPaymentFlavour.modeOfPayment.primaryKey)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());

                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsKey.containsEntry(modeOfPaymentFlavour.modeOfPayment,modeOfPaymentFlavour.modeOfPayment.primaryKey)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
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
        finally
        {
            clean(ServerPaymentProcessor.class);
            clean(ModeOfPaymentFlavour.class);
            clean(ModeOfPayment.class);
        }
    }
    
    /**
     * Test for the Set.contains() method.
     */
    public void testQueryUsesContainsSetRangeVariableAlreadyInQuery()
    {
        try
        {
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ModeOfPayment cash;
                ModeOfPaymentFlavour dollar;
                ServerPaymentProcessor cashManual;
                String organisationID = "orga00.jfire.org";
                
                // create Cash
                cash = new ModeOfPayment(organisationID, "Cash");
                dollar = new ModeOfPaymentFlavour(organisationID, "Dollar");
                cash.addModeOfPaymentFlavourSet(dollar);
                cashManual = new ServerPaymentProcessor(organisationID, "CashManualPayment");
                cashManual.addModeOfPaymentSet(cash);
                pm.makePersistent(cashManual);

                // create CreditCard with VISA and MasterCard
                ModeOfPayment creditCard = new ModeOfPayment(organisationID, "CreditCard");
                ServerPaymentProcessor clientSide = new ServerPaymentProcessor(organisationID, "ClientSidedCreditCardPayment");
                clientSide.addModeOfPaymentSet(creditCard);
                pm.makePersistent(clientSide);
                ServerPaymentProcessor saferPay = new ServerPaymentProcessor(organisationID, "SaferPay");
                saferPay.addModeOfPaymentSet(creditCard);
                pm.makePersistent(saferPay);
                ServerPaymentProcessor wireCard = new ServerPaymentProcessor(organisationID, "WireCard");
                wireCard.addModeOfPaymentSet(creditCard);
                pm.makePersistent(wireCard);

                ModeOfPaymentFlavour visa = new ModeOfPaymentFlavour(organisationID, "Visa");
                creditCard.addModeOfPaymentFlavourSet(visa);
                ServerPaymentProcessor cardVisaProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_Visa");
                cardVisaProcessor.addModeOfPaymentSet(creditCard);
                pm.makePersistent(cardVisaProcessor);

                ModeOfPaymentFlavour masterCard = new ModeOfPaymentFlavour(organisationID, "MasterCard");
                creditCard.addModeOfPaymentFlavourSet(masterCard);
                ServerPaymentProcessor cardMasterCardProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_MasterCard");
                cardMasterCardProcessor.addModeOfPaymentSet(creditCard);
                pm.makePersistent(cardMasterCardProcessor);

                // create DebitNote
                ServerPaymentProcessor debitNoteProcessor = new ServerPaymentProcessor(organisationID, "DebitNotePayment");
                ModeOfPayment debit = new ModeOfPayment(organisationID, "Debit");
                ModeOfPaymentFlavour debitNote = new ModeOfPaymentFlavour(organisationID, "DebitNote");
                debit.addModeOfPaymentFlavourSet(debitNote);
                debitNoteProcessor.addModeOfPaymentSet(debit);
                pm.makePersistent(debitNoteProcessor);

                tx.commit();
                
                tx.begin();
                
                Query q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsSet.contains(modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPayment"
                    );
                Collection c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  this.modeOfPaymentsSet.contains(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());

                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsSet.contains(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
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
        finally
        {
            clean(ServerPaymentProcessor.class);
            clean(ModeOfPaymentFlavour.class);
            clean(ModeOfPayment.class);
        }
    }

    /**
     * Test for the List.contains() method.
     */
    public void testQueryUsesContainsListRangeVariableAlreadyInQuery()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ModeOfPayment cash;
                ModeOfPaymentFlavour dollar;
                ServerPaymentProcessor cashManual;
                String organisationID = "orga00.jfire.org";
                
                // create Cash
                cash = new ModeOfPayment(organisationID, "Cash");
                dollar = new ModeOfPaymentFlavour(organisationID, "Dollar");
                cash.addModeOfPaymentFlavourList(dollar);
                cashManual = new ServerPaymentProcessor(organisationID, "CashManualPayment");
                cashManual.addModeOfPaymentList(cash);
                pm.makePersistent(cashManual);

                // create CreditCard with VISA and MasterCard
                ModeOfPayment creditCard = new ModeOfPayment(organisationID, "CreditCard");
                ServerPaymentProcessor clientSide = new ServerPaymentProcessor(organisationID, "ClientSidedCreditCardPayment");
                clientSide.addModeOfPaymentList(creditCard);
                pm.makePersistent(clientSide);
                ServerPaymentProcessor saferPay = new ServerPaymentProcessor(organisationID, "SaferPay");
                saferPay.addModeOfPaymentList(creditCard);
                pm.makePersistent(saferPay);
                ServerPaymentProcessor wireCard = new ServerPaymentProcessor(organisationID, "WireCard");
                wireCard.addModeOfPaymentList(creditCard);
                pm.makePersistent(wireCard);

                ModeOfPaymentFlavour visa = new ModeOfPaymentFlavour(organisationID, "Visa");
                creditCard.addModeOfPaymentFlavourList(visa);
                ServerPaymentProcessor cardVisaProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_Visa");
                cardVisaProcessor.addModeOfPaymentList(creditCard);
                pm.makePersistent(cardVisaProcessor);

                ModeOfPaymentFlavour masterCard = new ModeOfPaymentFlavour(organisationID, "MasterCard");
                creditCard.addModeOfPaymentFlavourList(masterCard);
                ServerPaymentProcessor cardMasterCardProcessor = new ServerPaymentProcessor(organisationID, "PaymentProviderXXX_MasterCard");
                cardMasterCardProcessor.addModeOfPaymentList(creditCard);
                pm.makePersistent(cardMasterCardProcessor);

                // create DebitNote
                ServerPaymentProcessor debitNoteProcessor = new ServerPaymentProcessor(organisationID, "DebitNotePayment");
                ModeOfPayment debit = new ModeOfPayment(organisationID, "Debit");
                ModeOfPaymentFlavour debitNote = new ModeOfPaymentFlavour(organisationID, "DebitNote");
                debit.addModeOfPaymentFlavourList(debitNote);
                debitNoteProcessor.addModeOfPaymentList(debit);
                pm.makePersistent(debitNoteProcessor);

                tx.commit();
                
                tx.begin();
                
                Query q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsList.contains(modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPayment"
                    );
                Collection c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  this.modeOfPaymentsList.contains(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());

                q = pm.newQuery(
                    "SELECT FROM org.jpox.samples.models.nightlabs_payments.ServerPaymentProcessor\n" +
                    "WHERE\n" +
                    "  modeOfPaymentFlavour.organisationID == paramOrganisationID &&\n" +
                    "  modeOfPaymentFlavour.modeOfPaymentFlavourID == paramModeOfPaymentFlavourID &&\n" +
                    "  modeOfPayment == modeOfPaymentFlavour.modeOfPayment &&\n" +
                    "  this.modeOfPaymentsList.contains(modeOfPaymentFlavour.modeOfPayment)\n" +
                    "VARIABLES ModeOfPaymentFlavour modeOfPaymentFlavour; ModeOfPayment modeOfPayment\n" +
                    "PARAMETERS String paramOrganisationID, String paramModeOfPaymentFlavourID\n" +
                    "import java.lang.String;\n" +
                    "import org.jpox.samples.models.nightlabs_payments.ModeOfPaymentFlavour"
                    );
                c = (Collection) q.execute(organisationID, "Dollar");
                assertEquals(1,c.size());
                assertEquals("CashManualPayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                c = (Collection) q.execute(organisationID, "Visa");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "MasterCard");
                assertEquals(5,c.size());
                c = (Collection) q.execute(organisationID, "DebitNote");
                assertEquals(1,c.size());
                assertEquals("DebitNotePayment",((ServerPaymentProcessor)c.iterator().next()).getServerPaymentProcessorID());
                
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
        finally
        {
            clean(ServerPaymentProcessor.class);
            clean(ModeOfPaymentFlavour.class);
            clean(ModeOfPayment.class);
        }
    }

    public void testQueryGroupByWithCountAndJoinSet()
    {
        try
        {
            addClassesToSchema(new Class[] { ProductType.class, Product.class, Transfer.class, ProductTransfer.class });

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            String orgaID = "test.jpox.org";
            
            // Persist some objects
            try
            {
                tx.begin();

                ProductType productType0 = (ProductType) pm.makePersistent(new ProductType(orgaID, "productType0"));
                ProductType productType1 = (ProductType) pm.makePersistent(new ProductType(orgaID, "productType1"));
                ProductType productType2 = (ProductType) pm.makePersistent(new ProductType(orgaID, "productType2"));

                /*Product product00 = (Product)*/ pm.makePersistent(new Product(orgaID, 0, productType0));
                Product product01 = (Product) pm.makePersistent(new Product(orgaID, 1, productType0));

                /*Product product10 = (Product)*/ pm.makePersistent(new Product(orgaID, 10, productType1));
                Product product11 = (Product) pm.makePersistent(new Product(orgaID, 11, productType1));
                Product product12 = (Product) pm.makePersistent(new Product(orgaID, 12, productType1));

                Product product20 = (Product) pm.makePersistent(new Product(orgaID, 20, productType2));
                Product product21 = (Product) pm.makePersistent(new Product(orgaID, 21, productType2));

                ProductTransfer productTransfer = (ProductTransfer) pm.makePersistent(new ProductTransfer(orgaID, 0));
                productTransfer.getProducts().add(product01);
                productTransfer.getProducts().add(product11);
                productTransfer.getProducts().add(product12);
                productTransfer.getProducts().add(product20);
                productTransfer.getProducts().add(product21);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown persisting objects : ", e);
                fail("Could not persist objects!");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ProductTransfer productTransfer = 
                    (ProductTransfer)pm.getExtent(ProductTransfer.class).iterator().next();

                Query q = pm.newQuery(
                        "SELECT product.productType, count(product) " +
                        "FROM " + ProductTransfer.class.getName() + ' ' +
                        "WHERE this == :productTransfer && this.products.contains(product) " +
                        "VARIABLES " + Product.class.getName() + " product " +
                        "GROUP BY product.productType "
                );

                Collection c = (Collection) q.execute(productTransfer);
                if (c.isEmpty())
                {
                    fail("No record found!");
                }
                if (c.size() != 3)
                {
                    fail("Found " + c.size() + " records, but expected 3!");
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown by query: ", e);
                fail("Exception thrown during test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                for (Iterator it = pm.getExtent(ProductTransfer.class).iterator(); it.hasNext();)
                {
                    ProductTransfer productTransfer = (ProductTransfer) it.next();
                    for (Iterator itP = productTransfer.getProducts().iterator(); itP.hasNext();)
                    {
                        itP.next();
                        itP.remove();
                    }
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown removing relations: ", e);
                fail("Exception thrown during test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();

            clean(ProductTransfer.class);
            clean(Product.class);
            clean(ProductType.class);
        }
    }
}