package cl.uchile.dcc
package units

import munit.FunSuite

class CharacterTest extends FunSuite:
  
  test("A Knight should initialise correctly with base attributes") {
    val knight = new Knight("Clive", maxHp = 100, defense = 15, weight = 20)
    
    assertEquals(knight.name, "Clive")
    assertEquals(knight.maxHp, 100)
    assertEquals(knight.currentHp, 100)
    assertEquals(knight.defense, 15)
    assertEquals(knight.weight, 20)
    assertEquals(knight.weaponSlot, None)
    assertEquals(knight.inventory, List())
    assert(!knight.isDefeated)
  }

  test("An Archer should initialise correctly with base attributes") {
    val archer = new Archer("Faendal", maxHp = 80, defense = 10, weight = 12)

    assertEquals(archer.name, "Faendal")
    assertEquals(archer.maxHp, 80)
    assert(!archer.isDefeated)
  }
  
  test("A Thief should initialise correctly with base attributes") {
    val thief = new Thief("Brynjolf", maxHp = 75, defense = 8, weight = 10)
    
    assertEquals(thief.name, "Brynjolf")
    assertEquals(thief.maxHp, 75)
    assert(!thief.isDefeated)
  }

  test("A Black Mage should initialize correctly with mana points") {
    val blackMage = new BlackMage("Vivi", maxHp = 60, maxMp = 50, defense = 5, weight = 8)
    assertEquals(blackMage.name, "Vivi")
    assertEquals(blackMage.maxMp, 50)
    assertEquals(blackMage.currentMp, 50)
    assert(!blackMage.isDefeated)
  }

  test("A White Mage should initialize correctly with mana points") {
    val whiteMage = new WhiteMage("Flora", maxHp = 55, maxMp = 60, defense = 6, weight = 7)

    assertEquals(whiteMage.name, "Flora")
    assertEquals(whiteMage.maxMp, 60)
    assertEquals(whiteMage.currentMp, 60)
    assert(!whiteMage.isDefeated)
  }

  test("An Enemy should initialize correctly with attack points") {
    val goblin = new Enemy("Guard Hound", maxHp = 30, attack = 12, defense = 4, weight = 6)

    assertEquals(goblin.name, "Guard Hound")
    assertEquals(goblin.attack, 12)
    assert(!goblin.isDefeated)
  }
