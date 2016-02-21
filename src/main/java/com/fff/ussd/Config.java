package com.fff.ussd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author majidkabir
 */
public class Config {
    
    private Properties properties = new Properties();
    
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
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public String getParameter(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
}
