/**********************************************************************
Copyright (c) Nov 18, 2004 erik and others.
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
    ...
**********************************************************************/
package org.datanucleus.enhancer.jdo;

import org.datanucleus.enhancer.samples.FieldAccess;

/**
 * For any field access that reads the value of a field, the getfield byte code
 * is replaced with a call to a generated local method, jdoGetXXX.
 */
public class TestA20_14_1 extends JDOTestBase
{
    public void testFieldAccess()
    {
        FieldAccess.DataHolder dh = new FieldAccess.DataHolder();
        dh.temp = "test1";
        FieldAccess fa = new FieldAccess();
        fa.id = 3;
        assertEquals(dh.temp,fa.getFieldValueNav1(dh));
        assertEquals(dh.temp,fa.getFieldValueNav2(dh));
        assertEquals(dh.temp,fa.getFieldValueNav3(dh));
        assertEquals(3,FieldAccess.getFieldValueNav4(new FieldAccess.Id("3")));
        assertEquals(4,FieldAccess.getFieldValueNav5(new FieldAccess.Id("4")));
        assertEquals(5,FieldAccess.getFieldValueNav6(new FieldAccess.Id("5")));
        assertEquals(6,FieldAccess.getFieldValueNav7(fa,new FieldAccess.Id("6")));
        assertEquals(7,fa.getFieldValueNav8(new FieldAccess.Id("7")));
        assertEquals(8,fa.getFieldValueNav9(new FieldAccess.Id("8")));
        assertEquals(9,fa.getFieldValueNav10(new FieldAccess.Id("9")));
        assertEquals(10,fa.getFieldValueNav11(new FieldAccess.Id("10")));
        assertEquals(11,fa.getFieldValueNav12(new FieldAccess.Id("11")));
        assertEquals(12,fa.getFieldValueNav13(new FieldAccess.Id("12")));
        assertEquals(13,fa.getFieldValueNav14(new FieldAccess.Id("13")));
        assertEquals(14,fa.getFieldValueNav15(new FieldAccess.Id("14")));
        assertEquals(15,fa.getFieldValueNav16(new FieldAccess.Id("15")));
        fa.id = 4;
        assertEquals(5,fa.getFieldValueNav17());
        assertEquals(6,fa.getFieldValueNav17());
        assertEquals(7,fa.getFieldValueNav18());
        assertEquals(8,fa.getFieldValueNav18());
    }
}