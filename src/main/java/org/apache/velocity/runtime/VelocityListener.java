package org.apache.velocity.runtime;

import org.apache.velocity.runtime.resource.VelocityEvent;

/**
 * <p>VelocityListener is ...
 * </p>
 * User: <a href="email:fbrier@multideck.com">Frederick N. Brier</a><br/>
 * Created: 9/22/14 12:00 PM<br/>
 * <p/>
 * Copyright @2014 Multideck Corporation.  All rights reserved.<br/>
 */
public interface VelocityListener
{
    String [] getTargetClasses();
    void handleVelocityEvent( VelocityEvent event );
}
