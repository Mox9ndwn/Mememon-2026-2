package cl.uchile.dcc
package map

import munit.FunSuite
import units.Knight
import actions.Move
import exceptions.InvalidMovementException

class MovementRestrictionTest extends FunSuite:

  test("A unit can move to an adjacent neighbouring panel") {
    val knight = new Knight("Ramza", maxHp = 100, defense = 10, weight = 20)
    val panel1 = new Panel(0, 0)
    val panel2 = new Panel(0, 1)
    val move = new Move()
    knight.addAction(move)

    // Set up neighbour relationship
    panel1.addNeighbor(panel2)
    panel2.addNeighbor(panel1)

    // Place knight initially on panel1
    panel1.addUnit(knight)
    assertEquals(knight.currentPanel, Some(panel1))

    // Move to adjacent panel2
    knight.doAction(move, panel2)

    assertEquals(knight.currentPanel, Some(panel2))
    assert(!panel1.units.contains(knight))
    assert(panel2.units.contains(knight))
  }
  
  test("A unit attempting to move to a non-neighbouring panel should throw InvalidMovementException") {
    val knight = new Knight("Ramza", maxHp = 100, defense = 10, weight = 20)
    val panel1 = new Panel(0, 0)
    val distantPanel = new Panel(5, 5)
    val move = new Move()
    knight.addAction(move)
    
    // Not neighbours
    panel1.addUnit(knight)
    
    intercept[InvalidMovementException] {
      knight.doAction(move, distantPanel)
    }
    
    // Knight remains on panel1
    assertEquals(knight.currentPanel, Some(panel1))
    assert(panel1.units.contains(knight))
  }
  
