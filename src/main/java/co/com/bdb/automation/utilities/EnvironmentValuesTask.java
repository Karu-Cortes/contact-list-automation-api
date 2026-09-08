package co.com.bdb.automation.utilities;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentValuesTask {

    private final Dotenv dotenv;

    public EnvironmentValuesTask() {
        dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    public String getenv(String key) {
        String systemValue = System.getenv(key);

        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        return dotenv.get(key);
    }
}
