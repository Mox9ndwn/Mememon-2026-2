package cl.uchile.dcc
package actions

import items.{Usable, Weapon, Potion}
import units.GameUnit
import scala.collection.mutable.ListBuffer

/** Pure contract representing an in-game action */
trait Action:
  def name: String
  def execute(source: Source, target: Target): Unit

/** Trait for actions that involve a list of usable items */
trait UsableItemAction[T <: Usable] extends Action:
  def usableItems: List[T]

/** Abstract base class for all actions */
abstract class AbstractAction(val name: String) extends Action:
  override def execute(source: Source, target: Target): Unit = ()

/** Abstract base class for actions that interact with usable items */
abstract class AbstractUsableItemAction[T <: Usable](
                                                    name: String,
                                                    initialItems: List[T] = List()
                                                    ) extends AbstractAction(name) with UsableItemAction[T]:
  private val _usableItems: ListBuffer[T] = ListBuffer.from(initialItems)

  /** Returns an immutable list of usable items associated with this action */
  override def usableItems: List[T] = _usableItems.toList


// Basic Actions

class Attack(name: String = "Attack") extends AbstractAction(name):
  override def execute(source: Source, target: Target): Unit =
    val rawDamage = source match
      case unit: GameUnit => unit.attackDamage
      case _ => 0
    target.receiveAttack(rawDamage)

class Move(name: String = "Move") extends AbstractAction(name):
  override def execute(source: Source, target: Target): Unit =
    source match {
      case unit: GameUnit => target.receiveMove(unit)
      case _ => ()
    }


// Usable Item Actions

class EquipWeapon(initialWeapons: List[Weapon] = List(), name: String = "Equip Weapon") extends AbstractUsableItemAction[Weapon](name, initialWeapons)

class UsePotion(initialPotions: List[Potion] = List(), name: String = "Use Potion") extends AbstractUsableItemAction[Potion](name, initialPotions)


// Spell/Skill Actions

abstract class Spell(name: String) extends AbstractAction(name)

class Thunder(name: String = "Thunder") extends Spell(name)
class Meteor(name: String = "Meteor") extends Spell(name)
class Heal(name: String = "Heal") extends Spell(name)
class Purify(name: String = "Purify") extends Spell(name)