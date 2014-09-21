package org.apache.velocity.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *  Simple (real simple...) classloader that depends
 *  on a Foo.class being located in the classloader
 *  directory under test
 */
public class TestClassloader extends ClassLoader
{
    private final static String testclass = "/classloader/Foo.class";

    private Class fooClass = null;

    public TestClassloader() throws Exception
    {
        File f = new File( getClass().getResource( testclass ).toURI() );

        byte[] barr = new byte[ (int) f.length() ];

        InputStream inputStream = new FileInputStream( f );
        inputStream.read( barr );
        inputStream.close();

        fooClass = defineClass("Foo", barr, 0, barr.length);
    }


    public Class findClass(String name)
    {
        return fooClass;
    }
}
