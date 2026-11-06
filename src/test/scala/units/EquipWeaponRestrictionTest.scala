package cl.uchile.dcc
package units

import munit.FunSuite
import items.{Sword, Dagger, Bow, Wand, Staff}
import actions.Attack
import exceptions.{IncompatibleWeaponException, MissingWeaponException}

class EquipWeaponRestrictionTest extends FunSuite:

  test("Knight can equip Sword, Dagger, Bow, but not Wand or Staff") {
    val knight = new Knight("Cloud", maxHp = 100, defense = 10, weight = 20)
    val sword = new Sword("Buster Sword", attackPoints = 30, weight = 10)
    val wand = new Wand("Wonder Wand", attackPoints = 5, magicAttackPoints = 20, weight = 5)

    // Allowed
    knight.equip(sword)
    assertEquals(knight.weaponSlot, Some(sword))

    // Disallowed: Wand must throw IncompatibleWeaponException
    intercept[IncompatibleWeaponException] {
      knight.equip(wand)
    }
  }

  test("BlackMage can equip Wand, Staff, Dagger, but not Sword or Bow") {
    val blackMage = new BlackMage("Vivi", maxHp = 60, maxMp = 10, defense = 15, weight = 80)
    val wand = new Wand("Fire Wand", attackPoints = 5, magicAttackPoints = 25, weight = 4)
    val sword = new Sword("Broadsword", attackPoints = 20, weight = 10)

    // Allowed
    blackMage.equip(wand)
    assertEquals(blackMage.weaponSlot, Some(wand))

    // Disallowed: Sword must throw IncompatibleWeaponException
    intercept[IncompatibleWeaponException] {
      blackMage.equip(sword)
    }
  }

  test("A character without an equipped weapon cannot perform an Attack action") {
    val unarmedKnight = new Knight("Unarmed Cloud", maxHp = 100, defense = 10, weight = 20)
    val goblin = new Enemy("Goblin", maxHp = 50, attack = 15, defense = 5, weight = 10)
    val attack = new Attack()
    unarmedKnight.addAction(attack)

    intercept[MissingWeaponException] {
      unarmedKnight.doAction(attack, goblin)
    }
  }

