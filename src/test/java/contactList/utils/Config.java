package contactList.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//tiene en cuenta las configuraciones de config.properties
public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            Logs.error(e.getMessage());
            throw new IllegalStateException("No se pudo leer las properties");
        }
    }

    public static String get(String key) {
        final var systemValue = System.getProperty(key);

        if (systemValue != null) {
            return systemValue;
        }

        return properties.getProperty(key);
    }
}
