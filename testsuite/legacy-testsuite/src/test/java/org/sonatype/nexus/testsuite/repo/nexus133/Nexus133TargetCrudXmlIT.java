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
package org.sonatype.nexus.testsuite.repo.nexus133;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.test.utils.TargetMessageUtil;

/**
 * CRUD tests for XML request/response.
 */
public class Nexus133TargetCrudXmlIT
    extends Nexus133TargetCrudJsonIT
{

    @BeforeClass
    public static void setSecureTest(){
        TestContainer.getInstance().getTestContext().setSecureTest( true );
    }
    
    @Before
    public void setUp()
    {
        this.messageUtil = new TargetMessageUtil( this, this.getXMLXStream(), MediaType.APPLICATION_XML );
    }
    
    @Test
    public void readTest()
        throws IOException
    {
        super.readTest();
    }
    
}
