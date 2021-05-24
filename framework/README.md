test-framework
==============

Framework root classes for all tests, defining handling for JDO, JPA and Jakarta Persistence APIs.
This project provides a structure for all end-to-end testing with DataNucleus 
It is also usable for all datastores. All DN "test.{api}.XXX" projects should depend on it. 
It depends on DN "core", and basic test libraries like JUnit, Log4J etc.

It also provides a set of property files defining sample input for the main datastores supported by DN. 
These can then be utilised by the other "test.{api}.XXX" projects. To add to these please add files under "src/conf"
