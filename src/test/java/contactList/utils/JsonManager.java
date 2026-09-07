package contactList.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

//leer, convertir y manipular el json
public class JsonManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonManager() {
    }

    public static String leerJsonComoString(String rutaRelativa) {
        final var rutaPayload = obtenerRutaPayload(rutaRelativa);

        try {
            return Files.readString(rutaPayload);
        } catch (IOException e) {
            Logs.error("No se pudo leer el archivo JSON: %s", rutaPayload);
            throw new IllegalStateException("No se pudo leer el archivo JSON: " + rutaPayload, e);
        }
    }

    public static String leerJsonComoString(String rutaRelativa, Map<String, String> valores) {
        var json = leerJsonComoString(rutaRelativa);

        for (var entry : valores.entrySet()) {
            json = json.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return json;
    }

    public static <T> T parsearJson(String content, Class<T> clazz) {
        try {
            return mapper.readValue(content, clazz);
        } catch (JacksonException e) {
            Logs.error("No se pudo convertir el JSON a la clase: %s", clazz.getSimpleName());
            throw new IllegalStateException("No se pudo convertir el JSON", e);
        }
    }

    public static <T> List<T> parsearListaJson(String content, Class<T> clazz) {
        try {
            return mapper.readValue(
                    content,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (JacksonException e) {
            Logs.error("No se pudo convertir el JSON a lista de: %s", clazz.getSimpleName());
            throw new IllegalStateException("No se pudo convertir el JSON a lista", e);
        }
    }

    public static String convertirObjetoAJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JacksonException e) {
            Logs.error("No se pudo convertir el objeto a JSON");
            throw new IllegalStateException("No se pudo convertir el objeto a JSON", e);
        }
    }

    private static Path obtenerRutaPayload(String rutaRelativa) {
        return Path.of(Config.get("path.payloads")).resolve(rutaRelativa);
    }
}