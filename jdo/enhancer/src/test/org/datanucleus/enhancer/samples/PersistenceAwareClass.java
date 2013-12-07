package org.datanucleus.enhancer.samples;

import java.math.*;
import java.util.*;

/**
 * @version $Revision: 1.1 $
 */
public class PersistenceAwareClass
{
    public boolean check;
    public PersistenceAwareClass(boolean value)
    {
        check = value;
    }

    private PersistenceAwareClass(FullPublicClass fpc)
    {
        //default constructor
	}
	public static PersistenceAwareClass getInstance()
	{
	    return new PersistenceAwareClass(null);
	}
    public static boolean isField00(FullPublicClass fpc)
    {
        return fpc.field00;
    }
    public static void setField00(FullPublicClass fpc,boolean field00)
    {
        fpc.field00 = field00;
    }
    public static byte getField01(FullPublicClass fpc)
    {
        return fpc.field01;
    }
    public static void setField01(FullPublicClass fpc,byte field01)
    {
        fpc.field01 = field01;
    }
    public static short getField02(FullPublicClass fpc)
    {
        return fpc.field02;
    }
    public static void setField02(FullPublicClass fpc,short field02)
    {
        fpc.field02 = field02;
    }
    public static char getField03(FullPublicClass fpc)
    {
        return fpc.field03;
    }
    public static void setField03(FullPublicClass fpc,char field03)
    {
        fpc.field03 = field03;
    }
    public static int getField04(FullPublicClass fpc)
    {
        return fpc.field04;
    }
    public static void setField04(FullPublicClass fpc,int field04)
    {
        fpc.field04 = field04;
    }
    public static float getField05(FullPublicClass fpc)
    {
        return fpc.field05;
    }
    public static void setField05(FullPublicClass fpc,float field05)
    {
        fpc.field05 = field05;
    }
    public static long getField06(FullPublicClass fpc)
    {
        return fpc.field06;
    }
    public static void setField06(FullPublicClass fpc,long field06)
    {
        fpc.field06 = field06;
    }
    public static double getField07(FullPublicClass fpc)
    {
        return fpc.field07;
    }
    public static void setField07(FullPublicClass fpc,double field07)
    {
        fpc.field07 = field07;
    }
    public static Boolean getField08(FullPublicClass fpc)
    {
        return fpc.field08;
    }
    public static void setField08(FullPublicClass fpc,Boolean field08)
    {
        fpc.field08 = field08;
    }
    public static Byte getField09(FullPublicClass fpc)
    {
        return fpc.field09;
    }
    public static void setField09(FullPublicClass fpc,Byte field09)
    {
        fpc.field09 = field09;
    }
    public static Short getField10(FullPublicClass fpc)
    {
        return fpc.field10;
    }
    public static void setField10(FullPublicClass fpc,Short field10)
    {
        fpc.field10 = field10;
    }
    public static Character getField11(FullPublicClass fpc)
    {
        return fpc.field11;
    }
    public static void setField11(FullPublicClass fpc,Character field11)
    {
        fpc.field11 = field11;
    }
    public static Integer getField12(FullPublicClass fpc)
    {
        return fpc.field12;
    }
    public static void setField12(FullPublicClass fpc,Integer field12)
    {
        fpc.field12 = field12;
    }
    public static Float getField13(FullPublicClass fpc)
    {
        return fpc.field13;
    }
    public static void setField13(FullPublicClass fpc,Float field13)
    {
        fpc.field13 = field13;
    }
    public static Long getField14(FullPublicClass fpc)
    {
        return fpc.field14;
    }
    public static void setField14(FullPublicClass fpc,Long field14)
    {
        fpc.field14 = field14;
    }
    public static Double getField15(FullPublicClass fpc)
    {
        return fpc.field15;
    }
    public static void setField15(FullPublicClass fpc,Double field15)
    {
        fpc.field15 = field15;
    }
    public static String getField16(FullPublicClass fpc)
    {
        return fpc.field16;
    }
    public static void setField16(FullPublicClass fpc,String field16)
    {
        fpc.field16 = field16;
    }
    public static Number getField17(FullPublicClass fpc)
    {
        return fpc.field17;
    }
    public static void setField17(FullPublicClass fpc,Number field17)
    {
        fpc.field17 = field17;
    }
    public static BigDecimal getField18(FullPublicClass fpc)
    {
        return fpc.field18;
    }
    public static void setField18(FullPublicClass fpc,BigDecimal field18)
    {
        fpc.field18 = field18;
    }
    public static BigInteger getField19(FullPublicClass fpc)
    {
        return fpc.field19;
    }
    public static void setField19(FullPublicClass fpc,BigInteger field19)
    {
        fpc.field19 = field19;
    }
    public static Date getField20(FullPublicClass fpc)
    {
        return fpc.field20;
    }
    public static void setField20(FullPublicClass fpc,Date field20)
    {
        fpc.field20 = field20;
    }
    public static Locale getField21(FullPublicClass fpc)
    {
        return fpc.field21;
    }
    public static void setField21(FullPublicClass fpc,Locale field21)
    {
        fpc.field21 = field21;
    }
    public static ArrayList getField22(FullPublicClass fpc)
    {
        return fpc.field22;
    }
    public static void setField22(FullPublicClass fpc,ArrayList field22)
    {
        fpc.field22 = field22;
    }
    public static HashMap getField23(FullPublicClass fpc)
    {
        return fpc.field23;
    }
    public static void setField23(FullPublicClass fpc,HashMap field23)
    {
        fpc.field23 = field23;
    }
    public static HashSet getField24(FullPublicClass fpc)
    {
        return fpc.field24;
    }
    public static void setField24(FullPublicClass fpc,HashSet field24)
    {
        fpc.field24 = field24;
    }
    public static Hashtable getField25(FullPublicClass fpc)
    {
        return fpc.field25;
    }
    public static void setField25(FullPublicClass fpc,Hashtable field25)
    {
        fpc.field25 = field25;
    }
    public static LinkedList getField26(FullPublicClass fpc)
    {
        return fpc.field26;
    }
    public static void setField26(FullPublicClass fpc,LinkedList field26)
    {
        fpc.field26 = field26;
    }
    public static TreeMap getField27(FullPublicClass fpc)
    {
        return fpc.field27;
    }
    public static void setField27(FullPublicClass fpc,TreeMap field27)
    {
        fpc.field27 = field27;
    }
    public static TreeSet getField28(FullPublicClass fpc)
    {
        return fpc.field28;
    }
    public static void setField28(FullPublicClass fpc,TreeSet field28)
    {
        fpc.field28 = field28;
    }
    public static Vector getField29(FullPublicClass fpc)
    {
        return fpc.field29;
    }
    public static void setField29(FullPublicClass fpc,Vector field29)
    {
        fpc.field29 = field29;
    }
    public static Collection getField30(FullPublicClass fpc)
    {
        return fpc.field30;
    }
    public static void setField30(FullPublicClass fpc,Collection field30)
    {
        fpc.field30 = field30;
    }
    public static Set getField31(FullPublicClass fpc)
    {
        return fpc.field31;
    }
    public static void setField31(FullPublicClass fpc,Set field31)
    {
        fpc.field31 = field31;
    }
    public static List getField32(FullPublicClass fpc)
    {
        return fpc.field32;
    }
    public static void setField32(FullPublicClass fpc,List field32)
    {
        fpc.field32 = field32;
    }
    public static Map getField33(FullPublicClass fpc)
    {
        return fpc.field33;
    }
    public static void setField33(FullPublicClass fpc,Map field33)
    {
        fpc.field33 = field33;
    }
    public static FullPublicClass getField34(FullPublicClass fpc)
    {
        return fpc.field34;
    }
    public static void setField34(FullPublicClass fpc,FullPublicClass field34)
    {
        fpc.field34 = field34;
    }
    public static boolean[] getField35(FullPublicClass fpc)
    {
        return fpc.field35;
    }
    public static void setField35(FullPublicClass fpc,boolean[] field35)
    {
        fpc.field35 = field35;
    }
    public static byte[] getField36(FullPublicClass fpc)
    {
        return fpc.field36;
    }
    public static void setField36(FullPublicClass fpc,byte[] field36)
    {
        fpc.field36 = field36;
    }
    public static short[] getField37(FullPublicClass fpc)
    {
        return fpc.field37;
    }
    public static void setField37(FullPublicClass fpc,short[] field37)
    {
        fpc.field37 = field37;
    }
    public static char[] getField38(FullPublicClass fpc)
    {
        return fpc.field38;
    }
    public static void setField38(FullPublicClass fpc,char[] field38)
    {
        fpc.field38 = field38;
    }
    public static int[] getField39(FullPublicClass fpc)
    {
        return fpc.field39;
    }
    public static void setField39(FullPublicClass fpc,int[] field39)
    {
        fpc.field39 = field39;
    }
    public static float[] getField40(FullPublicClass fpc)
    {
        return fpc.field40;
    }
    public static void setField40(FullPublicClass fpc,float[] field40)
    {
        fpc.field40 = field40;
    }
    public static long[] getField41(FullPublicClass fpc)
    {
        return fpc.field41;
    }
    public static void setField41(FullPublicClass fpc,long[] field41)
    {
        fpc.field41 = field41;
    }
    public static double[] getField42(FullPublicClass fpc)
    {
        return fpc.field42;
    }
    public static void setField42(FullPublicClass fpc,double[] field42)
    {
        fpc.field42 = field42;
    }
    public static Boolean[] getField43(FullPublicClass fpc)
    {
        return fpc.field43;
    }
    public static void setField43(FullPublicClass fpc,Boolean[] field43)
    {
        fpc.field43 = field43;
    }
    public static Byte[] getField44(FullPublicClass fpc)
    {
        return fpc.field44;
    }
    public static void setField44(FullPublicClass fpc,Byte[] field44)
    {
        fpc.field44 = field44;
    }
    public static Short[] getField45(FullPublicClass fpc)
    {
        return fpc.field45;
    }
    public static void setField45(FullPublicClass fpc,Short[] field45)
    {
        fpc.field45 = field45;
    }
    public static Character[] getField46(FullPublicClass fpc)
    {
        return fpc.field46;
    }
    public static void setField46(FullPublicClass fpc,Character[] field46)
    {
        fpc.field46 = field46;
    }
    public static Integer[] getField47(FullPublicClass fpc)
    {
        return fpc.field47;
    }
    public static void setField47(FullPublicClass fpc,Integer[] field47)
    {
        fpc.field47 = field47;
    }
    public static Float[] getField48(FullPublicClass fpc)
    {
        return fpc.field48;
    }
    public static void setField48(FullPublicClass fpc,Float[] field48)
    {
        fpc.field48 = field48;
    }
    public static Long[] getField49(FullPublicClass fpc)
    {
        return fpc.field49;
    }
    public static void setField49(FullPublicClass fpc,Long[] field49)
    {
        fpc.field49 = field49;
    }
    public static Double[] getField50(FullPublicClass fpc)
    {
        return fpc.field50;
    }
    public static void setField50(FullPublicClass fpc,Double[] field50)
    {
        fpc.field50 = field50;
    }
    public static String[] getField51(FullPublicClass fpc)
    {
        return fpc.field51;
    }
    public static void setField51(FullPublicClass fpc,String[] field51)
    {
        fpc.field51 = field51;
    }
    public static Number[] getField52(FullPublicClass fpc)
    {
        return fpc.field52;
    }
    public static void setField52(FullPublicClass fpc,Number[] field52)
    {
        fpc.field52 = field52;
    }
    public static Date[] getField53(FullPublicClass fpc)
    {
        return fpc.field53;
    }
    public static void setField53(FullPublicClass fpc,Date[] field53)
    {
        fpc.field53 = field53;
    }
    public static Locale[] getField54(FullPublicClass fpc)
    {
        return fpc.field54;
    }
    public static void setField54(FullPublicClass fpc,Locale[] field54)
    {
        fpc.field54 = field54;
    }
    public static java.io.File getN01(FullPublicClass fpc)
    {
        return fpc.n01;
    }
    public static void setN01(FullPublicClass fpc,java.io.File n01)
    {
        fpc.n01 = n01;
    }
    public static Void getN02(FullPublicClass fpc)
    {
        return fpc.n02;
    }
    public static void setN02(FullPublicClass fpc,Void n02)
    {
        fpc.n02 = n02;
    }
    public static UserDefinedClass getN03(FullPublicClass fpc)
    {
        return fpc.n03;
    }
    public static void setN03(FullPublicClass fpc,UserDefinedClass n03)
    {
        fpc.n03 = n03;
    }
}
