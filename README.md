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
                + RequestSignerAWS.java             | Clase para firmar la peticion con AWS Lambda URL
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

- Primero cree un archivo **.env** en la raiz del proyecto y agregue las variables de entorno necesarias para la ejecucion de los test. No olvide actualizar tambien el archivo **.env.example** con las variables de entorno necesarias pero sin sus valores.
- Luego ejecute el comando `./mvnw install` para instalar las dependencias del proyecto.

## Comandos:
- Para ejecutar los test: `./mvnw verify`
- Para instalar dependencias: `./mvnw install`
- Para limpiar el proyecto: `./mvnw clean`

## Consejos y recomendaciones:
- En la ruta src/test/java/co/com/bdb/automation/pojos agregue una clase POJO por cada recurso que va a probar, en esta clase agregue los atributos del cuerpo (body) y/o cabeceras (headers) que va a usar en el test.
- En la ruta src/test/resources/features agregue una carpeta por cada recurso que va a probar, en esta carpeta agregue los archivos de definicion de los escenarios en formato Gherkin.
- En la ruta src/test/resources/schemas agregue una carpeta por cada recurso que va a probar, en esta carpeta agregue los archivos de definicion del esquema JSON que va a validar en el test.
- Si desea modificar el tag de cucumber que se esta usando, hagalo en el archivo **junit-platform.properties** o agregue en el comando `./mvnw verify` el flag `-Dcucumber.filter.tags="@tag"`.
- Asegurese de almacenar los datos sensibles como API KEYS, TOKENS, etc, en variables de entorno en el archivo **.env**.

## Para usar Lambda Bridge:
- En el archivo **.env** agregue la variable `ENDPOINT_LAMBDA` con la url del servicio de Lambda Bridge.
- En el metodo que envia la peticion, agregue el metodo sendWithLambda(), por ejemplo `request.when().sendWithLambda().post()`.

> [!NOTE]
> Si va a usar lambda bridge en local, importe las credenciales AWS_SESSION_TOKEN, AWS_SECRET_ACCESS_KEY y AWS_ACCESS_KEY_ID de su usuario en la cuenta de AWS correspondiente al archivo .env.
