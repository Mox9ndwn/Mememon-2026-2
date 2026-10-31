package cl.uchile.dcc
package map

import munit.FunSuite
import units.Knight

class PanelTest extends FunSuite:
  test("A Panel should initialise correctly with its coordinates") {
    val panel = new Panel(2, 3)
    assertEquals(panel.x, 2)
    assertEquals(panel.y, 3)
    assertEquals(panel.units, List())
    assertEquals(panel.neighbors, List())
  }

  test("A Panel should allow adding and removing units") {
    val panel = new Panel(0, 0)
    val knight = new Knight("Cecil", maxHp = 100, defense = 15, weight = 20)
    
    panel.addUnit(knight)
    assertEquals(panel.units.length, 1)
    assertEquals(panel.units.head, knight)
    
    panel.removeUnit(knight)
    assertEquals(panel.units, List())
  }
  
  test("A Panel should correctly register and link neighbor panels") {
    val centerPanel = new Panel(1, 1)
    val northPanel = new Panel(1, 2)
    val eastPanel = new Panel(2, 1)
    
    centerPanel.addNeighbor(northPanel)
    centerPanel.addNeighbor(eastPanel)
    
    assertEquals(centerPanel.neighbors.length, 2)
    assert(centerPanel.neighbors.contains(northPanel))
    assert(centerPanel.neighbors.contains(eastPanel))
  }