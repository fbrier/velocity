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
import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;
import junit.framework.TestCase;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base test case that provides utility methods for
 * the rest of the tests.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author Nathan Bubna
 * @version $Id: BaseTestCase.java 898032 2010-01-11 19:51:03Z nbubna $
 */
public abstract class BaseTestCase extends TestCase implements TemplateTestBase
{
    Logger logger = LoggerFactory.getLogger( BaseTestCase.class );

    protected VelocityEngine engine;
    protected VelocityContext context;
    protected boolean DEBUG = false;
    protected String stringRepoName = "string.repo";

    public BaseTestCase(String name)
    {
        super(name);

        // if we're just running one case, then have DEBUG
        // automatically set to true
        String testcase = System.getProperty("testcase");
        if (testcase != null)
        {
            DEBUG = testcase.equals(getClass().getName());
        }
    }

    protected void setUp() throws Exception
    {
        engine = new VelocityEngine();

        //by default, make the engine's log output go to the test-report
//        log = new TestLogChute(false, false);
//        log.setEnabledLevel(TestLogChute.INFO_ID);
//        log.setSystemErrLevel(TestLogChute.WARN_ID);
//        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, log);

        // use string resource loader by default, instead of file
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,string,classpath");
        engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        engine.addProperty("string.resource.loader.repository.name", stringRepoName);
        engine.addProperty("string.resource.loader.repository.static", "false");

        setUpEngine(engine);

        context = new VelocityContext();
        setUpContext(context);
    }

    protected void setUpEngine(VelocityEngine engine)
    {
        // extension hook
    }

    protected void setUpContext(VelocityContext context)
    {
        // extension hook
    }

    protected StringResourceRepository getStringRepository()
    {
        StringResourceRepository repo =
            (StringResourceRepository)engine.getApplicationAttribute(stringRepoName);
        if (repo == null)
        {
            engine.init();
            repo =
                (StringResourceRepository)engine.getApplicationAttribute(stringRepoName);
        }
        return repo;
    }

    protected void addTemplate(String name, String template)
    {
        logger.info("Template '"+name+"':  "+template);
        getStringRepository().putStringResource(name, template);
    }

    protected void removeTemplate(String name)
    {
        logger.info("Removed: '"+name+"'");
        getStringRepository().removeStringResource(name);
    }

    public void tearDown()
    {
        engine = null;
        context = null;
    }

    public void testBase()
    {
        if (DEBUG && engine != null)
        {
            assertSchmoo("");
            assertSchmoo("abc\n123");
        }
    }

    /**
     * Compare an expected string with the given loaded template
     */
    protected void assertTmplEquals(String expected, String template)
    {
        logger.info("Expected:  " + expected + " from '" + template + "'");

        StringWriter writer = new StringWriter();
        try
        {          
            engine.mergeTemplate(template, "utf-8", context, writer);
        }
        catch (RuntimeException re)
        {
            logger.info("RuntimeException!", re);
            throw re;
        }
        catch (Exception e)
        {
            logger.info("Exception!", e);
            throw new RuntimeException(e);
        }

        logger.info("Result:  " + writer.toString());
        assertEquals(expected, writer.toString());  
    }
    
    /**
     * Ensure that a context value is as expected.
     */
    protected void assertContextValue(String key, Object expected)
    {
        logger.info("Expected value of '"+key+"': "+expected);
        Object value = context.get(key);
        logger.info("Result: "+value);
        assertEquals(expected, value);
    }

    /**
     * Ensure that a template renders as expected.
     */
    protected void assertEvalEquals(String expected, String template)
    {
        logger.info("Expectation: "+expected);
        assertEquals(expected, evaluate(template));
    }

    /**
     * Ensure that the given string renders as itself when evaluated.
     */
    protected void assertSchmoo(String templateIsExpected)
    {
        assertEvalEquals(templateIsExpected, templateIsExpected);
    }

    /**
     * Ensure that an exception occurs when the string is evaluated.
     */
    protected Exception assertEvalException(String evil)
    {
        return assertEvalException(evil, null);
    }

    /**
     * Ensure that a specified type of exception occurs when evaluating the string.
     */
    protected Exception assertEvalException(String evil, Class exceptionType)
    {
        try
        {
            if (!DEBUG)
            {
//                log.off();
            }
            if (exceptionType != null)
            {
                logger.info("Expectation: "+exceptionType.getName());
            }
            evaluate(evil);
            fail("Template '"+evil+"' should have thrown an exception.");
        }
        catch (Exception e)
        {
            if (exceptionType != null && !exceptionType.isAssignableFrom(e.getClass()))
            {
                fail("Was expecting template '"+evil+"' to throw "+exceptionType+" not "+e);
            }
            return e;
        }
        finally
        {
            if (!DEBUG)
            {
//                log.on();
            }
        }
        return null;
    }

    /**
     * Ensure that the error message of the expected exception has the proper location info.
     */
    protected Exception assertEvalExceptionAt(String evil, String template, int line, int col)
    {
        String loc = template+"[line "+line+", column "+col+"]";
        logger.info("Expectation: Exception at "+loc);
        Exception e = assertEvalException(evil);

        logger.info("Result: "+e.getClass().getName()+" - "+e.getMessage());
        if (e.getMessage().indexOf(loc) < 1)
        {
            fail("Was expecting exception at "+loc+" instead of "+e.getMessage());
        }
        return e;
    }

    /**
     * Only ensure that the error message of the expected exception
     * has the proper line and column info.
     */
    protected Exception assertEvalExceptionAt(String evil, int line, int col)
    {
         return assertEvalExceptionAt(evil, "", line, col);
    }

