package cl.uchile.dcc
package actions

import exceptions.MissingWeaponException
import items.{Potion, Usable, Weapon}
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
    source match {
      case c: units.Character if c.weaponSlot.isEmpty =>
        throw new MissingWeaponException(s"Character ${c.name} cannot attack without an equipped weapon")
      case unit: units.GameUnit =>
        target.receiveAttack(unit.attackDamage)
    }

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

abstract class Spell(name: String, val manaCost: Int = 10) extends AbstractAction(name):
  override def execute(source: Source, target: Target): Unit =
    source match {
      case caster: units.MagicCharacter =>
        caster.weaponSlot match {
          case Some(_: items.MagicWeapon) =>
            caster.consumeMp(manaCost)
            castSpell(caster, target)
          case _ =>
            throw new exceptions.MissingWeaponException(s"${caster.name} must have a MagicWeapon equipped to cast spells.")
        }
      case other =>
        throw new exceptions.MissingWeaponException(s"Source ${other.name} cannot cast spells.")
    }

  protected def castSpell(caster: units.MagicCharacter, target: Target): Unit

class Thunder(manaCost: Int = 15, name: String = "Thunder") extends Spell(name, manaCost):
  override protected def castSpell(caster: units.MagicCharacter, target: Target): Unit =
    val spellPower = caster.weaponSlot.collect {case mw: items.MagicWeapon => mw.magicAttackPoints}.getOrElse(25)
    target.receiveAttack(spellPower)

class Meteor(manaCost: Int = 25, name: String = "Meteor") extends Spell(name, manaCost):
  override protected def castSpell(caster: units.MagicCharacter, target: Target): Unit =
    val spellPower = caster.weaponSlot.collect {case mw: items.MagicWeapon => mw.magicAttackPoints}.getOrElse(35)
    target.receiveAttack(spellPower)

class Heal(manaCost: Int = 10, name: String = "Heal") extends Spell(name, manaCost):
  override protected def castSpell(caster: units.MagicCharacter, target: Target): Unit =
    val healPower = caster.weaponSlot.collect {case mw: items.MagicWeapon => mw.magicAttackPoints}.getOrElse(15)
    target.receiveHeal(healPower)

class Purify(manaCost: Int = 10,name: String = "Purify") extends Spell(name, manaCost):
  override protected def castSpell(caster: units.MagicCharacter, target: Target): Unit =
    val spellPower = caster.weaponSlot.collect {case mw: items.MagicWeapon => mw.magicAttackPoints}.getOrElse(20)
    target.receivePurify(spellPower)