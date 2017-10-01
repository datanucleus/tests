/*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)DataTypes2.java	1.5 06/02/14
 */

package com.sun.ts.tests.ejb30.persistence.types.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "DATATYPES2")
public class DataTypes2 implements java.io.Serializable {

   @Id
   @Column(name = "DATATYPES2_ID")
   @Temporal(TemporalType.DATE)
   protected java.util.Date id;

   @Column(name = "TIMEDATA")
   protected java.sql.Time timeData;

   @Column(name = "TSDATA")
   protected java.sql.Timestamp tsData;

   @Column(name = "BYTEDATA")
   protected byte[] byteData;

   @Column(name = "CHARDATA")
   protected char[] charData;
    
   public DataTypes2()
   {
   }

   public DataTypes2(java.util.Date id) {
	this.id = id;
   }

   public DataTypes2(java.util.Date id, char[] charData,
			byte[] byteData, java.sql.Time timeData,
			java.sql.Timestamp tsData)
   {
	this.id = id;
        this.charData = charData;
        this.byteData = byteData;
        this.timeData = timeData;
        this.tsData = tsData;
    }

   public java.util.Date fetchId()
   {
	return id;
   }

   public char[] fetchCharData()
   {
      return charData;
   }
   public void storeCharData(char[] charData)
   {
      this.charData = charData;
   }
    
   public java.sql.Time fetchTimeData(){
      return timeData;
   }
   public void storeTimeData(java.sql.Time timeData)
   {
      this.timeData = timeData;
   }

   public java.sql.Timestamp fetchTsData() {
       return tsData;
   }
   public void storeTsData(java.sql.Timestamp tsData)
   {
      this.tsData = tsData;
   }
    
   public byte[] fetchByteData()
   {
        return byteData;
   }
   public void storeByteData(byte[] byteData)
   {
      this.byteData = byteData;
   }
    
    
}
