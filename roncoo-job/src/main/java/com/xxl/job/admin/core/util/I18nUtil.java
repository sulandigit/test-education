package com.xxl.job.admin.core.util;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Internationalization Utility
 * 
 * This utility class provides internationalization (i18n) support for the
 * XXL-Job admin interface. It loads locale-specific property files and
 * provides methods to retrieve localized messages.
 * 
 * Features:
 * - Dynamic loading of locale-specific property files
 * - Caching of loaded properties for performance
 * - Support for multiple locales (zh_CN, zh_TC, en)
 * - Bulk message retrieval as JSON format
 * 
 * Property files are located in the i18n directory with naming pattern:
 * message_{locale}.properties (e.g., message_en.properties)
 * 
 * @author xuxueli 2018-01-17 20:39:06
 */
public class I18nUtil {
    /**
     * Logger instance for this utility class
     */
    private static Logger logger = LoggerFactory.getLogger(I18nUtil.class);

    /**
     * Cached properties for the current locale
     */
    private static Properties prop = null;
    
    /**
     * Load internationalization properties based on current locale setting
     * 
     * This method loads the appropriate property file based on the configured
     * locale and caches it for subsequent use. The property files should be
     * located in the classpath under i18n/ directory.
     * 
     * @return Properties object containing localized messages
     */
    public static Properties loadI18nProp(){
        if (prop != null) {
            return prop;
        }
        try {
            // Build i18n property file path
            String i18n = XxlJobAdminConfig.getAdminConfig().getI18n();
            String i18nFile = MessageFormat.format("i18n/message_{0}.properties", i18n);

            // Load properties with UTF-8 encoding
            Resource resource = new ClassPathResource(i18nFile);
            EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
            prop = PropertiesLoaderUtils.loadProperties(encodedResource);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return prop;
    }

    /**
     * Get localized message for the specified key
     *
     * @param key message key to look up
     * @return localized message string
     */
    public static String getString(String key) {
        return loadI18nProp().getProperty(key);
    }

    /**
     * Get multiple localized messages as JSON format
     * 
     * This method retrieves multiple localized messages and returns them
     * as a JSON string. If no keys are specified, all available messages
     * are returned.
     *
     * @param keys specific message keys to retrieve (optional)
     * @return JSON string containing key-value pairs of localized messages
     */
    public static String getMultString(String... keys) {
        Map<String, String> map = new HashMap<String, String>();

        Properties prop = loadI18nProp();
        if (keys!=null && keys.length>0) {
            // Retrieve specific keys
            for (String key: keys) {
                map.put(key, prop.getProperty(key));
            }
        } else {
            // Retrieve all available keys
            for (String key: prop.stringPropertyNames()) {
                map.put(key, prop.getProperty(key));
            }
        }

        String json = JacksonUtil.writeValueAsString(map);
        return json;
    }

}
