Feature: Estado

  Scenario: Estado
    Given Se usa el base path "estado"
    When Se envia el request con el metodo GET
    Then Se verifica que el status code sea 200
