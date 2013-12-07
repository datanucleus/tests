package org.datanucleus.samples.metadata.index;

import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;

/**
 * Class with all possible type of declarations for @Index and @Indices
 */
public class IndexSamples
{
    private static final String MEMBER3 = "member3";
    private static final String MEMBER2 = "member2";
    private static final String MEMBER1 = "member1";

    @PersistenceCapable
    public static final class IndexesTest
    {
        @Index
        @Persistent
        Object indexNonUnique;

        @Index(unique = "true")
        @Persistent
        Object indexUnique;

        @Unique
        @Persistent
        Object unique;
    }

    @Index(members = {MEMBER1, MEMBER2})
    @PersistenceCapable
    public static class IndexMembers
    {
        @Persistent
        Object member1;

        @Persistent
        Object member2;

        @Persistent
        Object member3;
    }

    @Indices({@Index(members = {MEMBER1, MEMBER2}), @Index(members = {MEMBER2, MEMBER3}, unique = "true")})
    @PersistenceCapable
    public static class IndicesMembers
    {
        @Persistent
        Object member1;

        @Persistent
        Object member2;

        @Persistent
        Object member3;
    }

    @Unique(members = {MEMBER1, MEMBER2})
    @PersistenceCapable
    public static class UniqueMembers
    {

        @Persistent
        Object member1;

        @Persistent
        Object member2;

        @Persistent
        Object member3;
    }

    @Index(members = {MEMBER1, MEMBER2})
    @Unique(members = {MEMBER3})
    @PersistenceCapable
    public static class MixedIndex
    {
        @Persistent
        Object member1;

        @Persistent
        Object member2;

        @Persistent
        Object member3;

        @Index
        @Persistent
        Object member4;
    }

    @PersistenceCapable
    public static class NoIndex
    {
        @Persistent
        Object member1;
    }
}