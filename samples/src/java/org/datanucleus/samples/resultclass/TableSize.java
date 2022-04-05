/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
***********************************************************************/
package org.datanucleus.samples.resultclass;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Simple result class for SQL query giving the size of a table.
 * @version $Revision: 1.1 $
 */
public class TableSize
{
	Integer thesize;

    public TableSize(int thesize)
    {
        this.thesize = Integer.valueOf(thesize);
    }

    public TableSize(long thesize)
    {
        this.thesize = Integer.valueOf((int)thesize);
    }

    public TableSize(BigDecimal thesize)
    {
        this.thesize = Integer.valueOf(thesize.intValue());
    }

    public TableSize(BigInteger thesize)
    {
        this.thesize = Integer.valueOf(thesize.intValue());
    }

	public Integer getTheSize()
    {
		return thesize;
	}

	public void setTheSize(Integer thesize)
    {
		this.thesize = thesize;
	}

    public void setTheSize(Long thesize)
    {
        this.thesize = Integer.valueOf(thesize.intValue());
    }

    public void setTheSize(BigDecimal thesize)
    {
        this.thesize = Integer.valueOf(thesize.intValue());
    }

    public void setTheSize(BigInteger thesize)
    {
        this.thesize = Integer.valueOf(thesize.intValue());
    }
}