package cl.uchile.dcc
package combat

import munit.FunSuite
import units.{Knight, Enemy}
import items.Sword

class TurnSchedulerTest extends FunSuite:

  test("TurnScheduler should add and remove units") {
    val scheduler = new TurnScheduler()
    val knight = new Knight("Luso", maxHp = 100, defense = 15, weight = 20)
    
    scheduler.addUnit(knight)
    assertEquals(scheduler.units.length, 1)
    assert(scheduler.units.contains(knight))
  }
  
  test("TurnScheduler should calculate the action bar maximum based on weight") {
    val scheduler = new TurnScheduler()
    val knight = new Knight("Luso", maxHp = 100, defense = 15, weight = 20)
    val sword = new Sword("Judge Sword", attackPoints = 30, weight = 10)
    
    // No weapon equipped: base weight 20
    assertEquals(scheduler.maxActionBar(knight), 20)
    
    // Enemy with no weapon slot: its own weight
    val goblin = new Enemy("Goblin", maxHp = 30, attack = 10, defense = 2, weight = 15)
    assertEquals(scheduler.maxActionBar(goblin), 15)
  }
  
  test("TurnScheduler should increase action bars and detect completion") {
    val scheduler = new TurnScheduler()
    val slowUnit = new Knight("Slow Knight", maxHp = 100, defense = 15, weight = 30)
    val fastUnit = new Enemy("Fast Goblin", maxHp = 30, attack = 10, defense = 2, weight = 10)
    
    scheduler.addUnit(slowUnit)
    scheduler.addUnit(fastUnit)
    
    // Both start at 0
    assertEquals(scheduler.currentActionBar(slowUnit), 0)
    assertEquals(scheduler.currentActionBar(fastUnit), 0)
    
    // Increasing by 15 simultaneously
    scheduler.increaseAllActionBars(15)

    assertEquals(scheduler.currentActionBar(slowUnit), 15)
    assertEquals(scheduler.currentActionBar(fastUnit), 15)
    
    // The goblin filled its bar (15 >= 10), the knight did not (15 < 30)
    assert(!scheduler.hasCompletedActionBar(slowUnit))
    assert(scheduler.hasCompletedActionBar(fastUnit))
  }
  
  test("TurnScheduler should order completed units by surplus and determine the current turn") {
    val scheduler = new TurnScheduler()
    val unitA = new Enemy("Goblin A", maxHp = 30, attack = 10, defense = 2, weight = 10)
    val unitB = new Enemy("Goblin B", maxHp = 30, attack = 10, defense = 2, weight = 15)
    
    scheduler.addUnit(unitA)
    scheduler.addUnit(unitB)
    
    // Everyone increases by 20:
    // unitA: bar = 20, max = 10 -> surplus = +10
    // unitB: bar = 20, max = 15 -> surplus = +5
    scheduler.increaseAllActionBars(20)
    
    val readyUnits = scheduler.completedUnitsOrderedBySurplus
    assertEquals(readyUnits, List(unitA, unitB))
    
    // It is unitA's turn, as it has the largest surplus
    assertEquals(scheduler.currentTurnUnit, Some(unitA))
    
    // unitA's bar is reset after its turn is taken
    scheduler.resetActionBar(unitA)
    assertEquals(scheduler.currentActionBar(unitA), 0)
    
    // Now it is unitB's turn
    assertEquals(scheduler.currentTurnUnit, Some(unitB))
  }
  
  
