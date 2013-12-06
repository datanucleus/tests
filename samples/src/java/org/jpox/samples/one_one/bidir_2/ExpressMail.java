/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributions
    ...
***********************************************************************/
package org.jpox.samples.one_one.bidir_2;

/**
 * Definition of a Express Mail
 *
 * @version $Revision: 1.1 $    
 **/
public class ExpressMail extends Mail
{
    /** 
     * insured
     **/
    protected boolean insured=true;

    /** Constructor. */
    public ExpressMail(String name)
    {
        super(name);
    }

    /**
     * Accessor for the insured
     * @return insured
     */    
    public boolean isInsured()
    {
        return insured;
    }
    
    /**
     * Accessor for the insured
     * @param insured
     */
    public void setInsured(boolean insured)
    {
        this.insured = insured;
    }
    
    public String toString()
    {
        return super.toString() + " [insured : "+ insured +"]";
    }
}
