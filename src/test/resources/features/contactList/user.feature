@users
Feature: Gestión de usuarios

  @create @happyPath
  Scenario: Crear un usuario exitosamente
    Given que tengo un body válido para crear usuario
    When envío la solicitud para crear el usuario
    Then la respuesta de crear usuario debe tener el status 201
    And la respuesta de crear usuario debe cumplir con el schema de usuario
    And la respuesta de crear usuario debe incluir los datos del usuario creado
    And guardo el token de autenticación de Contact List

  @create @negative
  Scenario Outline: Crear usuario con campos obligatorios vacíos
    Given que tengo un body para crear usuario con el campo "<campo>" vacío
    When envío la solicitud para crear el usuario
    Then la respuesta de crear usuario debe tener el status 400
    And la respuesta de error debe contener el mensaje "<mensaje>"

    Examples:
      | campo     | mensaje                       |
      | firstName | Path `firstName` is required. |
      | lastName  | Path `lastName` is required.  |
      | email     | Email is invalid              |
      | password  | Path `password` is required.  |

  @create @negative
  Scenario: Crear usuario con email repetido
    Given que ya existe un usuario registrado para crear usuario
    When envío la solicitud para crear el usuario con el mismo email
    Then la respuesta de crear usuario debe tener el status 400
    And la respuesta de error debe contener el mensaje "Email address is already in use"

  @create @negative
  Scenario Outline: Crear usuario con email inválido: <email>
    Given que tengo un body para crear usuario con email inválido "<email>"
    When envío la solicitud para crear el usuario
    Then la respuesta de crear usuario debe tener el status 400
    And la respuesta de error debe contener el mensaje "<mensaje>"

    Examples:
      | email              | mensaje          |
      | invalid-email      | Email is invalid |
      | correocorreo.com   | Email is invalid |
      | correo@            | Email is invalid |
      | correo @correo.com | Email is invalid |
      | correo@correo .com | Email is invalid |


  @create @negative
  Scenario Outline: Crear usuario con contraseña por debajo de 7 caracteres: <longitud>
    Given que tengo un body para crear usuario con contraseña "<password>"
    When envío la solicitud para crear el usuario
    Then la respuesta de crear usuario debe tener el status 400
    And la respuesta de error debe contener el mensaje "<mensaje>"

    Examples:
      | longitud | password | mensaje                                              |
      | 1        | a        | is shorter than the minimum allowed length (7).      |
      | 6        | Abc123   | is shorter than the minimum allowed length (7).      |
      | 5        | c123/    | is shorter than the minimum allowed length (7).      |


  @create @happyPath
  Scenario Outline: Crear usuario con caracteres especiales en nombre y apellido
    Given que tengo un body para crear usuario con nombre "<nombre>" y apellido "<apellido>"
    When envío la solicitud para crear el usuario
    Then la respuesta de crear usuario debe tener el status 201
    And guardo el token de autenticación de Contact List

    Examples:
      | nombre | apellido |
      | José   | García   |
      | María  | Muñoz    |

  @update @happyPath
  Scenario: Actualizar los datos de un usuario autenticado
    Given que tengo un usuario registrado y autenticado
    And que tengo un body válido para actualizar el usuario
    When envío la solicitud para actualizar el usuario
    Then la respuesta del perfil de usuario debe tener el status 200

  @profile @happyPath
  Scenario: Consultar el perfil de un usuario autenticado
    Given que tengo un usuario registrado y autenticado
    When consulto el perfil del usuario autenticado
    Then la respuesta del perfil de usuario debe tener el status 200
    And el perfil debe contener los datos esperados del usuario
