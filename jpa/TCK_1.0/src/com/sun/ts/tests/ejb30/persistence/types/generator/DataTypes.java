/*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)DataTypes.java	1.4 06/02/11
 */

package com.sun.ts.tests.ejb30.persistence.types.generator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name = "DATATYPES")
public class DataTypes implements java.io.Serializable {
    
    private int id;
    private Character characterData;
    private Short shortData;
    private Integer integerData;       
    private Long longData;
    private Double doubleData;
    private Float floatData;
    
    public DataTypes()
    {
    }

    public DataTypes(Character characterData, Short shortData, Integer integerData,
			Long longData, Double doubleData, Float floatData)
    {
        this.characterData = characterData;
        this.shortData = shortData;
        this.integerData = integerData;
        this.longData = longData;
        this.doubleData = doubleData;
        this.floatData = floatData;
    }

   @Id
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "myTableGenerator")
   @TableGenerator(name = "myTableGenerator", table="GENERATOR_TABLE",
            pkColumnName = "PK_COL", valueColumnName = "VAL_COL",
	    pkColumnValue = "DT_ID", allocationSize = 1)
   @Column(name="ID")
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id= id;
   }
   
   @Column(name = "CHARDATA")
   public Character getCharacterData()
   {
      return characterData;
   }
   public void setCharacterData(Character characterData)
   {
      this.characterData = characterData;
   }
    
   @Column(name = "SHORTDATA")
   public Short getShortData(){
      return shortData;
   }
   public void setShortData(Short shortData)
   {
      this.shortData = shortData;
   }

   @Column(name = "INTDATA")
   public Integer getIntegerData() {
       return integerData;
   }
   public void setIntegerData(Integer integerData)
   {
      this.integerData = integerData;
   }
    
   @Column(name = "LONGDATA")
   public Long getLongData()
   {
        return longData;
   }
   public void setLongData(Long longData)
   {
      this.longData = longData;
   }
    
   @Column(name = "DBLDATA")
   public Double getDoubleData()
   {
        return doubleData;
   }
   public void setDoubleData(Double doubleData)
   {
      this.doubleData = doubleData;
   }

   @Column(name = "FLOATDATA")
   public Float getFloatData()
   {
        return floatData;
   }
   public void setFloatData(Float floatData)
   {
      this.floatData = floatData;
   }
    
    
}
