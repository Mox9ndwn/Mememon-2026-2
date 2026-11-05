package cl.uchile.dcc
package exceptions

import munit.FunSuite

class GameExceptionsTest extends FunSuite:

  test("DefeatedUnitException should be interceptable as a GameRuleException with its message") {
    val caught = intercept[GameRuleException] {
      throw new DefeatedUnitException("Unit is defeated")
    }
    assertEquals(caught.getMessage, "Unit is defeated")
  }

