# Contact List Automation API

Proyecto de automatización para validar la API pública de Contact List usando **Rest Assured**, **Cucumber**, **JUnit Platform**, **Maven** y **Allure Report**.

La suite cubre flujos de autenticación, gestión de usuarios y gestión de contactos. Cada escenario crea sus propios datos de prueba con emails únicos y, al finalizar, los hooks eliminan los usuarios y contactos creados para mantener limpio el ambiente.

## Stack técnico
- Java 21
- Maven
- Rest Assured
- Cucumber
- JUnit
- Maven Failsafe
- Allure -> reportes
- java

## Ejecución

El comando principal para ejecutar la suite y generar el reporte Allure es:

```bash
mvn clean verify allure:report
```

## Reportes

Después de ejecutar:

```bash
mvn clean verify allure:report
```

los principales artefactos quedan en:

- `target/allure-results/`: resultados crudos generados por Allure.
- `target/site/allure-maven-plugin/index.html`: reporte HTML de Allure.
- `target/destination/cucumber.json`: salida JSON de Cucumber.
- `target/timeline/`: timeline generado por Cucumber.
- `target/failsafe-reports/`: reportes de Maven Failsafe.


```text
target/site/allure-maven-plugin/index.html
```

## Arquitectura del proyecto

```text
contact-list-automation-api/
├── pom.xml
├── settings.xml
├── sonar-project-custom.properties
├── mvnw
├── mvnw.cmd
├── README.md
└── src/
    ├── main/java/co/com/bdb/automation/
    │   └── utilities/
    │       └── EnvironmentValuesTask.java
    └── test/
        ├── java/co/com/bdb/automation/
        │   ├── definitions/
        │   │   ├── BaseTest.java
        │   │   ├── ContactDefinitions.java
        │   │   ├── LoginDefinitions.java
        │   │   ├── UserDefinitions.java
        │   │   └── hooks/
        │   │       └── Hooks.java
        │   ├── runner/
        │   │   └── CucumberTestSuite.java
        │   └── utilities/
        │       └── ContactListApi.java
        └── resources/
            ├── allure.properties
            ├── junit-platform.properties
            ├── bodies/contactList/
            ├── features/contactList/
            └── schemas/contactList/
```

## Componentes principales

- `EnvironmentValuesTask`: resuelve variables de entorno. Define la URL base de Contact List.
- `ContactListApi`: centraliza la configuración HTTP de Rest Assured, la URL base, paths principales, autenticación Bearer y login.
- `BaseTest`: comparte estado entre steps mediante inyección de Cucumber/Picocontainer: respuesta, token, usuario, contacto, email, password y escenario.
- `Hooks`: prepara el escenario y limpia datos creados. Si existe un usuario al final del escenario, elimina contactos pendientes y luego elimina el perfil del usuario.
- `CucumberTestSuite`: runner JUnit Platform que ejecuta los features ubicados en `features/contactList`.
- `junit-platform.properties`: configura plugins de Cucumber para Allure, JSON, pretty output y timeline.

## Escenarios cubiertos

### Autenticación

Archivo: `src/test/resources/features/contactList/login.feature`

- Login exitoso con credenciales válidas.
- Login rechazado con contraseña incorrecta.
- Logout de usuario autenticado.

### Usuarios

Archivo: `src/test/resources/features/contactList/user.feature`

- Creación exitosa de usuario.
- Validación de campos obligatorios vacíos.
- Validación de email repetido.
- Validación de emails inválidos.
- Validación de contraseña menor a 7 caracteres.
- Creación con caracteres especiales en nombre y apellido.
- Actualización de perfil autenticado.
- Consulta de perfil autenticado.
- Eliminación de usuario.

### Contactos

Archivo: `src/test/resources/features/contactList/contacts.feature`

- Creación de contacto.
- Consulta de contacto creado.
- Actualización completa de contacto.
- Actualización parcial de contacto.
- Eliminación de contacto.
- Validación de lista vacía después de eliminar.


```

