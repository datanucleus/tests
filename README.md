tests
=====

Test suites for testing DataNucleus using JDO, JPA and REST APIs.

Build "framework", "framework.maven" and "samples" first, then use the tests for the API in question.

Raise issues in the GitHub issue tracker for this repository.


Running tests
=============

Tests are run using [framework.maven][1] which is a custom maven plugin that allows the tests to be executed for different configurations e.g. optimistic, pessimistic. It will create several Maven Surefire executions dynamically to test different configurations. Some test projects also have more than one execution configured, allowing tests to be run for different identity types as well i.e. application or datastore.

Here are few examples on how to run the tests:

- Run all the executions and default configuration
>mvn clean test

- Run all the executions with optimistic and pessimistic configuration
>mvn -Dtest.configs=optimistic,pessimistic clean test

On test.jdo.identity this will trigger 4 different executions, each configuration is run for each execution.

- Run the CollectionTest, skippking application identity execution and only optimistic configuration
>mvn -Dtest=CollectionTest -Dtest.configs=optimistic -Dtest.application-identity.skip=true clean test

- Run all the executions with optimisitc using Mysql database
>mvn -Pmysql -Dtest.configs=optimistic clean test

## Database cleanup

Before running each test set execution the databased will be cleaned up automatically. This clean up can be skipped by setting `maven.datanucleus.test.skip.reset` property to `true`.
>mvn -Pmysql -Dmaven.datanucleus.test.skip.reset=true clean test

[1]: ../../tree/master/framework.maven

