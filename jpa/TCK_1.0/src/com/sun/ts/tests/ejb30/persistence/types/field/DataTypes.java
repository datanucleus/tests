/*
  * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
  * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
  */

/*
 * @(#)DataTypes.java	1.9 06/06/06
 */

package com.sun.ts.tests.ejb30.persistence.types.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import com.sun.ts.tests.ejb30.persistence.types.common.Grade;

@Entity
@Table(name = "DATATYPES")
public class DataTypes implements java.io.Serializable{

    @Id
    protected int id;
    
    @Column(name = "BOOLEANDATA")
    protected boolean booleanData;

    @Column(name = "BYTEDATA")
    protected byte byteData;

    @Column(name = "CHARDATA")
    protected char characterData;

    @Column(name = "SHORTDATA")
    protected short shortData;

    @Column(name = "INTDATA")
    protected int integerData;       

    @Column(name = "LONGDATA")
    protected long longData;
    
    @Column(name = "DBLDATA")
    protected double doubleData;

    @Column(name = "FLOATDATA")
    protected float floatData;

    @Column(name = "ENUMSDATA")
    @Enumerated(EnumType.STRING)
    protected Grade enumData;


    public DataTypes()
    {
    }
    
    public DataTypes(int id)
    {
	this.id = id;
    }

    public DataTypes(int id, boolean booleanData, byte byteData, char characterData,
			 short shortData, int integerData, long longData,
				 double doubleData, float floatData) {
	this.id = id;
    	this.booleanData = booleanData;
        this.byteData = byteData;
        this.characterData = characterData;
        this.shortData = shortData;
        this.integerData = integerData;
        this.longData = longData;
        this.doubleData = doubleData;
        this.floatData = floatData;
    }

    public Integer fetchIdData(){
       return new Integer(id);
    }

    public boolean fetchBooleanData(){
       return booleanData;
    }

    public void storeBooleanData(boolean booleanData)
    {
      this.booleanData = booleanData;
    }
    
    public byte fetchByteData()
    {
       return byteData;
    }
    public void storeByteData(byte byteData)
    {
       this.byteData= byteData;
    }
    
    public char fetchCharacterData()
    {
        return characterData;
    }
    public void storeCharacterData(char characterData)
    {
      this.characterData = characterData;
    }
    
    public short fetchShortData(){
        return shortData;
    }
    public void storeShortData(short shortData)
    {
      this.shortData = shortData;
    }

    public int fetchIntegerData(){
        return integerData;
    }

    public void storeIntegerData(int integerData)
    {
      this.integerData = integerData;
    }
    
    public long fetchLongData() {
        return longData;
    }
    public void storeLongData(long longData)
    {
      this.longData = longData;
    }

    public double fetchDoubleData() {
        return doubleData;
    }

    public void storeDoubleData(double doubleData)
    {
      this.doubleData = doubleData;
    }

    public float fetchFloatData() {
        return floatData;
    }
    public void storeFloatData(float floatData)
    {
      this.floatData = floatData;
    }

    public Grade fetchEnumData() {
        return enumData;
    }
    public void storeEnumData(Grade enumData)
    {
      this.enumData = enumData;
    }
}       
