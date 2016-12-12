/**
 * 
 */
package com.smartmonkee.embed.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.smartmonkee.embed.postgresql.Command;
import com.smartmonkee.embed.postgresql.PostgresExecutable;
import com.smartmonkee.embed.postgresql.PostgresProcess;
import com.smartmonkee.embed.postgresql.PostgresStarter;
import com.smartmonkee.embed.postgresql.config.DownloadConfigBuilder;
import com.smartmonkee.embed.postgresql.config.PostgresConfig;
import com.smartmonkee.embed.postgresql.config.RuntimeConfigBuilder;
import com.smartmonkee.embed.postgresql.distribution.Version;
import com.smartmonkee.embed.postgresql.ext.ArtifactStoreBuilder;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.HttpProxyFactory;

import static com.smartmonkee.embed.postgresql.config.AbstractPostgresConfig.*;

import java.util.Arrays;

/**
 * @author Aram S. Openden
 *
 */
public class PostgresEmbeddedService implements EmbeddedService {
    
    protected final String version;
    protected final String host;
    protected final int port;
    protected final String dbName;
    protected final String username;
    protected final String password;
    protected final Integer timeout;
    private final String proxyHost;
    private final Integer proxyPort;
    private final String installDownloadUrl;
    protected boolean stopped = false;
    protected boolean started = false;
    private Log mavenLog;    
    PostgresProcess postgresProcess;
    

    public PostgresEmbeddedService(String postgresVersion, String dbUrl, Integer dbPort, String dbName,
	    String dbUsername, String dbPassword, String proxyUrl, Integer proxyPort, Log log, Integer initTimeout, 
	    String downloadUrl) {
	this.version = postgresVersion;
        this.username = dbUsername;
        this.password = dbPassword;
        this.host = dbUrl;
        this.port = dbPort;
        this.dbName = dbName;
        this.proxyHost = proxyUrl;
        this.proxyPort = proxyPort;
        this.mavenLog = log;
        this.timeout = initTimeout;
        this.installDownloadUrl = downloadUrl;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void start() throws Exception {
	DownloadConfigBuilder downloadConfigBuilder = new DownloadConfigBuilder();
	downloadConfigBuilder.defaultsForCommand(Command.Postgres);
	if (StringUtils.isNotEmpty(proxyHost) && proxyPort != null) {
	    downloadConfigBuilder.proxyFactory(new HttpProxyFactory(proxyHost, proxyPort));
	}
	downloadConfigBuilder.downloadPath(installDownloadUrl);

	IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.Postgres)
		.artifactStore(new ArtifactStoreBuilder().defaults(Command.Postgres).download(downloadConfigBuilder))
		.build();
	final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(runtimeConfig);
	Version dbVersion = Version.valueOf(this.version);
	
	final PostgresConfig configDb = new PostgresConfig(dbVersion, new Net(host, port), new Storage(dbName), 
		new Timeout(timeout), new Credentials(username, password));
	configDb.getAdditionalInitDbParams().addAll(Arrays.asList(
                "-E", "UTF-8",
                "--locale=en_US.UTF-8",
                "--lc-collate=en_US.UTF-8",
                "--lc-ctype=en_US.UTF-8"
            )); 	
	PostgresExecutable exec = runtime.prepare(configDb);
	postgresProcess = exec.start();
	this.mavenLog.info("Embedded postgres process Started with PID: " + postgresProcess.getProcessId());
	started = true;
    }

    @Override
    public void stop() throws Exception {
	this.mavenLog.info("Attempting to Stop the Embedded Postgres process");
	if (!stopped) {
	    if (postgresProcess != null) {
		this.mavenLog.debug("stopping Postgres process with PID: " + postgresProcess.getProcessId());
        	started = false;
		postgresProcess.stop();
		postgresProcess = null;
		this.mavenLog.info("Stop of Embedded Postgres process Succeeded!");
		stopped = true;
            } else {
        	this.mavenLog.info("Postgresql process does Not exist - Nothing to Stop!");
            }
        }
    }

    @Override
    public boolean isStarted() {
	return started;
    }

}
