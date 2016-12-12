/**
 * 
 */
package com.smartmonkee.embed.service;

/**
 * @author Aram S. Openden
 *
 */
public interface EmbeddedService {

    void start() throws Exception;

    void stop() throws Exception;

    boolean isStarted();
    
}
