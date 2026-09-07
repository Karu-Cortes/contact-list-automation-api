package contactList.utils;

import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public final class Logs {
    private static final Logger log = LogManager.getLogger("AUTOMATION");

    private static final ThreadLocal<StringBuilder> scenarioLogs =
            ThreadLocal.withInitial(StringBuilder::new);

    private Logs() {
    }

    public static void trace(String message) {
        log.trace(message);
        append("TRACE", message);
    }

    public static void debug(String message) {
        log.debug(message);
        append("DEBUG", message);
    }

    public static void info(String message) {
        log.info(message);
        append("INFO", message);
    }

    public static void warning(String message) {
        log.warn(message);
        append("WARN", message);
    }

    public static void error(String message) {
        log.error(message);
        append("ERROR", message);
    }

    public static void fatal(String message) {
        log.fatal(message);
        append("FATAL", message);
    }

    public static void trace(String format, Object... args) {
        trace(String.format(format, args));
    }

    public static void debug(String format, Object... args) {
        debug(String.format(format, args));
    }

    public static void info(String format, Object... args) {
        info(String.format(format, args));
    }

    public static void warning(String format, Object... args) {
        warning(String.format(format, args));
    }

    public static void error(String format, Object... args) {
        error(String.format(format, args));
    }

    public static void fatal(String format, Object... args) {
        fatal(String.format(format, args));
    }

    public static void attachToScenario(Scenario scenario) {
        final var logs = scenarioLogs.get().toString();

        if (!logs.isBlank()) {
            scenario.attach(
                    logs.getBytes(StandardCharsets.UTF_8),
                    "text/plain",
                    "Execution Logs"
            );
        }

        scenarioLogs.remove();
    }

    private static void append(String level, String message) {
        scenarioLogs.get().append(
                String.format("[%-5s] %s%n", level, message)
        );
    }
}
