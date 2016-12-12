/**
 * 
 */
package com.smartmonkee.maven.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.smartmonkee.embed.postgresql.config.DownloadConfigBuilder;
import com.smartmonkee.embed.service.PostgresEmbeddedService;

import org.apache.maven.plugins.annotations.LifecyclePhase;

/**
 * @author Aram S. Openden
 *
 */
@Mojo(name = "startpostgres", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class StartPostgresMojo extends AbstractMojo {

    private static final String ERROR_MSG_START = "There was a problem attempting to start the embedded Postgreql instance at ";

    private static final String DEFAULT = "DEFAULT";

    @Parameter( property = "startpostgres.skipExecution", defaultValue = "false" )
    private boolean skipExecution;
    
    @Parameter( property = "startpostgres.postgresVersion", defaultValue = "V9_3_6" )
    private String postgresVersion;
    
    @Parameter( property = "startpostgres.dbUrl", defaultValue = "localhost" )
    private String dbUrl;    
    
    @Parameter( property = "startpostgres.dbPort", defaultValue = "5432" )
    private Integer dbPort;
    
    @Parameter( property = "startpostgres.dbName")
    private String dbName;
    
    @Parameter( property = "startpostgres.dbUsername")
    private String dbUsername;      

    @Parameter( property = "startpostgres.dbPassword")
    private String dbPassword;      
    
    @Parameter( property = "startpostgres.proxyUrl" )
    private String proxyUrl;      
    
    @Parameter( property = "startpostgres.proxyPort", defaultValue = "8080" )
    private Integer proxyPort;    
    
    @Parameter( property = "startpostgres.downloadUrl" )
    private String downloadUrl;       
    
    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
	 getLog().info( "Initialize Start Postgres Mojo" );
	 
	 if (skipExecution) {
	     getLog().info( "Skip Running Postgres Service - No-Op" );
	     return;
	 }
	 
	 if (StringUtils.isEmpty(downloadUrl) || downloadUrl.equalsIgnoreCase(DEFAULT)) {
	     downloadUrl = DownloadConfigBuilder.DEFAULT_INSTALLATION_DOWNLOAD_URL;
	 }
	 
	 PostgresEmbeddedService postgresService = new PostgresEmbeddedService(
		 postgresVersion, dbUrl, dbPort, dbName, dbUsername, dbPassword, 
		 proxyUrl, proxyPort, getLog(), 30000, downloadUrl);
	 try {
	    postgresService.start();
	} catch (Exception e) {
	    String errMsg = ERROR_MSG_START + dbUrl + ":" + dbPort;
	    throw new MojoExecutionException(errMsg, e);
	}
	 getPluginContext().put("embeddedPostgresService", postgresService);
    }

}
