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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * Load templates from the Classpath.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:daveb@miceda-data.com">Dave Bryson</a>
 * @version $Id: MultiLoaderTestCase.java 832247 2009-11-03 01:29:30Z wglass $
 */
public class MultiLoaderTestCase extends BaseTestCase
{
     /**
     * VTL file extension.
     */
    private static final String TMPL_FILE_EXT = "vm";

    /**
     * Comparison file extension.
     */
    private static final String CMP_FILE_EXT = "cmp";

    /**
     * Comparison file extension.
     */
    private static final String RESULT_FILE_EXT = "res";

    /**
     * Results relative to the build directory.
     */
    private static final String RESULTS_DIR = TEST_RESULT_DIR + "/multiloader";

    /**
     * Path for templates. This property will override the
     * value in the default velocity properties file.
     */
    private final static String TEST_PATH = "/multiloader";

    /**
     * Results relative to the build directory.
     */
    private static final String COMPARE_DIR = "/multiloader/compare";

    VelocityEngine engine;
    
    /**
     * Default constructor.
     */
    public MultiLoaderTestCase(String name)
    {
        super(name);
    }

    public void setUp()
            throws Exception
    {
        engine = new VelocityEngine();
        
        assureResultsDirectoryExists(RESULTS_DIR);

        /*
         * Set up the file loader.
         */
        String jarFileName = getClass().getResource( TEST_PATH + "/test2.jar" ).getFile();
        int slashIndex = jarFileName.lastIndexOf( File.separator );
        String filePath = jarFileName.substring( 0, slashIndex );

        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");

        engine.setProperty( RuntimeConstants.FILE_RESOURCE_LOADER_PATH, filePath );

        engine.addProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");

        engine.addProperty(RuntimeConstants.RESOURCE_LOADER, "jar");

        /*
         *  Set up the classpath loader.
         */

        engine.setProperty( RuntimeConstants.CLASSPATH_RESOURCE_LOADER_CLASS,
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        engine.setProperty( RuntimeConstants.CLASSPATH_RESOURCE_LOADER_CACHE, "false");

        engine.setProperty( "classpath." + RuntimeConstants.RESOURCE_LOADER + ".modificationCheckInterval", "2" );

        /*
         *  setup the Jar loader
         */

        engine.setProperty( RuntimeConstants.JAR_RESOURCE_LOADER_CLASS,
                             "org.apache.velocity.runtime.resource.loader.JarResourceLoader");

        engine.setProperty( RuntimeConstants.JAR_RESOURCE_LOADER_PATH, "jar:file:" + jarFileName );


//        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, TestLogChute.class.getName());

        engine.init();
    }

    public static Test suite ()
    {
        return new TestSuite(MultiLoaderTestCase.class);
    }

    /**
     * Runs the test.
     */
    public void testMultiLoader ()
            throws Exception
    {
        /*
         *  lets ensure the results directory exists
         */
        assureResultsDirectoryExists(RESULTS_DIR);

        /*
         * Template to find with the file loader.
         */
        Template templatePath1 = engine.getTemplate( getFileName(null, "path1", TMPL_FILE_EXT));

        /*
         * Failure test - Template to find with the classpath loader.
         */
        try
        {
            engine.getTemplate("template/test1." + TMPL_FILE_EXT);
            fail( "should fail since test2.jar contains test2.vm, not test1.vm." );
        }
        catch ( ResourceNotFoundException e )
        {
            // Success - should fail.
        }

        /*
         * Template to find with the jar loader
         */
        Template templateTest2 = engine.getTemplate("template/test2." + TMPL_FILE_EXT);

        /*
         * Template to find with the classpath loader.
         */
        Template templatePath2 = engine.getTemplate( getFileName(TEST_PATH, "path2", TMPL_FILE_EXT));

        /*
         * and the results files
         */

        FileOutputStream fosPath1 =
            new FileOutputStream (
                getFileName(RESULTS_DIR, "path1", RESULT_FILE_EXT));

        FileOutputStream fosTest2 =
            new FileOutputStream (
                getFileName(RESULTS_DIR, "test2", RESULT_FILE_EXT));

        FileOutputStream fosPath2 =
            new FileOutputStream (
                getFileName(RESULTS_DIR, "path2", RESULT_FILE_EXT));

        Writer writerPath1 = new BufferedWriter(new OutputStreamWriter(fosPath1));
        Writer writerTest2 = new BufferedWriter(new OutputStreamWriter(fosTest2));
        Writer writerPath2 = new BufferedWriter(new OutputStreamWriter(fosPath2));

        /*
         *  put the Vector into the context, and merge both
         */

        VelocityContext context = new VelocityContext();

        templatePath1.merge( context, writerPath1 );
        writerPath1.flush();
        writerPath1.close();

        templateTest2.merge( context, writerTest2 );
        writerTest2.flush();
        writerTest2.close();

        templatePath2.merge( context, writerPath2 );
        writerPath2.flush();
        writerPath2.close();

        if (!isMatch(RESULTS_DIR,COMPARE_DIR,"path1",RESULT_FILE_EXT,CMP_FILE_EXT))
        {
            fail("Output incorrect for FileResourceLoader test.");
        }

        if (!isMatch(RESULTS_DIR,COMPARE_DIR,"test2",RESULT_FILE_EXT,CMP_FILE_EXT) )
        {
            fail("Output incorrect for JarResourceLoader test.");
        }

        if( !isMatch(RESULTS_DIR,COMPARE_DIR,"path2",RESULT_FILE_EXT,CMP_FILE_EXT))
        {
            fail("Output incorrect for ClasspathResourceLoader test.");
        }
    }
}
