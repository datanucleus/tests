/**********************************************************************
Copyright (c) 2004 Erik Bengtson  and others. All rights reserved.
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

/**
 * Sample class utilising all possible combinations of null, not null, default, etc.
 * @version $Revision: 1.1 $
 */
public class Isnullable
{	 
	// metadata null-value="none"
    private Integer nullMetaDataNone;
    private Integer nonnullMetaDataNone;
    private Integer nullDfltMetaDataNone;
    private Integer nonnullDfltMetaDataNone;
    
    // medata null-value="default"
	private Integer nullMetaDataDflt;
	private Integer nonnullMetaDataDflt;
    private Integer nullDfltMetaDataDflt;
	private Integer nonnullDfltMetaDataDflt;
    
	// metadata null-value="exception"
	private Integer nullMetaDataExc;
	private Integer nonnullMetaDataExc;
	private Integer nullDfltMetaDataExc;
    private Integer nonnullDfltMetaDataExc;

	public boolean compareTo(Object obj)
	{
		if (!(obj instanceof Isnullable))
		{
			return false;
		}
		Isnullable isnullable = (Isnullable) obj;
		return isnullable.nullMetaDataNone == null ? (this.nullMetaDataNone == null)
			: (this.nullMetaDataNone != null && this.nullMetaDataNone.intValue() == isnullable.nullMetaDataNone.intValue())
			
			&& isnullable.nonnullMetaDataNone == null ? (this.nonnullMetaDataNone == null)
			: (this.nonnullMetaDataNone != null && this.nonnullMetaDataNone.intValue() == isnullable.nonnullMetaDataNone.intValue())
			
			&& isnullable.nullDfltMetaDataNone == null ? (this.nullDfltMetaDataNone == null)
			: (this.nullDfltMetaDataNone != null && this.nullDfltMetaDataNone.intValue() == isnullable.nullDfltMetaDataNone.intValue())
			
			&& isnullable.nonnullDfltMetaDataNone == null ? (this.nonnullDfltMetaDataNone == null)
			: (this.nonnullDfltMetaDataNone != null && this.nonnullDfltMetaDataNone.intValue() == isnullable.nonnullDfltMetaDataNone.intValue())

			&& isnullable.nullMetaDataDflt == null ? (this.nullMetaDataDflt == null)
			: (this.nullMetaDataDflt != null && this.nullMetaDataDflt.intValue() == isnullable.nullMetaDataDflt.intValue())
				
			&& isnullable.nonnullMetaDataDflt == null ? (this.nonnullMetaDataDflt == null)
			: (this.nonnullMetaDataDflt != null && this.nonnullMetaDataDflt.intValue() == isnullable.nonnullMetaDataDflt.intValue())
			
			&& isnullable.nullDfltMetaDataDflt == null ? (this.nullDfltMetaDataDflt == null)
			: (this.nullDfltMetaDataDflt != null && this.nullDfltMetaDataDflt.intValue() == isnullable.nullDfltMetaDataDflt.intValue())
			
			&& isnullable.nonnullDfltMetaDataDflt == null ? (this.nonnullDfltMetaDataDflt == null)
			: (this.nonnullDfltMetaDataDflt != null && this.nonnullDfltMetaDataDflt.intValue() == isnullable.nonnullDfltMetaDataDflt.intValue())

			&& isnullable.nullMetaDataExc == null ? (this.nullMetaDataExc == null)
			: (this.nullMetaDataExc != null && this.nullMetaDataExc.intValue() == isnullable.nullMetaDataExc.intValue())
			
			&& isnullable.nonnullMetaDataExc == null ? (this.nonnullMetaDataExc == null)
			: (this.nonnullMetaDataExc != null && this.nonnullMetaDataExc.intValue() == isnullable.nonnullMetaDataExc.intValue())

			&& isnullable.nullDfltMetaDataExc == null ? (this.nullDfltMetaDataExc == null)
			: (this.nullDfltMetaDataExc != null && this.nullDfltMetaDataExc.intValue() == isnullable.nullDfltMetaDataExc.intValue())
				
			&& isnullable.nonnullDfltMetaDataExc == null ? (this.nonnullDfltMetaDataExc == null)
			: (this.nonnullDfltMetaDataExc != null && this.nonnullDfltMetaDataExc.intValue() == isnullable.nonnullDfltMetaDataExc.intValue());
	}

