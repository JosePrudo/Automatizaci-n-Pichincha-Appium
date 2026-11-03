package com.pichincha.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carga la configuracion desde src/test/resources/config.properties y permite
 * sobreescribir cualquier valor mediante propiedades del sistema (-Dclave=valor).
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input =
                     ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("No se encontro config.properties en el classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        return System.getProperty(key, PROPERTIES.getProperty(key));
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return value != null ? Integer.parseInt(value.trim()) : defaultValue;
    }
}
