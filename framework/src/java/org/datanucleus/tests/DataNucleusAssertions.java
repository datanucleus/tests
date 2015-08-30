/**********************************************************************
Copyright (c) 2015 Renato and others. All rights reserved.
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
package org.datanucleus.tests;

import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;

import org.assertj.core.api.Condition;
import org.datanucleus.store.types.SCO;

/**
 * Provides an extension to AssertJ for common test assertions and condtions.
 */
public class DataNucleusAssertions
{
    /**
     * It must use an instance for each assertion because we keep the actual state in order to be able to show
     * the actual state in case it does not match.
     * @param expectedState The {@link ObjectState} that the object is expected to be in
     * @return a new Condition to the given expectedState.
     */
    public static Condition<Object> state(ObjectState expectedState)
    {
        return new StateCondition(expectedState);
    }

    public static Condition<Object> WRAPPER = new Condition<Object>("a SCO wrapper")
    {
        public boolean matches(Object object)
        {
            return object instanceof SCO;
        }
    };

    private static class StateCondition extends ConditionWithActual<Object, ObjectState>
    {
        public StateCondition(ObjectState expectedState)
        {
            super(expectedState);
        }

        @Override
        public boolean matches(Object object)
        {
            actual = JDOHelper.getObjectState(object);

            boolean result = actual == expected;
            if (result)
            {
                as("state [%s]", expected);
            }
            else
            {
                as("state [%s] instead of [%s]", expected, actual);
            }
            return actual == expected;
        }
    }

    private static abstract class ConditionWithActual<T, A> extends Condition<T>
    {
        protected A actual;

        protected A expected;

        public ConditionWithActual(A expected)
        {
            this.expected = expected;
        }
    }
}
