@users
Feature: Creación de usuarios

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
  Scenario: Crear usuario con email inválido
    Given que tengo un body para crear usuario con email inválido
    When envío la solicitud para crear el usuario
    Then la respuesta de crear usuario debe tener el status 400
    And la respuesta de error debe contener el mensaje "Email is invalid"
