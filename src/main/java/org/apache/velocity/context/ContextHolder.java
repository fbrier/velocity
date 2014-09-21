package org.apache.velocity.context;

import java.util.Stack;

/**
 * <p>ContextHolder allows the Velocity Context to be available to the thread without having to pass it as a parameter.
 * This is the same technique used by J2EE and Spring security context holders.  The initial problem being addressed is
 * the include directive not working because the FileResourceLoader is hard coded.
 * </p>
 * User: <a href="email:fbrier@multideck.com">Frederick N. Brier</a><br/>
 * Created: 9/19/14 4:20 PM<br/>
 * <p/>
 * Copyright @2014 Multideck Corporation.  All rights reserved.<br/>
 */
public class ContextHolder
{
    private static final ThreadLocal<Stack<Context>> contextStackHolder = new ThreadLocal<Stack<Context>>();

    public static void clearContext()
    {
        contextStackHolder.remove();
    }

    public static Context peekContext()
    {
        Stack<Context> contextStack = getContextStack();
        return contextStack.peek();
    }

    public static Context popContext()
    {
        Stack<Context> contextStack = getContextStack();
        return contextStack.pop();
    }

    public static Context pushContext( Context context )
    {
        if ( null == context )
            throw new IllegalArgumentException( "context parameter may not be null" );
        Stack<Context> contextStack = getContextStack();
        return contextStack.push( context );
    }

    private static Stack<Context> getContextStack()
    {
        Stack<Context> contextStack = contextStackHolder.get();
        if ( contextStack == null )
        {
            contextStack = new Stack<Context>();
            contextStackHolder.set( contextStack );
        }
        return contextStack;
    }
}