    public Integer getNonnullDfltMetaDataDflt()
    {
        return nonnullDfltMetaDataDflt;
    }

    public Integer getNonnullDfltMetaDataExc()
    {
        return nonnullDfltMetaDataExc;
    }

    public Integer getNonnullDfltMetaDataNone()
    {
        return nonnullDfltMetaDataNone;
    }

    public Integer getNonnullMetaDataDflt()
    {
        return nonnullMetaDataDflt;
    }

    public Integer getNonnullMetaDataExc()
    {
        return nonnullMetaDataExc;
    }

    public Integer getNonnullMetaDataNone()
    {
        return nonnullMetaDataNone;
    }

    public Integer getNullDfltMetaDataDflt()
    {
        return nullDfltMetaDataDflt;
    }

    public Integer getNullDfltMetaDataExc()
    {
        return nullDfltMetaDataExc;
    }

    public Integer getNullDfltMetaDataNone()
    {
        return nullDfltMetaDataNone;
    }

    public Integer getNullMetaDataDflt()
    {
        return nullMetaDataDflt;
    }

    public Integer getNullMetaDataExc()
    {
        return nullMetaDataExc;
    }

    public Integer getNullMetaDataNone()
    {
        return nullMetaDataNone;
    }

    public void setNonnullDfltMetaDataDflt(Integer integer)
    {
        nonnullDfltMetaDataDflt = integer;
    }

    public void setNonnullDfltMetaDataExc(Integer integer)
    {
        nonnullDfltMetaDataExc = integer;
    }

    public void setNonnullDfltMetaDataNone(Integer integer)
    {
        nonnullDfltMetaDataNone = integer;
    }

    public void setNonnullMetaDataDflt(Integer integer)
    {
        nonnullMetaDataDflt = integer;
    }

    public void setNonnullMetaDataExc(Integer integer)
    {
        nonnullMetaDataExc = integer;
    }

    public void setNonnullMetaDataNone(Integer integer)
    {
        nonnullMetaDataNone = integer;
    }

    public void setNullDfltMetaDataDflt(Integer integer)
    {
        nullDfltMetaDataDflt = integer;
    }

    public void setNullDfltMetaDataExc(Integer integer)
    {
        nullDfltMetaDataExc = integer;
    }

    public void setNullDfltMetaDataNone(Integer integer)
    {
        nullDfltMetaDataNone = integer;
    }

    public void setNullMetaDataDflt(Integer integer)
    {
        nullMetaDataDflt = integer;
    }

    public void setNullMetaDataExc(Integer integer)
    {
        nullMetaDataExc = integer;
    }

    public void setNullMetaDataNone(Integer integer)
    {
        nullMetaDataNone = integer;
    }

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("nullMetaDataNone: "+nullMetaDataNone);
		buffer.append("\n");
		buffer.append("nonnullMetaDataNone: "+nonnullMetaDataNone);
		buffer.append("\n");
		buffer.append("nullDfltMetaDataNone: "+nullDfltMetaDataNone);
		buffer.append("\n");
		buffer.append("nonnullDfltMetaDataNone: "+nonnullDfltMetaDataNone);
		buffer.append("\n");

		buffer.append("nullMetaDataDflt: "+nullMetaDataDflt);
		buffer.append("\n");
		buffer.append("nonnullMetaDataDflt: "+nonnullMetaDataDflt);
		buffer.append("\n");
		buffer.append("nullDfltMetaDataDflt: "+nullDfltMetaDataDflt);
		buffer.append("\n");
		buffer.append("nonnullDfltMetaDataDflt: "+nonnullDfltMetaDataDflt);
		buffer.append("\n");

		buffer.append("nullMetaDataExc: "+nullMetaDataExc);
		buffer.append("\n");
		buffer.append("nonnullMetaDataExc: "+nonnullMetaDataExc);
		buffer.append("\n");
		buffer.append("nullDfltMetaDataExc: "+nullDfltMetaDataExc);
		buffer.append("\n");
		buffer.append("nonnullDfltMetaDataExc: "+nonnullDfltMetaDataExc);
		buffer.append("\n");
		return buffer.toString();
	}
}