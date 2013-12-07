package org.datanucleus.store.ldap;

import javax.naming.ldap.LdapName;

import junit.framework.TestCase;

public class LDAPUtilTest extends TestCase
{

    public void testLdapName() throws Exception
    {
        LdapName dn = new LdapName("cn=a,ou=b,dc=c");

        // getRdn()
        assertEquals("cn=a", dn.getRdn(dn.size() - 1).toString());

        // getSuffix()
        assertEquals("cn=a,ou=b,dc=c", dn.getSuffix(0).toString());
        assertEquals("cn=a,ou=b", dn.getSuffix(1).toString());
        assertEquals("cn=a", dn.getSuffix(2).toString());
        assertEquals("", dn.getSuffix(3).toString());

        assertEquals("", dn.getSuffix(dn.size()).toString());

        // getPrefix
        assertEquals("", dn.getPrefix(0).toString());
        assertEquals("dc=c", dn.getPrefix(1).toString());
        assertEquals("ou=b,dc=c", dn.getPrefix(2).toString());
        assertEquals("cn=a,ou=b,dc=c", dn.getPrefix(3).toString());
    }
}
