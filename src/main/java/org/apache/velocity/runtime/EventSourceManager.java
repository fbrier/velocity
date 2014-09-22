package org.apache.velocity.runtime;

import java.util.List;
import org.apache.velocity.runtime.resource.VelocityEvent;

/**
 * <p>EventSourceManager contains shallow references to two lists of listeners for this target class maintained in
 * RuntimeInstance.
 * </p>
 * User: <a href="email:fbrier@multideck.com">Frederick N. Brier</a><br/>
 * Created: 9/22/14 12:33 PM<br/>
 * <p/>
 * Copyright @2014 Multideck Corporation.  All rights reserved.<br/>
 */
public class EventSourceManager
{
    Object source;
    List<VelocityListener> listenerToEverything;
    List<VelocityListener> listeners;

    public EventSourceManager( Object source, List<VelocityListener> listenerToEverything, List<VelocityListener> listeners )
    {
        this.source = source;
        this.listenerToEverything = listenerToEverything;
        this.listeners = listeners;
    }

    public boolean hasListeners()
    {
        return ! ( listenerToEverything.isEmpty() && listeners.isEmpty() );
    }

    public void notify( String s )
    {
        VelocityEvent event = new VelocityEvent( source, s );
        for ( VelocityListener listener : listenerToEverything )
        {
            listener.handleVelocityEvent( event );
        }
        for ( VelocityListener listener : listeners )
        {
            listener.handleVelocityEvent( event );
        }
    }
}
