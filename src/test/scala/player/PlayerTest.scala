package cl.uchile.dcc
package player

import munit.FunSuite
import units.{Knight, Enemy}

class PlayerTest extends FunSuite:
  
  test("A Player should initialize correctly with name and initial units") {
    val knight = new Knight("Clive", maxHp = 100, defense = 15, weight = 20)
    val player = new Player("Player 1", List(knight))

    assertEquals(player.name, "Player 1")
    assertEquals(player.units.length, 1)
    assertEquals(player.units.head, knight)
    assert(!player.isDefeated)
  }

  test("A Player with no units should be considered defeated") {
    val emptyPlayer = new Player("Player 2")
    assert(emptyPlayer.isDefeated)
  }

  test("A Player can add units dynamically to their team") {
    val player = new Player("Player 3")
    assert(player.isDefeated)

    val enemy = new Enemy("Goblin", maxHp = 30, attack = 10, defense = 2, weight = 5)

    player.addUnit(enemy)

    assertEquals(player.units.length, 1)
    assert(!player.isDefeated)
  }