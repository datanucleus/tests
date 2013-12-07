/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.jdo.JDOHelper;

import org.datanucleus.store.rdbms.mapping.datastore.StreamableSpooler;

/**
 * Class with a File member for persisting.
 */
public class FileHolder
{
    long id;

    String name;

    File file;

    public FileHolder(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public long getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
        JDOHelper.makeDirty(this, "file");
    }

    public void setFile(InputStream is)
    throws IOException
    {
        if (file == null) 
        {
            file = StreamableSpooler.instance().spoolStream(is);
        }
        else 
        {
            StreamableSpooler.instance().spoolStreamTo(is, file);
            JDOHelper.makeDirty(this, "file");
        }
    }

    /**
     * Convenience method to return the file contents.
     * @return Contents, or null if an exception occurs
     */
    public String getFileContents()
    {
        try
        {
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.getName()));
            return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(fileBytes)).toString();
        }
        catch (IOException ioe)
        {
            return null;
        }
    }
}