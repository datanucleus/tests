/**********************************************************************
Copyright (c) 2007 Guido Anzuoni and others. All rights reserved.
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
package org.datanucleus.tests.jta.util;

import javax.jdo.PersistenceManager;
import javax.transaction.Synchronization;
    
public class PersistenceManagerDisposer implements Synchronization 
{
    protected PersistenceManager persistenceManager;

    public PersistenceManagerDisposer(PersistenceManager pm)
    {
        persistenceManager = pm;
        pm.currentTransaction().setSynchronization(this);
    }

    public PersistenceManager getPersistenceManager()
    {
        return persistenceManager;
    }

    public void afterCompletion(int s)
    {
        closePersistenceManager();
    }

    private void closePersistenceManager()
    {
        persistenceManager.close();
    }

    public void beforeCompletion()
    {
    }
}