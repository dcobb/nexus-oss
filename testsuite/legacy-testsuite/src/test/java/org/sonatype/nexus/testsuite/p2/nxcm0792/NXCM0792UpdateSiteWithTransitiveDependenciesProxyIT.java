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
package org.sonatype.nexus.testsuite.p2.nxcm0792;

import org.junit.Test;
import org.sonatype.nexus.testsuite.p2.AbstractNexusProxyP2IT;
import org.sonatype.nexus.test.utils.TaskScheduleUtil;

public class NXCM0792UpdateSiteWithTransitiveDependenciesProxyIT
    extends AbstractNexusProxyP2IT
{

    public NXCM0792UpdateSiteWithTransitiveDependenciesProxyIT()
    {
        super( "nxcm0792" );
    }

    @Test
    public void test()
        throws Exception
    {
        TaskScheduleUtil.run( "1" );
        TaskScheduleUtil.waitForAllTasksToStop();

        installAndVerifyP2Feature(
            "com.sonatype.nexus.p2.its.feature3.feature.group",
            new String[]{ "com.sonatype.nexus.p2.its.feature3_1.0.0" },
            new String[]{
                "com.sonatype.nexus.p2.its.bundle_1.0.0.jar",
                "com.sonatype.nexus.p2.its.bundle3_1.0.0.jar"
            }
        );
    }

}
