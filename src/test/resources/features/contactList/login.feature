@auth
Feature: Autenticación de usuarios

  @TC_028 @login @happyPath
  Scenario: Iniciar sesión con credenciales válidas
    Given que tengo un usuario registrado para probar el login
    When inicio sesión con las credenciales correctas
    Then la respuesta de autenticación debe tener el status 200

  @TC_029 @login @negative
  Scenario: Rechazar el login con contraseña incorrecta
    Given que tengo un usuario registrado para probar el login
    When inicio sesión con una contraseña incorrecta
    Then la respuesta de autenticación debe tener el status 401

  @TC_030 @logout @happyPath
  Scenario: Cerrar sesión de un usuario autenticado
    Given que tengo un usuario registrado para probar el login
    When inicio sesión con las credenciales correctas
    Then la respuesta de autenticación debe tener el status 200
    When cierro la sesión del usuario
    Then la respuesta de autenticación debe tener el status 200
