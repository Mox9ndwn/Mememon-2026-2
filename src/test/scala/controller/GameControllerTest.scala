package cl.uchile.dcc
package controller

import actions.Attack
import items.Sword
import units.{Enemy, Knight}

import munit.FunSuite

class GameControllerTest extends FunSuite:
  
  test("GameCharacter should track added units and report available actions") {
    val controller = new GameController()
    val knight = new Knight("Clive", maxHp = 100, defense = 10, weight = 20)
    val attack = new Attack()
    knight.addAction(attack)
    
    controller.addUnit(knight)
    
    assertEquals(controller.units.length, 1)
    assertEquals(controller.availableActions(knight), List(attack))
  }
  
  test("GameController should determinate current turn and advance to next turn") {
    val controller = new GameController()
    val fastEnemy = new Enemy("Celia", maxHp = 30, attack = 10, defense = 2, weight = 10)
    val slowKnight = new Knight("Ramza", maxHp = 100, defense = 10, weight = 15)
    
    controller.addUnit(fastEnemy)
    controller.addUnit(slowKnight)
    
    // Initially no one has completed the action bar
    assertEquals(controller.currentTurnUnit, None)
    
    // Next turn increases action bars unit a unit is ready
    controller.nextTurn()
    assertEquals(controller.currentTurnUnit, Some(fastEnemy))
    
    // Advancing next turn resets the current unit's action bar and finds the next
    controller.nextTurn()
    assertEquals(controller.currentTurnUnit, Some(slowKnight))
  }

  test("GameController should perform an action from a source unit to a target") {
    val controller = new GameController()
    val knight = new Knight("Sephiroth", maxHp = 100, defense = 10, weight = 20)
    val sword = new Sword("Masamune", attackPoints = 30, weight = 10)
    knight.equip(sword)
    
    val goblin = new Enemy("Goblin", maxHp = 50, attack = 10, defense = 5, weight = 10)
    val attack = new Attack()
    knight.addAction(attack)
    
    controller.addUnit(knight)
    controller.addUnit(goblin)
    
    controller.performAction(knight, attack, goblin)
    // Damage = 30 - 5 = 25 -> Goblin HP: 50 - 25 = 25
    assertEquals(goblin.currentHp, 25)
  }
  
  test("GameController resetGame should restore initial state") {
    val controller = new GameController()
    val knight = new Knight("Adelbert", maxHp = 100, defense = 10, weight = 20)
    
    controller.addUnit(knight)
    controller.nextTurn()
    assert(controller.units.nonEmpty)
    
    controller.resetGame()
    assertEquals(controller.units, List())
    assertEquals(controller.currentTurnUnit, None)
  }

  test("GameController performAction should gracefully catch GameRuleException and report failure") {
    val controller = new GameController()
    val unarmedKnight = new Knight("Basch", maxHp = 100, defense = 10, weight = 20)
    val goblin = new Enemy("Goblin", maxHp = 50, attack = 10, defense = 5, weight = 10)
    val attack = new actions.Attack()
    unarmedKnight.addAction(attack)

    controller.addUnit(unarmedKnight)
    controller.addUnit(goblin)

    // Unarmed attack throws MissingWeaponException, but performAction should catch it
    val success = controller.performAction(unarmedKnight, attack, goblin)
    assertEquals(success, false)
    assert(controller.lastErrorMessage.isDefined)
  }
