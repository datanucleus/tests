/**********************************************************************
Copyright (c) Aug 4, 2004 Erik Bengtson and others.
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
package org.datanucleus.samples.metadata.correctclasses;

/**
 * @author Erik Bengtson
 * @version $Revision: 1.1 $
 */
public class TestClassInnerClassStatic
{
    private int fieldA;

    /**
     * 
     */
    public TestClassInnerClassStatic()
    {
        super();
    }
    
    /**
     * @return Returns the fieldA.
     */
    public int getFieldA()
    {
        return fieldA;
    }
    /**
     * @param fieldA The fieldA to set.
     */
    public void setFieldA(int fieldA)
    {
        this.fieldA = fieldA;
    }
    /**
     * @author Erik Bengtson
     * @version $Revision: 1.1 $
     */
    public static class TheInnerClass
    {
        private int fieldB;
        /**
         * 
         */
        public TheInnerClass()
        {
            super();
        }
        
        /**
         * @return Returns the fieldB.
         */
        public int getFieldB()
        {
            return fieldB;
        }
        /**
         * @param fieldB The fieldB to set.
         */
        public void setFieldB(int fieldB)
        {
            this.fieldB = fieldB;
        }
    }
}
