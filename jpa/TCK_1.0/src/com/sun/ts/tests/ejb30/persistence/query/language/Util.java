/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)Util.java	1.3 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.query.language;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;
import com.sun.ts.lib.harness.*;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.persistence.query.language.schema30.*;

import java.util.*;

public class Util
{
    private static boolean debug = false;

    public static boolean checkEJBs(Collection c, int refType, String pks[])
    {
	Customer cref = null;
	Order oref = null;
	Alias aref = null;
	Product pref = null;
	boolean foundPK = false;
	String cpks2[] = new String[c.size()];
	String cpks = "(";
	String epks = "(";
	TLogger.log("Util.checkEJBs");
	try {
	    if(pks.length == 0) epks = "()";
	    if(c.size() == 0) cpks = "()";
	    for(int i=0; i<pks.length; i++) {
	        if(i+1 != pks.length)
	            epks = epks + pks[i] + ", ";
	        else
	            epks = epks + pks[i] + ")";
	    }
	    int k = 0;
	    Iterator iterator = c.iterator();
	    while(iterator.hasNext()) {
	        if(refType == Client.CUSTOMERREF) {
	            cref = (Customer) iterator.next();
		    cpks = cpks + cref.getId();
		    cpks2[k] = cref.getId();
	        } else if(refType == Client.ORDERREF) {
	            oref = (Order)iterator.next();
		    cpks = cpks + oref.getId();
		    cpks2[k] = oref.getId();
	        } else if(refType == Client.ALIASREF) {
	            aref = (Alias)iterator.next();
		    cpks = cpks + aref.getId();
		    cpks2[k] = aref.getId();
	        } else {
	            pref = (Product)iterator.next();
		    cpks = cpks + pref.getId();
		    cpks2[k] = pref.getId();
	        }
	        if(++k != c.size())
	            cpks = cpks + ", ";
	        else
	            cpks = cpks + ")";
	    }
	    if(checkWrongSize(c, pks.length)) {
		TLogger.log("ERROR:  Wrong ejb's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    if(checkDuplicates(cpks2)) {
		TLogger.log("ERROR: Duplicate ejb's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    iterator = c.iterator();
	    while(iterator.hasNext()) {
	        if(refType == Client.CUSTOMERREF)
	            cref = (Customer) iterator.next();
	        else if(refType == Client.ORDERREF)
	            oref = (Order)iterator.next();
	        else if(refType == Client.ALIASREF)
	            aref = (Alias)iterator.next();
	        else
	            pref = (Product)iterator.next();
	        foundPK = false;
	        for(int j=0; j<pks.length; j++) {
	            if(refType == Client.CUSTOMERREF) {
		        if(cref.getId().equals(pks[j])) {
		            foundPK = true;
		            break;
		        }
	            } else if(refType == Client.ORDERREF) {
		        if(oref.getId().equals(pks[j])) {
		            foundPK = true;
		            break;
		        }
	            } else if(refType == Client.ALIASREF) {
		        if(aref.getId().equals(pks[j])) {
		            foundPK = true;
		            break;
		        }
		    } else {
		        if(pref.getId().equals(pks[j])) {
		            foundPK = true;
		            break;
		        }
		    }
	        }
	        if(!foundPK) {
		    TLogger.log("ERROR: Wrong ejb's returned, expected PKs of " 
			+ epks + ", got PKs of " + cpks);
		    return false;
	        }
	    } 
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkEJBs: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkAddressDVCs(Collection c, Address a[]) {
	TLogger.log("Util.checkAddressDVCs");
	try {
	    boolean foundPK = false;
	    String cpks = "(";
	    String epks = "(";
	    if(a.length == 0) epks = "()";
	    if(c.size() == 0) cpks = "()";
	    for(int i=0; i<a.length; i++) {
	        if(i+1 != a.length)
	            epks = epks + a[i].getId() + ", ";
	        else
	            epks = epks + a[i].getId() + ")";
	    }
	    int k=0;
    	    Iterator iterator = c.iterator();
	    String pks[] = new String[c.size()];
	    while(iterator.hasNext()) {
		Address advc = (Address)iterator.next();
		cpks = cpks + advc.getId();
		pks[k] = advc.getId();
	        if(++k != c.size())
	            cpks = cpks + ", ";
	        else
	            cpks = cpks + ")";
	    }
	    if(checkWrongSize(c, a.length)) {
		TLogger.log("ERROR:  Wrong Address's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    if(checkDuplicates(pks)) {
		TLogger.log("ERROR: Duplicate Address's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    k = 0;
	    iterator = c.iterator();
    	    while(iterator.hasNext()) {
		Address advc = (Address)iterator.next();
		foundPK = false;
		for(int j=0; j<a.length; j++) {
		    if(advc.getId().equals(a[j].getId())) {
			foundPK = true;
			break;
		    }
		}
	        if(!foundPK) {
		    TLogger.log("ERROR:  Wrong Address's returned, expected " +
			"PKs of " + epks + ", got PKs of " + cpks);
		    return false;
	        }
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkAddressDVCs: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkLineItemDVCs(Collection c, LineItem a[]) {
	TLogger.log("Util.checkLineItemDVCs");
	try {
	    boolean foundPK = false;
	    String cpks = "(";
	    String epks = "(";
	    if(a.length == 0) epks = "()";
	    if(c.size() == 0) cpks = "()";
	    for(int i=0; i<a.length; i++) {
	        if(i+1 != a.length)
	            epks = epks + a[i].getId() + ", ";
	        else
	            epks = epks + a[i].getId() + ")";
	    }
	    int k=0;
    	    Iterator iterator = c.iterator();
	    String pks[] = new String[c.size()];
	    while(iterator.hasNext()) {
		LineItem ldvc = (LineItem)iterator.next();
		cpks = cpks + ldvc.getId();
		pks[k] = ldvc.getId();
	        if(++k != c.size())
	            cpks = cpks + ", ";
	        else
	            cpks = cpks + ")";
	    }
	    if(checkWrongSize(c, a.length)) {
		TLogger.log("ERROR: Wrong LineItem's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    if(checkDuplicates(pks)) {
		TLogger.log("ERROR: Duplicate LineItem's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    k = 0;
	    iterator = c.iterator();
    	    while(iterator.hasNext()) {
		LineItem ldvc = (LineItem)iterator.next();
		foundPK = false;
		for(int j=0; j<a.length; j++) {
		    if(ldvc.getId().equals(a[j].getId())) {
			foundPK = true;
			break;
		    }
		}
	        if(!foundPK) {
		    TLogger.log("ERROR:  Wrong LineItem's returned, expected " +
			"PKs of " + epks + ", got PKs of " + cpks);
		    return false;
	        }
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkLineItemDVCs: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkCreditCardDVCs(
					Collection c, CreditCard a[]) {
	TLogger.log("Util.checkCreditCardDVCs");
	try {
	    boolean foundPK = false;
	    String cpks = "(";
	    String epks = "(";
	    if(a.length == 0) epks = "()";
	    if(c.size() == 0) cpks = "()";
	    for(int i=0; i<a.length; i++) {
	        if(i+1 != a.length)
	            epks = epks + a[i].getId() + ", ";
	        else
	            epks = epks + a[i].getId() + ")";
	    }
	    int k=0;
    	    Iterator iterator = c.iterator();
	    String pks[] = new String[c.size()];
	    while(iterator.hasNext()) {
		CreditCard advc = (CreditCard)iterator.next();
		cpks = cpks + advc.getId();
		pks[k] = advc.getId();
	        if(++k != c.size())
	            cpks = cpks + ", ";
	        else
	            cpks = cpks + ")";
	    }
	    if(checkWrongSize(c, a.length)) {
		TLogger.log("ERROR: Wrong CreditCard's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    if(checkDuplicates(pks)) {
		TLogger.log("ERROR: Duplicate CreditCard's returned, expected " 
		    + "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    k = 0;
	    iterator = c.iterator();
    	    while(iterator.hasNext()) {
		CreditCard advc = (CreditCard)iterator.next();
		foundPK = false;
		for(int j=0; j<a.length; j++) {
		    if(advc.getId().equals(a[j].getId())) {
			foundPK = true;
			break;
		    }
		}
	        if(!foundPK) {
		    TLogger.log("ERROR: Wrong CreditCard's returned, " +
			"expected PKs of " + epks + ", got PKs of " + cpks);
		    return false;
	        }
	    }
	} catch (Exception e) {
	  TLogger.log("Exception in Util.checkCreditCardDVCs: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkPhoneDVCs(Collection c, Phone a[]) {
	TLogger.log("Util.checkPhoneDVCs");
	try {
	    boolean foundPK = false;
	    String cpks = "(";
	    String epks = "(";
	    if(a.length == 0) epks = "()";
	    if(c.size() == 0) cpks = "()";
	    for(int i=0; i<a.length; i++) {
	        if(i+1 != a.length)
	            epks = epks + a[i].getId() + ", ";
	        else
	            epks = epks + a[i].getId() + ")";
	    }
	    int k=0;
    	    Iterator iterator = c.iterator();
	    String pks[] = new String[c.size()];
	    while(iterator.hasNext()) {
		Phone advc = (Phone)iterator.next();
		cpks = cpks + advc.getId();
		pks[k] = advc.getId();
	        if(++k != c.size())
	            cpks = cpks + ", ";
	        else
	            cpks = cpks + ")";
	    }
	    if(checkWrongSize(c, a.length)) {
		TLogger.log("ERROR: Wrong Phone's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    if(checkDuplicates(pks)) {
		TLogger.log("ERROR: Duplicate Phone's returned, expected " +
		    "PKs of " + epks + ", got PKs of " + cpks);
		return false;
	    }
	    k = 0;
	    iterator = c.iterator();
    	    while(iterator.hasNext()) {
		Phone advc = (Phone)iterator.next();
		foundPK = false;
		for(int j=0; j<a.length; j++) {
		    if(advc.getId().equals(a[j].getId())) {
			foundPK = true;
			break;
		    }
		}
	        if(!foundPK) {
		    TLogger.log("ERROR: Wrong Phone's returned, expected " +
			"PKs of " + epks + ", got PKs of " + cpks);
		        return false;
	        }
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkPhoneDVCs: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkEJB(Customer a1, String a2) {
	TLogger.log("Util.checkEJB");
	try {
	    if(!a1.getId().equals(a2)) {
		TLogger.log("ERROR: Wrong ejb returned, expected " +
		    "PK of " + a2 + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkEJB: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkEJB(Order a1, String a2) {
	TLogger.log("Util.checkEJB");
	try {
	    if(!a1.getId().equals(a2)) {
		TLogger.log("ERROR:  Wrong ejb returned, expected " +
		    "PK of " + a2 + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("ERROR: Exception in Util.checkEJB: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkEJB(Alias a1, String a2) {
	TLogger.log("Util.checkEJB");
	try {
	    if(!a1.getId().equals(a2)) {
		TLogger.log("ERROR:  Wrong ejb returned, expected " +
		    "PK of " + a2 + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkEJB: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkEJB(Product a1, String a2) {
	TLogger.log("Util.checkEJB");
	try {
	    if(!a1.getId().equals(a2)) {
		TLogger.log("ERROR: Wrong ejb returned, expected " +
		    "PK of " + a2 + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkEJB: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkSingleResult(Object a1, Object a2) {
	TLogger.log("Util.checkSingleResult");
	try {
	    if(! (a1 == a2)) {
		TLogger.log("ERROR: Objects are not identical");
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkSingleResult: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkAddressDVC(Address a1, Address a2) {
	TLogger.log("Util.checkAddressDVC");
	try {
	    if(!a1.getId().equals(a2.getId())) {
		TLogger.log("ERROR: Wrong Address returned, expected " +
		    "PK of " + a2.getId() + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkAddressDVC: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkLineItemDVC(LineItem a1, LineItem a2) {
	TLogger.log("Util.checkLineItemDVC");
	try {
	    if(!a1.getId().equals(a2.getId())) {
		TLogger.log("ERROR: Wrong LineItem returned, expected " +
		    "PK of " + a2.getId() + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkLineItemDVC: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkCreditCardDVC(
				CreditCard a1, CreditCard a2) {
	TLogger.log("Util.checkCreditCardDVC");
	try {
	    if(!a1.getId().equals(a2.getId())) {
		TLogger.log("ERROR: Wrong CreditCard returned, expected " +
		    "PK of " + a2.getId() + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkCreditCardDVC: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static boolean checkPhoneDVC(Phone a1, Phone a2) {
	TLogger.log("Util.checkPhoneDVC");
	try {
	    if(!a1.getId().equals(a2.getId())) {
		TLogger.log("ERROR: Wrong Phone returned, expected " +
		    "PK of " + a2.getId() + ", got PK of " + a1.getId());
		return false;
	    }
	} catch (Exception e) {
	    TLogger.log("Exception in Util.checkPhoneDVC: " + e);
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    private static boolean checkWrongSize(Collection c, int s) 
    {
	TLogger.log("Util.checkWrongSize");
	if(c.size() != s ) {
	    TLogger.log("ERROR: Wrong collection size returned (expected " +
		s + ", got " + c.size() + ")");
	    return true;
	}
	return false;
    }

    private static boolean checkDuplicates(String s[])
    {
	TLogger.log("Util.checkDuplicates");
	boolean duplicates = false;
	for(int i=0; i<s.length; i++) {
	    for(int j=0; j<s.length; j++) {
		if(i == j) continue;
		if(s[i].equals(s[j])) {
		    duplicates = true;
		    break;
		}
	    }
	}
	if(duplicates) {
	    TLogger.log("ERROR: Wrong collection contents returned " +
		"(contains duplicate entries)");
	    return true;
	}
	return false;
    }
}
