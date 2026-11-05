package cl.uchile.dcc
package units

import scala.collection.mutable.ListBuffer
import items.{Usable, Weapon}
import actions.{Action, Source, Target}


/** Represents a generic game unit capable of being both a source and target of actions*/
trait GameUnit extends Source with Target:
  def name: String
  def maxHp: Int
  def currentHp: Int
  def defense: Int
  def weight: Int
  def attackDamage: Int
  def isDefeated: Boolean

/** Abstract base class sharing common unit state and behaviours */
abstract class AbstractGameUnit(
                               val name: String,
                               val maxHp: Int,
                               val defense: Int,
                               val weight: Int
                               ) extends GameUnit:
  private var _currentHp: Int = maxHp
  private val _actions: ListBuffer[Action] = ListBuffer()

  override def currentHp: Int = _currentHp

  protected def currentHp_=(newHp: Int): Unit =
    _currentHp = math.max(0, math.min(newHp, maxHp))

  override def isDefeated: Boolean = _currentHp <= 0

  override def actions: List[Action] = _actions.toList

  override def addAction(action: Action): Unit =
    if !_actions.contains(action) then _actions += action

  override def findAction(actionName: String): Option[Action] =
    _actions.find(_.name == actionName)

  override def doAction(action: Action, target: Target): Unit =
    action.execute(this, target)

  override def receiveAttack(incomingDamage: Int): Unit =
    val effectiveDamage = math.max(0, incomingDamage - defense)
    currentHp = currentHp - effectiveDamage

/** Represents a controllable character in the game */
trait Character extends GameUnit:
  def weaponSlot: Option[Weapon]
  def inventory: List[Usable]
  def equip(weapon: Weapon): Unit

/** Abstract base class sharing common character state and behaviour */
abstract class AbstractCharacter(
                                name: String,
                                maxHp: Int,
                                defense: Int,
                                weight: Int
                                ) extends AbstractGameUnit(name, maxHp, defense, weight) with Character:

  private var _weaponSlot: Option[Weapon] = None
  private var _inventory: List[Usable] = List()

  override def weaponSlot: Option[Weapon] = _weaponSlot

  override def inventory: List[Usable] = _inventory

  override def equip(weapon: Weapon): Unit = {
    _weaponSlot = Some(weapon)
    weapon.setOwner(Some(this))
  }

  override def attackDamage: Int =
    _weaponSlot.map(_.attackPoints).getOrElse(0)


/** Represents a magic-user character */
trait MagicCharacter extends Character:
  def maxMp: Int
  def currentMp: Int

/** Abstract base class for magic-user characters */
abstract class AbstractMagicCharacter(
                                     name: String,
                                     maxHp: Int,
                                     val maxMp: Int,
                                     defense: Int,
                                     weight: Int
                                     ) extends AbstractCharacter(name, maxHp, defense, weight) with MagicCharacter:

  private var _currentMp: Int = maxMp
  override def currentMp: Int = _currentMp
  protected def currentMp_=(newMp: Int): Unit =
    _currentMp = math.max(0, math.min(newMp, maxMp))


// Concrete Character Classes

/** Knight character class */
class Knight(name: String, maxHp: Int, defense: Int, weight: Int) extends AbstractCharacter(name, maxHp, defense, weight)

/** Archer character class */
class Archer(name: String, maxHp: Int, defense: Int, weight: Int) extends AbstractCharacter(name, maxHp, defense, weight)

/** Thief character class */
class Thief(name: String, maxHp: Int, defense: Int, weight: Int) extends AbstractCharacter(name, maxHp, defense, weight)

/** Black Mage character class */
class BlackMage(name: String, maxHp: Int, maxMp: Int, defense: Int, weight: Int) extends AbstractMagicCharacter(name, maxHp, maxMp, defense, weight)

/** White Mage character class */
class WhiteMage(name: String, maxHp: Int, maxMp: Int, defense: Int, weight: Int) extends AbstractMagicCharacter(name, maxHp, maxMp, defense, weight)

// Enemy Unit Class

/** Represents an enemy unit in the game */
class Enemy(
            name: String,
            maxHp: Int,
            val attack: Int,
            defense: Int,
            weight: Int
           ) extends AbstractGameUnit(name, maxHp, defense, weight):

  override def attackDamage: Int = attack
