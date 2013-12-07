test-jdo-ldap
=============

Series of tests for the JDO API specific to LDAP datastores.
Assumes that you have a working LDAP server on your local machine at port 10389.
Also assumes that you have a DIT as
dc=example, dc=com
  ou=Users
  ou=Groups
  ou=Accounts
  ou=Addresses
  ou=Computers
  ou=Departments
  ou=Persons


Simple instructions :
* Download Apache Directory 1.5.x and install it.
* Start Apache Directory 1.5.x on your machine (on Linux "/etc/rc.d/init.d/apacheds start default")
* Install Apache Directory Studio 1.x into Eclipse
* Eclipse : Window -> Show View -> LDAP Browser -> Connections
* New Connection : localhost : 10389 and take defaults (No Authentication)
* LDAP Browser tab : DIT -> Import LDIF
* Select "example.ldif" from this directory
* Run LDAP tests as normal "mvn clean test -Pldap"

