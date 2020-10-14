/**********************************************************************
 Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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

import org.datanucleus.enhancer.DataNucleusEnhancer;

/**
 * JDO spec $21.21 has a sample code:
 * private static int jdoGetManagedFieldCount()
 * {
 *     return jdoFieldNames.length + superclass.jdoGetManagedFieldCount();
 * }
 * Due to the above code there was an initialisation error in a model
 * where the super class has an reference to the child class [ENHANCER-58]
 */
public class TestA21_21_7 extends JDOTestBase 
{
	public void testInitializationSuperClassRefersToChildClass() throws Exception
    {
        String[] files = {
                "-q",
                TestA21_21_7.class.getClassLoader().getResource("org/datanucleus/enhancer/samples/A21_21_7_A.jdo").getFile(),
                TestA21_21_7.class.getClassLoader().getResource("org/datanucleus/enhancer/samples/A21_21_7_B.jdo").getFile(),
                TestA21_21_7.class.getClassLoader().getResource("org/datanucleus/enhancer/samples/A21_21_7_C.jdo").getFile()};
        DataNucleusEnhancer.main(files);
        Class.forName("org.datanucleus.enhancer.samples.A21_21_7_B");
        //should not raise exception
	}
}