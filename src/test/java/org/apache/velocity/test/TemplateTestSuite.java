package org.apache.velocity.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import junit.framework.TestSuite;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * Test suite for Templates.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id: TemplateTestSuite.java 704299 2008-10-14 03:13:16Z nbubna $
 */
public class TemplateTestSuite extends TestSuite implements TemplateTestBase
{
    private Properties testProperties;

    /**
     * Creates an instace of the Apache Velocity test suite.
     */
    public TemplateTestSuite()
    {
        try
        {
            String loaderPath = calcPathToTestDirectory( FILE_RESOURCE_LOADER_PATH, "arithmetic", "vm" );
            Velocity.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH, loaderPath);
            Velocity.setProperty( Velocity.CLASSPATH_RESOURCE_LOADER_PATH, FILE_RESOURCE_LOADER_PATH);
            Velocity.setProperty( RuntimeConstants.EVENTHANDLER_INCLUDE, "org.apache.velocity.app.event.implement.IncludeRelativePath" );
            Velocity.setProperty( RuntimeConstants.RESOURCE_LOADER, "file,classpath" );
            //RuntimeSingleton.init();

//            Velocity.setProperty( Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, TestLogChute.class.getName());

            Velocity.init();

            testProperties = new Properties();
            testProperties.load(new FileInputStream( getFile(FILE_RESOURCE_LOADER_PATH, "templates", "properties")));
        }
        catch (Exception e)
        {
            System.err.println("Cannot setup TemplateTestSuite!");
            e.printStackTrace();
            System.exit(1);
        }

        addTemplateTestCases();
    }

    /**
     * Adds the template test cases to run to this test suite.  Template test
     * cases are listed in the <code>TEST_CASE_PROPERTIES</code> file.
     */
    private void addTemplateTestCases()
    {
        String template;
        for (int i = 1 ;; i++)
        {
            template = testProperties.getProperty(getTemplateTestKey(i));

            if (template != null)
            {
                System.out.println("Adding TemplateTestCase : " + template);
                addTest(new TemplateTestCase(template));
            }
            else
            {
                // Assume we're done adding template test cases.
                break;
            }
        }
    }

    /**
     * Macro which returns the properties file key for the specified template
     * test number.
     *
     * @param nbr The template test number to return a property key for.
     * @return    The property key.
     */
    private static final String getTemplateTestKey(int nbr)
    {
        return ("test.template." + nbr);
    }

    protected String calcPathToTestDirectory( String path, String knownFile, String ext ) throws IOException
    {
        String file = getFile( path, knownFile, ext );
        return  file.substring( 0, file.lastIndexOf( File.separator ) );

    }

    private String getFile( String path, String knownFile, String ext ) throws IOException
    {
        return getClass().getResource( getFileName( path, knownFile, ext) ).getFile();
    }

    /**
     * Concatenates the file name parts together appropriately.
     *
     * @return The full path to the file.
     */
    protected String getFileName(final String dir, final String base, final String ext) throws IOException
    {
        return getFileName(dir, base, ext, false);
    }

    protected String getFileName(final String dir, final String base, final String ext, final boolean mustExist) throws IOException
    {
        StringBuffer buf = new StringBuffer();
        File baseFile = new File(base);
        if (dir != null)
        {
            if (!baseFile.isAbsolute())
            {
                baseFile = new File(dir, base);
            }

            buf.append(baseFile.getCanonicalPath());
        }
        else
        {
            buf.append(baseFile.getPath());
        }

        if (org.apache.commons.lang.StringUtils.isNotEmpty(ext))
        {
            buf.append('.').append(ext);
        }

        if (mustExist)
        {
            File testFile = new File(buf.toString());
//                File testFile = new File( this.getClass().getResource( buf.toString() ).getFile() );

            if (!testFile.exists())
            {
                String msg = "getFileName() result " + testFile.getPath() + " does not exist!";
                throw new FileNotFoundException( msg );
            }

            if (!testFile.isFile())
            {
                String msg = "getFileName() result " + testFile.getPath() + " is not a file!";
                throw new FileNotFoundException( msg );
            }
        }

        return buf.toString();
    }
}
