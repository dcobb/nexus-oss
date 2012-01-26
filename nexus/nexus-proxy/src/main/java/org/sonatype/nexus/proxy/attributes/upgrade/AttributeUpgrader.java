/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.proxy.attributes.upgrade;

/**
 * Component responsible for "upgrading" of the legacy attributes to new attributes on upgraded systems.
 * 
 * @author cstamas
 * @since 2.0
 */
public interface AttributeUpgrader
{
    /**
     * Returns true if folder holding legacy attributes is present.
     * 
     * @return
     */
    boolean isLegacyAttributesDirectoryPresent();

    /**
     * Returns true if {@link #isLegacyAttributesDirectoryPresent()} returns true, and those folders are not marked as
     * "done".
     * 
     * @return
     */
    boolean isUpgradeNeeded();

    /**
     * Returns true if the UpgraderThread is started, and is alive.
     * 
     * @return
     */
    boolean isUpgradeRunning();

    /**
     * Returns true if {@link #isUpgradeRunning()} returns false, and {@link #isLegacyAttributesDirectoryPresent()}
     * returns true, and those folders are marked as "done".
     * 
     * @return
     */
    boolean isUpgradeFinished();

    /**
     * Returns the UPS of currently executing upgrade (if {@link #isUpgradeRunning()} returns true). Otherwise, it
     * returns -1.
     * 
     * @return
     */
    int getCurrentUps();

    /**
     * Returns the max UPS of currently executing upgrade (if {@link #isUpgradeRunning()} returns true). Otherwise, it
     * returns -1.
     * 
     * @return
     */
    int getLimiterUps();

    /**
     * Sets the max UPS of currently executing upgrade (if {@link #isUpgradeRunning()} returns true). Otherwise, it is
     * neglected.
     * 
     * @return
     */
    void setLimiterUps( int limit );

    /**
     * Starts the upgrade thread if needed.
     */
    void upgrade();
}
