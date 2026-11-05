package cl.uchile.dcc
package actions

import munit.FunSuite
import units.{Knight, Enemy}
import items.Sword


class InteractionTest extends FunSuite:

  test("Source should store available actions and find action by name") {
    val attack = new Attack()
    val knight = new Knight("Cecil", maxHp = 100, defense = 10, weight = 20)
    knight.addAction(attack)

    assertEquals(knight.actions.length, 1)
    assertEquals(knight.findAction("Attack"), Some(attack))
    assertEquals(knight.findAction("NonExistent"), None)
  }

  test("An Enemy attacking a Character should reduce the target's HP using its attack points") {
    val goblin = new Enemy("Goblin", maxHp = 50, attack = 25, defense = 5, weight = 10)
    val knight = new Knight("Cecil", maxHp = 100, defense = 10, weight = 20)
    val attack = new Attack()

    goblin.addAction(attack)
    // Goblin has 25 attack; and Knight has 10 defense -> damage is 15 -> Knight's HP goes from 100 to 85
    goblin.doAction(attack, knight)

    assertEquals(knight.currentHp, 85)
  }

  test("A Character with an equipped weapon attacking an Enemy should deal damage") {
    val knight = new Knight("Cloud", maxHp = 100, defense = 10, weight = 20)
    val sword = new Sword("Buster Sword", attackPoints = 35, weight = 12)
    knight.equip(sword)

    val goblin = new Enemy("Cactuar", maxHp = 50, attack = 20, defense = 5, weight = 10)
    val attack = new Attack()

    knight.addAction(attack)
    // Knight has 35 attack (sword); and Goblin has 5 defense -> damage is 30 -> Goblin HP goes from 50 to 20
    knight.doAction(attack, goblin)

    assertEquals(goblin.currentHp, 20)
  }

  test("Attack damage should not be negative when defense exceeds attack") {
    val weakGoblin = new Enemy("Palmer", maxHp = 30, attack = 5, defense = 2, weight = 5)
    val toughKnight = new Knight("Thunder God Cid", maxHp = 100, defense = 20, weight = 25)
    val attack = new Attack()

    weakGoblin.addAction(attack)
    weakGoblin.doAction(attack, toughKnight)

    // Damage = max(0, 5 - 20) = 0 -> Knight takes 0 damage
    assertEquals(toughKnight.currentHp, 100)
  }

  test("A GamePanel should act as a Target for the Move action") {
    import map.Panel
    val knight = new Knight("Cecil", maxHp = 100, defense = 10, weight = 20)
    val targetPanel = new Panel(2, 3)
    val move = new Move()

    knight.addAction(move)
    knight.doAction(move, targetPanel)

    // The panel should now contain the knight
    assertEquals(targetPanel.units.length, 1)
    assertEquals(targetPanel.units.head, knight)
  }

  test("A defeated unit should not be able to perform any action") {
    import exceptions.DefeatedUnitException
    val knight = new Knight("Fallen Cecil", maxHp = 100, defense = 10, weight = 20)
    val goblin = new Enemy("Goblin", maxHp = 50, attack = 10, defense = 5, weight = 10)
    val attack = new Attack()
    knight.addAction(attack)

    // Receive lethal damage
    knight.receiveAttack(200)
    assert(knight.isDefeated)

    // Attempting to act must throw DefeatedUnitException
    intercept[DefeatedUnitException] {
      knight.doAction(attack, goblin)
    }
  }

  test("A defeated unit should mot be able to receive any action") {
    import exceptions.InvalidTargetException
    val knight = new Knight("King Arthur", maxHp = 100, defense = 10, weight = 20)
    val sword = new items.Sword("Excalibur", attackPoints = 30, weight = 10)
    knight.equip(sword)
    val attack = new Attack()
    knight.addAction(attack)

    val fallenGoblin = new Enemy("Gobbledygook", maxHp = 30, attack = 10, defense = 2, weight = 10)
    fallenGoblin.receiveAttack(100)
    assert(fallenGoblin.isDefeated)

    // Attacking a defended target must throw InvalidTargetException
    intercept[InvalidTargetException] {
      knight.doAction(attack, fallenGoblin)
    }
  }