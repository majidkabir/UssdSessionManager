package com.fff.ussd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author majidkabir
 */
public class Config {
    
    private Properties properties = new Properties();
    private static final Logger logger = Logger.getRootLogger();
    
    private static Config instance;
    
    public static Config getInstance() {
        //double check locking thread safe singleton pattern
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        
        return instance;
    }
    
    public Config() {
        try {
            properties.loadFromXML(getClass().getResourceAsStream("/config.xml"));
        } catch (FileNotFoundException ex) {
            logger.error("Config file not found!");
        } catch (IOException ex) {
            logger.error("Can't open config file!");
        }        
    }
    
    public String getParameter(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
}