    /**
     * Evaluate the specified String as a template and return the result as a String.
     */
    protected String evaluate(String template)
    {
        StringWriter writer = new StringWriter();
        try
        {
            logger.info("Template: "+template);

            // use template as its own name, since our templates are short
            // unless it's not that short, then shorten it...
            String name = (template.length() <= 15) ? template : template.substring(0,15);
            engine.evaluate(context, writer, name, template);

            String result = writer.toString();
            logger.info("Result: "+result);
            return result;
        }
        catch (RuntimeException re)
        {
            logger.info("RuntimeException!", re);
            throw re;
        }
        catch (Exception e)
        {
            logger.info("Exception!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Concatenates the file name parts together appropriately.
     *
     * @return The full path to the file.
     */
    protected String getFileName(final String dir, final String base, final String ext)
    {
        return getFileName(dir, base, ext, false);
    }

    protected String getFileName(final String dir, final String base, final String ext, final boolean mustExist)
    {
        StringBuffer buf = new StringBuffer();
        try
        {
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
                    logger.info(msg);
                    fail(msg);
                }

                if (!testFile.isFile())
                {
                    String msg = "getFileName() result " + testFile.getPath() + " is not a file!";
                    logger.info(msg);
                    fail(msg);
                }
            }
        }
        catch (IOException e)
        {
            fail("IO Exception while running getFileName(" + dir + ", " + base + ", "+ ext + ", " + mustExist + "): " + e.getMessage());
        }

        return buf.toString();
    }

    /**
     * Assures that the results directory exists.  If the results directory
     * cannot be created, fails the test.
     */
    protected void assureResultsDirectoryExists(String resultsDirectory)
    {
        File dir = new File(resultsDirectory);
        if (!dir.exists())
        {
            logger.info("Template results directory ("+resultsDirectory+") does not exist");
            if (dir.mkdirs())
            {
                logger.info("Created template results directory");
                if (DEBUG)
                {
                    logger.info("Created template results directory: "+resultsDirectory);
                }
            }
            else
            {
                String errMsg = "Unable to create '"+resultsDirectory+"'";
                logger.info(errMsg);
                fail(errMsg);
            }
        }
    }


    //TODO: drop this for JDK regex once we move to JDK 1.5
    private static Perl5Util perl = new Perl5Util();
    /**
     * Normalizes lines to account for platform differences.  Macs use
     * a single \r, DOS derived operating systems use \r\n, and Unix
     * uses \n.  Replace each with a single \n.
     *
     * @author <a href="mailto:rubys@us.ibm.com">Sam Ruby</a>
     * @return source with all line terminations changed to Unix style
     */
    protected String normalizeNewlines (String source)
    {
        return perl.substitute("s/\r[\r]?[\n]/\n/g", source);
    }

    /**
     * Returns whether the processed template matches the
     * content of the provided comparison file.
     *
     * @return Whether the output matches the contents
     *         of the comparison file.
     *
     * @exception Exception Test failure condition.
     */
    protected boolean isMatch (String resultsDir,
                               String compareDir,
                               String baseFileName,
                               String resultExt,
                               String compareExt) throws Exception
    {
        String result = getFileContents(resultsDir, baseFileName, resultExt);
        return isMatch(result,compareDir,baseFileName,compareExt);
    }


    protected String getFileContents(String dir, String baseFileName, String ext)
    {
        String fileName = getFileName(dir, baseFileName, ext, true);
        return StringUtils.fileContentsToString(fileName);
    }

    /**
     * Returns whether the processed template matches the
     * content of the provided comparison file.
     *
     * @return Whether the output matches the contents
     *         of the comparison file.
     *
     * @exception Exception Test failure condition.
     */
    protected boolean isMatch (String result,
                               String compareDir,
                               String baseFileName,
                               String compareExt) throws Exception
    {
        String compare = getResourceContents( compareDir, baseFileName, compareExt );

        // normalize each wrt newline
        result = normalizeNewlines(result);
        compare = normalizeNewlines(compare);
        if (DEBUG)
        {
            logger.info("Expection: "+compare);
            logger.info("Result: "+result);
        }
        return result.equals(compare);
    }

    protected String getResourceContents( String dir, String baseFileName, String ext )
    {
        StringBuffer buf = new StringBuffer();
        if ( ( null != dir ) && ! dir.isEmpty() )
        {
            buf.append( dir ).append( File.separator );
        }

        buf.append( baseFileName );

        if (org.apache.commons.lang.StringUtils.isNotEmpty(ext))
        {
            buf.append('.').append(ext);
        }

        getClass().getResourceAsStream( buf.toString() );
        Scanner scanner = new Scanner( getClass().getResourceAsStream( buf.toString() ) ).useDelimiter( "\\z" );
        StringBuffer contents = new StringBuffer();
        while ( scanner.hasNext() )
        {
            contents.append( scanner.next() );
        }
        return contents.toString();
    }

    /**
     * Turns a base file name into a test case name.
     *
     * @param s The base file name.
     * @return  The test case name.
     */
    protected static final String getTestCaseName(String s)
    {
        StringBuffer name = new StringBuffer();
        name.append(Character.toTitleCase(s.charAt(0)));
        name.append(s.substring(1, s.length()).toLowerCase());
        return name.toString();
    }

    protected String calcPathToTestDirectory( String path, String knownFile, String ext )
    {
        String file = getClass().getResource( getFileName( path, knownFile, ext) ).getFile();
        return  file.substring( 0, file.lastIndexOf( File.separator ) );

    }
}
