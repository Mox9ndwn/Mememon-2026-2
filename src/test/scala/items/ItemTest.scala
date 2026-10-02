package cl.uchile.dcc
package items

import munit.FunSuite

class ItemTest extends FunSuite:

  test("Common weapons should initialize correctly with attributes") {
    val sword = new Sword("Masamune", attackPoints = 40, weight = 12)
    val dagger = new Dagger("Iron Dagger", attackPoints = 15, weight = 4)
    val bow = new Bow("Daedric Bow", attackPoints = 25, weight = 8)

    assertEquals(sword.name, "Masamune")
    assertEquals(sword.attackPoints, 40)
    assertEquals(sword.weight, 12)
    assertEquals(sword.owner, None)

    assertEquals(dagger.attackPoints, 15)
    assertEquals(bow.attackPoints, 25)
  }
  
  test("Magic weapons should initialize correctly with magic attack points") {
    val wand = new Wand("Star Rod", attackPoints = 10, magicAttackPoints = 30, weight = 5)
    val staff = new Staff("Mage Staff", attackPoints = 12, magicAttackPoints = 35, weight = 7)

    assertEquals(wand.name, "Star Rod")
    assertEquals(wand.attackPoints, 10)
    assertEquals(wand.magicAttackPoints, 30)
    assertEquals(wand.weight, 5)

    assertEquals(staff.magicAttackPoints, 35)
  }
  
  test("Weapons should allow updating their owner") {
    val sword = new Sword("Buster Sword", attackPoints = 50, weight = 20)
    assertEquals(sword.owner, None)

    val knight = new units.Knight("Cloud", maxHp = 100, defense = 15, weight = 20)
    sword.setOwner(Some(knight))
    assertEquals(sword.owner, Some(knight))
  }
  
  test("Potions should initialize correctly with their names") {
    val healing = new HealingPotion()
    val fortitude = new FortitudePotion()
    val mana = new ManaPotion()
    val magicStrength = new MagicStrengthPotion()

    assertEquals(healing.name, "Healing Potion")
    assertEquals(fortitude.name, "Fortitude Potion")
    assertEquals(mana.name, "Mana Potion")
    assertEquals(magicStrength.name, "Magic Strength Potion")
  }
