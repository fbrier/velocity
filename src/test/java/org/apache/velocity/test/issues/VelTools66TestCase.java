package org.apache.velocity.test.issues;

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

import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.security.Permission;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.test.BaseTestCase;
import org.apache.velocity.util.introspection.Introspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case for <a href="https://issues.apache.org/jira/browse/VELTOOLS-66">Velocity Tools Issue 66</a>.
 */
public class VelTools66TestCase extends BaseTestCase
{
    Logger logger = LoggerFactory.getLogger( VelTools66TestCase.class );

    protected static boolean DEBUG = false;

    public VelTools66TestCase(final String name)
            throws Exception
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(VelTools66TestCase.class);
    }

    public void setUp()
            throws Exception
    {
//        Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, TestLogChute.class.getName());

        Velocity.init();
        System.setSecurityManager(new TestSecurityManager());

    }

    public void tearDown()
    {
        System.setSecurityManager(null);
    }

    public void testVelTools66()
            throws Exception
    {
        Method verifyMethod = TestInterface.class.getMethod("getTestValue", new Class[0]);


        RuntimeInstance ri = new RuntimeInstance();
        Introspector introspector = ri.getIntrospector();

        Method testMethod = introspector.getMethod(TestObject.class, "getTestValue", new Object[0]);
        assertNotNull(testMethod);
        assertEquals("Method object does not match!", verifyMethod, testMethod);
    }

    public static interface TestInterface
    {
        String getTestValue();

        void setTestValue(String testValue);
    }

    public static final class TestObject
            implements TestInterface
    {
        String testValue = null;

        public TestObject()
        {
        }

        public String getTestValue()
        {
            return testValue;
        }

        public void setTestValue(final String testValue)
        {
            this.testValue = testValue;
        }
    }

    public static final class TestSecurityManager extends SecurityManager
    {
        Logger logger = LoggerFactory.getLogger( TestSecurityManager.class );
        private final Class clazz = TestObject.class;

        public TestSecurityManager()
        {
            super();
        }

        public void checkMemberAccess(final Class c, final int i)
        {
            LoggerFactory.getLogger( VelTools66TestCase.class ).debug("checkMemberAccess(" + c.getName() + ", " + i + ")");

            if (c.equals(clazz))
            {
                throw new AccessControlException("You are not allowed to access TestObject directly!");
            }
        }

        public void checkRead(final String file)
        {
            logger.debug("checkRead(" + file + ")");
        }

        public void checkPackageAccess(final String s)
        {
            logger.debug("checkPackageAccess(" + s + ")");
        }

        public void checkPropertyAccess(final String s)
        {
            logger.debug("checkPropertyAccess(" + s + ")");
        }

        public void checkPermission(final Permission p)
        {
            logger.debug("checkPermission(" + p + ")");
        }
    }
}
