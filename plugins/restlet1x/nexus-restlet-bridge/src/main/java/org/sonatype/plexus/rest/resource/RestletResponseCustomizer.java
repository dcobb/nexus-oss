/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.plexus.rest.resource;

import org.restlet.data.Response;
import org.restlet.resource.Representation;

/**
 * Optional interface to be implemented by {@link Representation}s that need to customize Restlet {@link Response} as
 * for example add additional HTTP headers.
 *
 * @since 1.20
 */
public interface RestletResponseCustomizer
{

    /**
     * Callback just before returning the response to Restlet framework. The call is always done after the call to
     * {@link PlexusResource} GET/POST/PUT/UPLOAD was performed.
     *
     * @param response Restlet response
     */
    void customize( Response response );

}
