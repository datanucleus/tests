/*
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
/*
 * @(#)Member.java	1.4 06/07/27
 */

package com.sun.ts.tests.ejb30.persistence.versioning;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Version;
import java.math.BigInteger;

/*
 * Member
 */

@Entity
@Table(name="MEMBER")
public class Member implements java.io.Serializable {

    private int	     	     memberId;
    private Integer          version;
    private String	     memberName;
    private boolean	     duesPaid;
    private BigInteger	     donation;

    public Member() {
    }

    public Member(int memberId, String memberName, boolean duesPaid)
    {
        	this.memberId = memberId;
        	this.memberName = memberName;
		this.duesPaid = duesPaid;
    }

    public Member(int memberId, String memberName, boolean duesPaid, BigInteger donation )
    {
        	this.memberId = memberId;
        	this.memberName = memberName;
		this.duesPaid = duesPaid;
		this.donation = donation;
    }

   // ===========================================================
   // getters and setters for the state fields

    @Id
    @Column(name="MEMBER_ID")
    public int getMemberId() {
        return memberId;
    }
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    @Version
    @Column(name="VERSION")
    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer i) {
        version = i;
    }

    @Column(name="MEMBER_NAME")
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Column(name="DUES")
    public boolean isDuesPaid() {
        return duesPaid;
    }
    public void setDuesPaid(boolean duesPaid) {
        this.duesPaid = duesPaid;
    }

    @Column(name="DONATION")
    public BigInteger getDonation() {
        return donation;
    }
    public void setDonation(BigInteger donation) {
        this.donation = donation;
    }

}

