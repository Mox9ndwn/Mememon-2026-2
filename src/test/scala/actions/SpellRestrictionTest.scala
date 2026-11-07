package cl.uchile.dcc
package actions

import exceptions.{InsufficientManaException, InvalidTargetException, MissingWeaponException}
import items.{Bow, Wand}
import units.{Enemy, Knight, WhiteMage}

import munit.FunSuite

class SpellRestrictionTest extends FunSuite:

  test("Casting a spell requires MagicWeapon equipped") {
    val whiteMage = new WhiteMage("Rosa", maxHp = 80, maxMp = 50, defense = 5, weight = 12)
    val bow = new Bow("Elven Bow", attackPoints = 15, weight = 8)
    val heal = new Heal()
    whiteMage.addAction(heal)

    // Equipped with a non-magic weapon (Bow): must throw MissingWeaponException
    whiteMage.equip(bow)
    intercept[MissingWeaponException] {
      whiteMage.doAction(heal, whiteMage)
    }
  }

  test("Casting a spell requires sufficient MP and consumes it") {
    val whiteMage = new WhiteMage("Rosa", maxHp = 80, maxMp = 10, defense = 5, weight = 12)
    val wand = new Wand("Healing Wand", attackPoints = 5, magicAttackPoints = 12, weight = 4)
    val heal = new Heal(manaCost = 15) // Costs 15, unit only has 10
    whiteMage.addAction(heal)
    whiteMage.equip(wand)

    // Insufficient mana
    intercept[InsufficientManaException] {
      whiteMage.doAction(heal, whiteMage)
    }
  }

  test("Purify can only be received by enemies") {
    val whiteMage = new WhiteMage("Rosa", maxHp = 80, maxMp = 50, defense = 5, weight = 12)
    val wand = new Wand("Holy Wand", attackPoints = 5, magicAttackPoints = 25, weight = 4)
    val purify = new Purify(manaCost = 10)
    val knight = new Knight("Cecil" , maxHp = 100, defense = 10, weight = 20)
    val goblin = new Enemy("goblin", maxHp = 50, attack = 10, defense = 2, weight = 8)

    whiteMage.addAction(purify)
    whiteMage.equip(wand)

    // Casting Purify on an ally Character must throw InvalidTargetException
    intercept[InvalidTargetException] {
      whiteMage.doAction(purify, knight)
    }

    // Casting Purify on an Enemy should succeed
    whiteMage.doAction(purify, goblin)
    assert(goblin.currentHp < 50)
  }

  test("Heal can only be received by allied characters") {
    val whiteMage = new WhiteMage("Rosa", maxHp = 80, maxMp = 50, defense = 5, weight = 12)
    val wand = new Wand("Holy Wand", attackPoints = 5, magicAttackPoints = 25, weight = 4)
    val heal = new Heal(manaCost = 10)
    val knight = new Knight("Ramza", maxHp = 100, defense = 10, weight = 20)
    val goblin = new Enemy("Goblin", maxHp = 50, attack = 10, defense = 2, weight = 8)

    whiteMage.addAction(heal)
    whiteMage.equip(wand)

    // Damage knight first to verify healing
    knight.receiveAttack(30)
    assertEquals(knight.currentHp, 80)

    // Casting Heal on an Enemy must throw InvalidTargetException
    intercept[InvalidTargetException] {
      whiteMage.doAction(heal, goblin)
    }

    // Casting Heal on Knight should restore HP
    whiteMage.doAction(heal, knight)
    assert(knight.currentHp > 80)
  }
