package co.com.bdb.automation.utilities;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentValuesTask {

    private static final String DEFAULT_CONTACT_LIST_BASE_URL =
            "https://thinking-tester-contact-list.herokuapp.com";

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

    public String getContactListBaseUrl() {
        String configuredUrl = getenv("BASE_URL_CONTACT_LIST");
        return configuredUrl == null || configuredUrl.isBlank()
                ? DEFAULT_CONTACT_LIST_BASE_URL
                : configuredUrl.trim();
    }
}
