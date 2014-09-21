package org.apache.velocity.context;

import org.apache.velocity.runtime.resource.Resource;

/**
 * <p>ResourceContext is ...
 * </p>
 * User: <a href="email:fbrier@multideck.com">Frederick N. Brier</a><br/>
 * Created: 9/20/14 8:47 AM<br/>
 * <p/>
 * Copyright @2014 Multideck Corporation.  All rights reserved.<br/>
 */
public interface ResourceContext
{
    /**
     *  temporary fix to enable #include() to figure out
     *  current encoding.
     *
     * @return The current resource.
     */
    Resource getCurrentResource();

    /**
     * @param r
     */
    void setCurrentResource( Resource r );
}
