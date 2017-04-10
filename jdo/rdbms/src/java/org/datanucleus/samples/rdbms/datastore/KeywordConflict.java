/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.rdbms.datastore;

import java.util.Random;

/**
 * A test object using Java identifiers intentionally chosen to conflict with reserved SQL keywords.
 */
public class KeywordConflict
{
    private int column;
    private int select;
    private int where;
    private int varchar;
    private int decimal;
    private int _leading;
    private int trailing_;
    private int _surrounding_;

    public KeywordConflict()
    {
        super();
    }

    public int getColumn()
    {
        return column;
    }

    public int getSelect()
    {
        return select;
    }

    public int getWhere()
    {
        return where;
    }

    public int getVarchar()
    {
        return varchar;
    }

    public int getDecimal()
    {
        return decimal;
    }

    public int getLeading()
    {
        return _leading;
    }

    public int getTrailing()
    {
        return trailing_;
    }

    public int getSurrounding()
    {
        return _surrounding_;
    }

    public void fillRandom()
    {
        Random r = new Random();
        column  = r.nextInt();
        select  = r.nextInt();
        where   = r.nextInt();
        varchar = r.nextInt();
        decimal = r.nextInt();
        _leading = r.nextInt();
        trailing_ = r.nextInt();
        _surrounding_ = r.nextInt();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(getClass().getName() + ":");
        s.append("  JVM id = ").append(System.identityHashCode(this));
        s.append('\n');
        s.append("  column = ").append(column);
        s.append('\n');
        s.append("  select = ").append(select);
        s.append('\n');
        s.append("  where = ").append(where);
        s.append('\n');
        s.append("  varchar = ").append(varchar);
        s.append('\n');
        s.append("  decimal = ").append(decimal);
        s.append('\n');
        s.append("  _leading = ").append(_leading);
        s.append('\n');
        s.append("  trailing_ = ").append(trailing_);
        s.append('\n');
        s.append("  _surrounding_ = ").append(_surrounding_);
        s.append('\n');
        return s.toString();
    }
}