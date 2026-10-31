package cl.uchile.dcc
package actions

import munit.FunSuite
import items.{Sword, Wand, HealingPotion, ManaPotion}

class ActionTest extends FunSuite:

  test("Basic actions (Attack, Move) should initialise correctly with their names") {
    val attack = new Attack()
    val move = new Move()

    assertEquals(attack.name, "Attack")
    assertEquals(move.name, "Move")
  }

  test("EquipWeapon action should store and provide the list of eligible weapons") {
    val sword = new Sword("Buster Sword", attackPoints = 30, weight = 30)
    val wand = new Wand("Sanguine Rose", attackPoints = 5, magicAttackPoints = 20, weight = 3)
    val equip = new EquipWeapon(List(sword, wand))
    
    assertEquals(equip.name, "Equip Weapon")
    assertEquals(equip.usableItems.length, 2)
    assertEquals(equip.usableItems, List(sword, wand))
  }
  
  test("UsePotion action should store and provide the list of eligible potions") {
    val healing = new HealingPotion()
    val mana = new ManaPotion()
    val consume = new UsePotion(List(healing, mana))
    
    assertEquals(consume.name, "Use Potion")
    assertEquals(consume.usableItems.length, 2)
    assertEquals(consume.usableItems, List(healing, mana))
  }
  
  test("Spell actions (Thunder, Meteor, Heal, Purify) should initialise correctly") {
    val thunder = new Thunder()
    val meteor = new Meteor()
    val heal = new Heal()
    val purify = new Purify()

    assertEquals(thunder.name, "Thunder")
    assertEquals(meteor.name, "Meteor")
    assertEquals(heal.name, "Heal")
    assertEquals(purify.name, "Purify")
  }
