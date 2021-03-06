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
package org.sonatype.security.configuration.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.configuration.ConfigurationException;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.security.configuration.model.SecurityConfiguration;
import org.sonatype.security.configuration.upgrade.SecurityConfigurationUpgrader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The default configuration source powered by Modello. It will try to load configuration, upgrade if needed and
 * validate it. It also holds the one and only existing Configuration object.
 * 
 * @author tstevens
 */
@Singleton
@Typed( SecurityConfigurationSource.class )
@Named( "file" )
public class FileSecurityConfigurationSource
    extends AbstractSecurityConfigurationSource
{
    /**
     * The configuration file.
     */
    private File configurationFile;

    /**
    * The configuration upgrader.
    */
    private SecurityConfigurationUpgrader configurationUpgrader;

    /**
     * The defaults configuration source.
     */
    private final SecurityConfigurationSource securityDefaults;

    private final PasswordHelper passwordHelper;

    /** Flag to mark defaulted config */
    private boolean configurationDefaulted;

    @Inject
    public FileSecurityConfigurationSource( @Named( "static" ) SecurityConfigurationSource securityDefaults,
                                            @Named( "${application-conf}/security-configuration.xml" ) File configurationFile,
                                            PasswordHelper passwordHelper,
                                            SecurityConfigurationUpgrader configurationUpgrader )
    {
        this.securityDefaults = securityDefaults;
        this.configurationFile = configurationFile;
        this.passwordHelper = passwordHelper;
        this.configurationUpgrader = configurationUpgrader;
    }

    /**
     * Gets the configuration file.
     * 
     * @return the configuration file
     */
    public File getConfigurationFile()
    {
        return configurationFile;
    }

    /**
     * Sets the configuration file.
     * 
     * @param configurationFile the new configuration file
     * @deprecated replaced by constructor injection
     */
    @Deprecated
    public void setConfigurationFile( File configurationFile )
    {
        this.configurationFile = configurationFile;
    }

    public SecurityConfiguration loadConfiguration()
        throws ConfigurationException, IOException
    {
        // propagate call and fill in defaults too
        securityDefaults.loadConfiguration();

        if ( getConfigurationFile() == null || getConfigurationFile().getAbsolutePath().contains( "${" ) )
        {
            throw new ConfigurationException( "The configuration file is not set or resolved properly: "
                + ( getConfigurationFile() == null ? "null" : getConfigurationFile().getAbsolutePath() ) );
        }

        if ( !getConfigurationFile().exists() )
        {
            this.getLogger().warn( "No configuration file in place, copying the default one and continuing with it." );

            // get the defaults and stick it to place
            setConfiguration( securityDefaults.getConfiguration() );

            saveConfiguration( getConfigurationFile() );

            configurationDefaulted = true;
        }
        else
        {
            configurationDefaulted = false;
        }

        loadConfiguration( getConfigurationFile() );

        // check for loaded model
        if ( getConfiguration() == null )
        {
            upgradeConfiguration( getConfigurationFile() );
            
            loadConfiguration( getConfigurationFile() );
        }

        return getConfiguration();
    }

    public void storeConfiguration()
        throws IOException
    {
        saveConfiguration( getConfigurationFile() );
    }

    public InputStream getConfigurationAsStream()
        throws IOException
    {
        return new FileInputStream( getConfigurationFile() );
    }

    public SecurityConfigurationSource getDefaultsSource()
    {
        return securityDefaults;
    }

     protected void upgradeConfiguration( File file )
    		 throws IOException,
    		 ConfigurationException
     {
	     this.getLogger().info( "Trying to upgrade the security configuration file {}", file.getAbsolutePath() );
	    
	     setConfiguration( configurationUpgrader.loadOldConfiguration( file ) );
	    
	     // after all we should have a configuration
	     if ( getConfiguration() == null )
	     {
	    	 throw new ConfigurationException( "Could not upgrade Security configuration! Please replace the "
	    			 + file.getAbsolutePath() + " file with a valid Security configuration file." );
	     }
	     
         // Need to decrypt the anonymous user's password
         SecurityConfiguration configuration = this.getConfiguration();
         if ( StringUtils.isNotEmpty( configuration.getAnonymousPassword() ) )
         {
             String encryptedPassword = configuration.getAnonymousPassword();
             try
             {
                 configuration.setAnonymousPassword( this.passwordHelper.decrypt( encryptedPassword ) );
             }
             catch ( PlexusCipherException e )
             {
                 this.getLogger().error( "Failed to decrypt anonymous user's password in security-configuration.xml, password might be encrypted in memory.",
                                         e );
             }
         }
	    
	     this.getLogger().info( "Creating backup from the old file and saving the upgraded security configuration." );
	    
	     // backup the file
	     File backup = new File( file.getParentFile(), file.getName() + ".bak" );
	    
	     FileUtils.copyFile(file, backup);
	    
	     // set the upgradeInstance to warn the application about this
	     setConfigurationUpgraded( true );
	    
	     saveConfiguration( file );
     }

    /**
     * Load configuration.
     */
    private void loadConfiguration( File file )
        throws IOException
    {
        this.getLogger().info( "Loading Security configuration from {}", file.getAbsolutePath() );

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream( file );

            loadConfiguration( fis );

            // decrypte the anon users password
            SecurityConfiguration configuration = this.getConfiguration();
            if ( configuration != null && StringUtils.isNotEmpty( configuration.getAnonymousPassword() ) )
            {
                String encryptedPassword = configuration.getAnonymousPassword();
                try
                {
                    configuration.setAnonymousPassword( this.passwordHelper.decrypt( encryptedPassword ) );
                }
                catch ( PlexusCipherException e )
                {
                    this.getLogger().error( "Failed to decrype anonymous user's password in security-configuration.xml, password might be encrypted in memory.",
                                            e );
                }
            }
        }
        finally
        {
            if ( fis != null )
            {
                fis.close();
            }
        }
    }

    /**
     * Save configuration.
     * 
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void saveConfiguration( File file )
        throws IOException
    {
        FileOutputStream fos = null;

        File backupFile = new File( file.getParentFile(), file.getName() + ".old" );

        try
        {
            // Create the dir if doesn't exist, throw runtime exception on failure
            // bad bad bad
            if ( !file.getParentFile().exists() && !file.getParentFile().mkdirs() )
            {
                String message =
                    "\r\n******************************************************************************\r\n"
                        + "* Could not create configuration file [ "
                        + file.toString()
                        + "]!!!! *\r\n"
                        + "* Application cannot start properly until the process has read+write permissions to this folder *\r\n"
                        + "******************************************************************************";

                this.getLogger().error( message );
            }

            // copy the current security config file as file.bak
            if ( file.exists() )
            {
                FileUtils.copyFile( file, backupFile );
            }

            SecurityConfiguration configuration = getConfiguration();
            checkNotNull(configuration, "Missing security configuration");

            String clearPassword = configuration.getAnonymousPassword();
            try
            {
                String encryptedPassword = this.passwordHelper.encrypt( clearPassword );
                configuration.setAnonymousPassword( encryptedPassword );
            }
            catch ( PlexusCipherException e )
            {
                this.getLogger().error( "Filed to encrypte the anonymous users password, using clear text: " + e );
            }

            fos = new FileOutputStream( file );

            saveConfiguration( fos, configuration );

            // set back to clear text
            configuration.setAnonymousPassword( clearPassword );

            fos.flush();
        }
        finally
        {
            IOUtil.close( fos );
        }

        // if all went well, delete the bak file
        FileUtils.forceDelete(backupFile);
    }

    /**
     * Was the active configuration fetched from config file or from default source? True if it from default source.
     */
    public boolean isConfigurationDefaulted()
    {
        return configurationDefaulted;
    }

}
