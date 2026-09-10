@contacts
Feature: Gestión de contactos en Contact List

  Background: Usuario autenticado para cada escenario
    Given que tengo un usuario registrado para probar el login
    When inicio sesión con las credenciales correctas
    Then la respuesta de autenticación debe tener el status 200

  @TC_031 @create @happyPath
  Scenario: Crear contacto
    Given que tengo un body válido para crear un contacto
    When creo el contacto
    Then la respuesta de contactos debe tener el status 201

    When consulto el contacto creado
    Then la respuesta de contactos debe tener el status 200
    And el contacto debe contener los datos enviados

  @TC_032 @update @happyPath
  Scenario: Actualizar contacto
    Given que tengo un contacto registrado
    And que tengo un body para actualizar el contacto completo
    When actualizo el contacto completo
    Then la respuesta de contactos debe tener el status 200

    When consulto el contacto creado
    Then la respuesta de contactos debe tener el status 200
    And el contacto debe contener los datos enviados

  @TC_033 @partialUpdate @happyPath
  Scenario: Actualizar parcialmente el contacto
    Given que tengo un contacto registrado
    And que tengo un body para actualizar solo el teléfono del contacto
    When actualizo parcialmente el contacto
    Then la respuesta de contactos debe tener el status 200

    When consulto el contacto creado
    Then la respuesta de contactos debe tener el status 200
    And el contacto debe contener los datos enviados

  @TC_034 @delete @happyPath
  Scenario: Eliminar contacto
    Given que tengo un contacto registrado
    When elimino el contacto creado
    Then la respuesta de contactos debe tener el status 200

    When consulto la lista de contactos
    Then la respuesta de contactos debe tener el status 200
    And la lista de contactos debe quedar vacía
