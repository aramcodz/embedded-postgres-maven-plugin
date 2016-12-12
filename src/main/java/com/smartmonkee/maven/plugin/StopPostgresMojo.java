/**
 * 
 */
package com.smartmonkee.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.smartmonkee.embed.service.PostgresEmbeddedService;

/**
 * @author Aram S. Openden
 *
 */

@Mojo(name = "stoppostgres", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class StopPostgresMojo extends AbstractMojo {

    private static final String STOP_ERROR_MSG = "There was an Error trying to STOP the postgres process!";

    @Parameter( property = "startpostgres.skipExecution", defaultValue = "false" )
    private boolean skipExecution;    
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
	 if (skipExecution) {
	     getLog().info( "Skip Stop Postgres Service - No-Op" );
	     return;
	 }	
	
	PostgresEmbeddedService postgresService = 
		(PostgresEmbeddedService) getPluginContext().get("embeddedPostgresService");
	try {
	    postgresService.stop();
	} catch (Exception e) {
	    throw new MojoExecutionException(STOP_ERROR_MSG, e);
	}
//	getLog().info("Stop of Embedded Postgres process Succeeded!");
    }

}
