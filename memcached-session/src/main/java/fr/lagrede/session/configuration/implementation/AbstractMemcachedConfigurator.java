package fr.lagrede.session.configuration.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * M�thode utilitaire pour la lecture de {@link System#getProperties()}
 * 
 * @author anthony.lagrede
 *
 */
public abstract class AbstractMemcachedConfigurator {

    final Logger logger = LoggerFactory.getLogger(AbstractMemcachedConfigurator.class);

    protected static final String DEFAULT_LIST_SEPARATOR = ";";
    
    
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    public String getProperty(String key) {
        return System.getProperty(key);
    }
    
    public <T extends Enum<T>> T getEnum(Class<T> enumType, String key, T defaultValue) {
        T value = defaultValue;
        if (key != null) {
            String stringValue = getProperty(key, defaultValue.name()).toUpperCase();
            try {
                value = Enum.valueOf(enumType, stringValue);
            }
            catch (IllegalArgumentException e) {
                logger.error("Bad enum '" + enumType.getCanonicalName() + "' value '" + stringValue + "' for property '" + key + "' >> use default value : " + defaultValue);
            } 
        }
        return value;
    }

    public List<String>  getList(String key) {
        return getList(key, DEFAULT_LIST_SEPARATOR);
    }

    public List<String> getList(String key, String sSeparator) {
        List<String> lst = new ArrayList<String>();
        String     s   = getProperty(key, "");
        StringTokenizer st = new StringTokenizer(s, sSeparator);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken().trim();
            lst.add(tok);
        }
        return lst;
    }

    public Integer getInteger(String key) {
        String s = getProperty(key);
        try {
            return Integer.valueOf(s);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public  Integer getInteger(String key, int defaultValue) {
        Integer l = getInteger(key);
        return l == null
             ? Integer.valueOf(defaultValue)
             : l;
    }
    
    public boolean getboolean(String key, boolean defaultValue) {
        String s = getProperty(key, "").toLowerCase();
        if (s.equals("true")
         || s.equals("1")
         || s.equals("on")
         || s.equals("yes"))
            return true;
        if (s.equals("false")
         || s.equals("0")
         || s.equals("off")
         || s.equals("no"))
            return false;
        return defaultValue;
    }

    public Long getLong(String key) {
        String s = getProperty(key);
        try {
            return Long.valueOf(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    public  Long getLong(String key, long defaultValue) {
        Long l = getLong(key);
        return l == null
             ? Long.valueOf(defaultValue)
             : l;
    }

    
}
