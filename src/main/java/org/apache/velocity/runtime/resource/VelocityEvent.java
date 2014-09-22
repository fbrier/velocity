package org.apache.velocity.runtime.resource;

import java.util.EventObject;

/**
 * <p>VelocityEvent is ...
 * </p>
 * User: <a href="email:fbrier@multideck.com">Frederick N. Brier</a><br/>
 * Created: 9/22/14 12:03 PM<br/>
 * <p/>
 * Copyright @2014 Multideck Corporation.  All rights reserved.<br/>
 */
public class VelocityEvent extends EventObject
{
    private String message;

    public VelocityEvent( Object source, String message )
    {
        super( source );
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
}
