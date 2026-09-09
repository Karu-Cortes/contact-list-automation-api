# bbog-cat-api-rest-assured-template-testing

> [!NOTE]
> Este proyecto es solo una plantilla, uselo como referencia para crear sus propios test de api con rest assured.

## Estructura del proyecto:

```
.github                                             |
  + workflows                                       | Github Actions workflows
    + requirements.yml                              | Pipeline de analisis de calidad de codigo
    + test-integration.yml                          | Pipeline de integracion de pruebas
.mvn                                                | Carpeta de configuracion de maven
  + wrapper                                         |
    + maven-wrapper.jar                             | Wrapper de maven
    + maven-wrapper.properties                      | Propiedades del wrapper de maven
src                                                 |
  + main                                            | Source main
    + java                                          |
        + co.com.bdb.automation                     |
            + utilities                             | 
                + CustomRequestSpecification.java   | Clase para la configuracion de la peticion
                + EnvironmentValuesTask.java        | Clase para obtener las variables de entorno
  + test                                            | Source test
    + java                                          | 
        + co.com.bdb.automation                     |
            + definitions                           |
                + {resourceName}Definitions.java    | Clase para definir los pasos de los escenarios
                + hooks                             |
                    + Hooks.java                    | Clase para definir los hooks de los escenarios
            + pojos                                 |
                + {resourceName}Pojo.java           | Clase POJO para el manejo de datos
            + runner                                |
                + CucumberTestSuite.java            | Clase para ejecutar los escenarios con Cucumber
    + resources                                     |
        + features                                  |
            + {resourceName}                        | Carpeta del manager de recursos a probar
                + {nameFeature}.feature             | Archivo de definicion de los escenarios
        + schemas                                   |
            + {resourceName}                        | Carpeta del manager de recursos a probar
                + {schemaName}.json                 | Archivo de definicion del esquema JSON
        + junit-platform.properties                 | Configuracion de JUnit y Cucumber
.env.example                                        | Ejemplo de variables de entorno
.env                                                | Variables de entorno
.gitignore                                          | Archivos ignorados por git
mvnw                                                | Script de maven para sistemas Unix
mvnw.cmd                                            | Script de maven para sistemas Windows
pom.xml                                             | Archivo de dependencias
README.md                                           | Documentacion del proyecto
settings.xml                                        | Configuracion de maven
sonar-project-custom.properties                     | Propiedades generales de Sonarcloud
```

## Requisitos:
- Java 21 (Recomendado)
- IDE Intellij IDEA (Recomendado)
    * Plugins:
        - .env files support
        - CheckStyle-IDEA
        - Cucumber for Java
        - Gherkin
        - Lombok

## Configurar proyecto:

- Con Java 21 y Maven instalados, ejecute `mvn verify` desde la raiz del proyecto. Maven descarga las dependencias y ejecuta los casos; no es necesario crear un archivo `.env`.
- La URL predeterminada es `https://thinking-tester-contact-list.herokuapp.com`, correspondiente a la [API Contact List](https://documenter.getpostman.com/view/4012288/TzK2bEa8).
- Para usar otra URL, configure `BASE_URL_CONTACT_LIST` como variable de entorno o copie `.env.example` a `.env` en la raiz del proyecto y cambie su valor. No agregue `/users` a la URL base. La variable de entorno tiene prioridad sobre `.env`; si el valor obtenido falta o esta en blanco, se usa la URL predeterminada.
- `.env` sigue excluido de Git para proteger configuraciones privadas. `.env.example` documenta las opciones disponibles y no debe contener secretos.
- En IntelliJ, configure el directorio de trabajo (Working directory) en la raiz del proyecto para cargar un `.env` opcional y los archivos de los casos.

## Comandos:
- Para ejecutar los test: `mvn verify`
- Para ejecutar los test limpiando recursos generados anteriores: `mvn clean verify`
- Para instalar dependencias: `mvn install`
- Para limpiar el proyecto: `mvn clean`
- Para generar resultados de Allure: `mvn clean verify`
- Para generar el reporte HTML de Allure: `mvn allure:report`
- Para abrir el reporte de Allure en un servidor local: `mvn allure:serve`

`target/allure-results` contiene los archivos JSON generados por las pruebas. El reporte visual se genera en `target/site/allure-maven-plugin/index.html`.
Las peticiones hechas con Rest Assured se adjuntan al reporte en cada step como `Request` y `HTTP/1.1 ...`, incluyendo body, headers y respuesta de la API.

## Casos de registro de usuarios

`user.feature` incluye body vacío, contraseñas de 1, 6, 7 y 8 caracteres, formatos de email inválido, nombres con tildes, ñ y apóstrofes, y tipos JSON incorrectos en los cuatro campos del registro.

Los escenarios `@passwordBoundary` expresan el requisito de mínimo 8 caracteres y los escenarios `@invalidTypes` esperan rechazar valores que no sean texto. En la verificación del 8 de septiembre de 2026, la API pública aceptó una contraseña de 7 caracteres, números y booleanos en `firstName`/`lastName`, y un número en `password` (HTTP 201). Estos seis casos se mantienen activos y fallan para mostrar las diferencias respecto a esas expectativas; es necesario confirmar el contrato antes de cambiarlas. El body `{}` devuelve HTTP 400 con errores de `firstName`, `lastName` y `password`.

Las respuestas de creación guardan el token antes de validar el resultado, para que el hook de limpieza intente eliminar también los usuarios creados inesperadamente en casos negativos.

## Consejos y recomendaciones:

- En la ruta src/test/java/co/com/bdb/automation/pojos agregue una clase POJO por cada recurso que va a probar, en esta clase agregue los atributos del cuerpo (body) y/o cabeceras (headers) que va a usar en el test.
- En la ruta src/test/resources/features agregue una carpeta por cada recurso que va a probar, en esta carpeta agregue los archivos de definicion de los escenarios en formato Gherkin.
- En la ruta src/test/resources/schemas agregue una carpeta por cada recurso que va a probar, en esta carpeta agregue los archivos de definicion del esquema JSON que va a validar en el test.
- Si desea modificar el tag de cucumber que se esta usando, hagalo en el archivo **junit-platform.properties** o agregue en el comando `mvn verify` el flag `-Dcucumber.filter.tags="@tag"`.
- Asegurese de almacenar los datos sensibles como API KEYS, TOKENS, etc, en variables de entorno en el archivo **.env**.
