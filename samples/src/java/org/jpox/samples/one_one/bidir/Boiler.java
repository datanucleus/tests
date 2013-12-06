/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.one_one.bidir;

/**
 * Representation of a Boiler in a heating system
 * @version $Revision: 1.1 $
 */
public class Boiler extends Equipment
{
    private String model;
    private Timer timer;
    private Timer timer2;

    public Boiler(String make, String model)
    {
        super(make);
        this.model = model;
    }

    public void setTimer(Timer timer)
    {
        this.timer = timer;
    }

    public Timer getTimer()
    {
        return timer;
    }

    public void setTimer2(Timer timer)
    {
        this.timer2 = timer;
    }
    

    public Timer getTimer2()
    {
        return timer2;
    }
    
    
    /**
     * @return Returns the model.
     */
    public String getModel()
    {
        return model;
    }

    /**
     * @param model The model to set.
     */
    public void setModel(String model)
    {
        this.model = model;
    }
}