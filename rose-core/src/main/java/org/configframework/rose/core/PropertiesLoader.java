/**
 * 
 */
package org.configframework.rose.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;

/**
 * @author yuantengkai
 *
 */
public class PropertiesLoader {
	
	private static final String SCHEMA_CLASSPATH = "classpath:";
    
    public static Properties load(String resource) throws IOException {
        if(StringUtils.isBlank(resource)){
        	throw new NullPointerException("resource is null");
        }
        if(resource.startsWith(SCHEMA_CLASSPATH)) {
            return loadFromClassPath(resource.substring(SCHEMA_CLASSPATH.length()));
        } else {
            return loadFromFileSystem(resource);
        }
    }
    
    public static Properties loadFromClassPath(String file) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(file);
        if(url == null) {
        	throw new FileNotFoundException("file " + file + " is not exist in classpath");
        }
        return load(url);
    }
    
    public static Properties loadFromFileSystem(String file) throws IOException {
        File f = new File(file);
        if(!f.exists()) {
        	throw new FileNotFoundException("file " + file + " is not exist");
        }
        URL url = new File(file).toURI().toURL();
        return load(url);
    }
    
    
    private static Properties load(URL url) throws IOException {
        InputStream in = null;
        try {
            in = url.openStream();
            Properties props = new Properties();
            props.load(in);
            return props;
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
