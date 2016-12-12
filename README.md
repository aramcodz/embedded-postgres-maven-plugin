## Synopsis

The Embedded postgresql maven plugin supports creating of Integration tests in maven by starting up a postgresql instance/process in the pre-integration-test phase of the maven lifecycle and shuts down that same process in the post-integration-phase of the lifecycle. It depends upon the open-source [postgresql-embedded](https://github.com/yandex-qatools/postgresql-embedded) Java component.

## Notes on Usage

Currently (as of 10/26/2016), this plugin only supports the following 9.x versions of [Postgresql](https://www.postgresql.org/):
  * V9_2_4("9.2.4-1")
  * V9_3_6("9.3.6-1")
  * V9_4_4("9.4.4-1")
  * V9_5_0("9.5.0-1")

This restriction comes from the dependency on the [postgresql-embedded](https://github.com/yandex-qatools/postgresql-embedded) Java component.  

You use maven to compile this plugin and install it into your local maven repository (if you download the project or clone this Repo):
  `mvn clean install ` 

An example of using this plugin in the pom for your project (where you wish to use the plugin to set up your Integration tests):
```
      <plugin>
        <groupId>com.ge.current.maven</groupId>
        <artifactId>embedded-postgres-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <executions>
          <execution>
            <id>manage-service</id>
            <goals>
              <goal>startpostgres</goal>
              <goal>stoppostgres</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <postgresVersion>V9_5_0</postgresVersion>
          <dbName>YOUR_DB_NAMWE</dbName>
          <dbUsername>YOUR_DB_USER_ACCOUNT</dbUsername>
          <dbPassword>YOUR_DB_PASSWORD</dbPassword>
          <proxyUrl>http-proxy.appl.ge.com</proxyUrl> 
        </configuration>        
      </plugin>  
``` 

## Known Issues
1. __Note:__ that there appears to be some issue with some confusing/incorrect maven console (Warnings) output when running a maven build that *uses* this plugin. This issue appears to be something caused by the use of the underlying [postgres-embedded Java library](https://github.com/yandex-qatools/postgresql-embedded) So, when the "stop" postgres action runs in the `post-integration-phase` you may see some console output like this:

```
[INFO] --- embedded-postgres-maven-plugin:0.0.1-SNAPSHOT:stoppostgres (manage-service) @ LightGrid ---
[INFO] Attempting to Stop the Embedded Postgres process
Oct 28, 2016 11:37:16 AM ru.yandex.qatools.embed.postgresql.PostgresProcess stopInternal
INFO: trying to stop postgresql
[INFO] start AbstractPostgresConfig{storage=Storage{dbDir=/var/folders/8g/69wh31fn7nx3q81phwfdpld00000gn/T/postgresql-embed-a304fe66-9b14-47ef-94e8-2de787943696/db-content-589b3260-c83e-4ad2-9742-a135c64de3ac, dbName='myDB', isTmpDir=true}, network=Net{host='localhost', port=5432}, timeout=Timeout{startupTimeout=30000}, credentials=Credentials{username='myDbUser', password='myPassword'}, args=[stop], additionalInitDbParams=[]}
Oct 28, 2016 11:37:17 AM ru.yandex.qatools.embed.postgresql.PostgresProcess sendStopToPostgresqlInstance
INFO: Cleaning up after the embedded process (removing /var/folders/8g/69wh31fn7nx3q81phwfdpld00000gn/T/postgresql-embed-a304fe66-9b14-47ef-94e8-2de787943696)...
Oct 28, 2016 11:37:18 AM ru.yandex.qatools.embed.postgresql.PostgresProcess stopInternal
WARNING: could not stop postgresql with command, try next
[INFO] stopOrDestroyProcess: process hasn't exited 

[INFO] execSuccess: false [kill, -2, 9482]
Oct 28, 2016 11:37:19 AM ru.yandex.qatools.embed.postgresql.PostgresProcess stopInternal
WARNING: could not stop postgresql, try next
[INFO] stopOrDestroyProcess: process hasn't exited 

[INFO] execSuccess: false [kill, 9482]
Oct 28, 2016 11:37:19 AM ru.yandex.qatools.embed.postgresql.PostgresProcess stopInternal
WARNING: could not stop postgresql, try next
Oct 28, 2016 11:37:19 AM ru.yandex.qatools.embed.postgresql.PostgresProcess stopInternal
WARNING: could not stop postgresql the second time, try one last thing
Oct 28, 2016 11:37:19 AM ru.yandex.qatools.embed.postgresql.PostgresProcess deleteTempFiles
WARNING: Could not delete temp db dir: /var/folders/8g/69wh31fn7nx3q81phwfdpld00000gn/T/postgresql-embed-a304fe66-9b14-47ef-94e8-2de787943696/db-content-589b3260-c83e-4ad2-9742-a135c64de3ac
[WARNING] Could not delete pid file: /var/folders/8g/69wh31fn7nx3q81phwfdpld00000gn/T/postgresql-embed-a304fe66-9b14-47ef-94e8-2de787943696/pgsql/bin/postgres.pid
[INFO] Stop of Embedded Postgres process Succeeded!
```
Despite these Warnings & misleading INFO messages, if you see the following log entries, the shutdown postgres process has (very likely) succeded:
```
[INFO] Attempting to Stop the Embedded Postgres process
Oct 28, 2016 11:37:16 AM ru.yandex.qatools.embed.postgresql.PostgresProcess stopInternal
INFO: trying to stop postgresql

INFO: Cleaning up after the embedded process (removing /[SOME_TEMP_MAVEN_FOLDER])...

[INFO] Stop of Embedded Postgres process Succeeded!
```

For reference, [An issue](https://github.com/yandex-qatools/postgresql-embedded/issues/50) has been opened with the maintainers of the postgresql-embedded project.


## License

[Apache 2](https://www.apache.org/licenses/LICENSE-2.0)