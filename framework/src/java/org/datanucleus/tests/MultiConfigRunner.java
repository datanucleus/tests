package org.datanucleus.tests;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Runner that allows to distinguish tests with error from different
 * configurations easily by showing the configuration name in the method
 * description.
 */
public class MultiConfigRunner extends Runner
{
    private Runner delegateRunner;

    public MultiConfigRunner(Class<?> test) throws Exception
    {
        this.delegateRunner = runnerForClass(test);
    }

    @Override
    public Description getDescription()
    {
        return delegateRunner.getDescription();
    }

    @Override
    public void run(RunNotifier notifier)
    {
        delegateRunner.run(notifier);
    }

    private Runner runnerForClass(Class<?> testClass) throws Exception
    {
        String config = System.getProperty("datanucleus.test.config", "default");

        Runner runner;

        if (TestCase.class.isAssignableFrom(testClass))
        {
            runner = new JUnit38ConfigRunner(testClass, config);
        }
        else
        {
            runner = new JUnit4ConfigRunner(testClass, config);
        }

        return runner;
    }

    class JUnit38ConfigRunner extends JUnit38ClassRunner
    {
        final private String config;

        public JUnit38ConfigRunner(Class<?> test, String config)
        {
            super(test);
            this.config = config;
        }

        @Override
        public TestListener createAdaptingListener(RunNotifier notifier)
        {
            return new OldTestClassAdaptingListener(notifier);
        }

        private final class OldTestClassAdaptingListener implements TestListener
        {
            private final RunNotifier fNotifier;

            private OldTestClassAdaptingListener(RunNotifier notifier)
            {
                fNotifier = notifier;
            }

            public void endTest(Test test)
            {
                fNotifier.fireTestFinished(asDescription(test));
            }

            public void startTest(Test test)
            {
                fNotifier.fireTestStarted(asDescription(test));
            }

            // Implement junit.framework.TestListener
            public void addError(Test test, Throwable t)
            {
                Failure failure = new Failure(asDescription(test), t);
                fNotifier.fireTestFailure(failure);
            }

            private Description asDescription(Test test)
            {
                return Description.createTestDescription(test.getClass(), getName(test) + "[" + config + "]");
            }

            private String getName(Test test)
            {
                if (test instanceof TestCase)
                {
                    return ((TestCase) test).getName();
                }
                else
                {
                    return test.toString();
                }
            }

            public void addFailure(Test test, AssertionFailedError t)
            {
                addError(test, t);
            }
        }

    }

    class JUnit4ConfigRunner extends BlockJUnit4ClassRunner
    {
        final private String config;

        public JUnit4ConfigRunner(Class<?> klass, String config) throws InitializationError
        {
            super(klass);
            this.config = config;
        }

        @Override
        protected Description describeChild(FrameworkMethod method)
        {
            return Description.createTestDescription(getTestClass().getJavaClass(),
                testName(method) + "[" + config + "]", method.getAnnotations());
        }
    }
}
