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

import org.datanucleus.enhancer.samples.UserStaticBlock;

/**
 * JDO spec 21.16 says: "The generated static initialization code is placed
 * after any user-defined static initialization code"
 * @version $Revision: 1.2 $
 */
public class TestA21_16 extends JDOTestBase 
{
	public void testUserStaticBlockCodeInitializedBeforeJDOStaticBlock()
    {
        //in case of error, the constructor will raise exception
        new UserStaticBlock();
	}

}
