/*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)DataTypes2.java	1.5 06/03/23
 */

package com.sun.ts.tests.ejb30.persistence.types.property;

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
    
    private java.util.Date id;
    private java.sql.Time timeData;
    private java.sql.Timestamp tsData;
    private Byte[] byteData;
    private Character[] charData;
    
    public DataTypes2()
    {
    }

    public DataTypes2(java.util.Date id) {
	this.id = id;
    }

    public DataTypes2(java.util.Date id, Character[] charData,
			Byte[] byteData, java.sql.Time timeData,
			java.sql.Timestamp tsData)
    {
	this.id = id;
        this.charData = charData;
        this.byteData = byteData;
        this.timeData = timeData;
        this.tsData = tsData;
    }

   @Id
   @Column(name = "DATATYPES2_ID")
   @Temporal(TemporalType.DATE)
   public java.util.Date getId()
   {
      return id;
   }
   public void setId(java.util.Date id)
   {
      this.id= id;
   }
   
   @Column(name = "CHARDATA")
   public Character[] getCharData()
   {
      return charData;
   }
   public void setCharData(Character[] charData)
   {
      this.charData = charData;
   }
    
   @Column(name = "TIMEDATA")
   public java.sql.Time getTimeData(){
      return timeData;
   }
   public void setTimeData(java.sql.Time timeData)
   {
      this.timeData = timeData;
   }

   @Column(name = "TSDATA")
   public java.sql.Timestamp getTsData() {
       return tsData;
   }
   public void setTsData(java.sql.Timestamp tsData)
   {
      this.tsData = tsData;
   }
    
   @Column(name = "BYTEDATA")
   public Byte[] getByteData()
   {
        return byteData;
   }
   public void setByteData(Byte[] byteData)
   {
      this.byteData = byteData;
   }
    
    
}
