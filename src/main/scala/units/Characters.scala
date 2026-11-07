package cl.uchile.dcc
package units

import actions.{Action, Source, Target}
import exceptions.{DefeatedUnitException, IncompatibleWeaponException, InsufficientManaException, InvalidTargetException}
import items.*

import scala.collection.mutable.ListBuffer


/** Represents a generic game unit capable of being both a source and target of actions*/
trait GameUnit extends Source with Target:
  def name: String
  def maxHp: Int
  def currentHp: Int
  def defense: Int
  def weight: Int
  def attackDamage: Int
  def isDefeated: Boolean
  def currentPanel: Option[map.GamePanel]
  def setPanel(panel: Option[map.GamePanel]): Unit

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

  override def doAction(action: Action, target: Target): Unit = {
    if isDefeated then
      throw new DefeatedUnitException(s"Unit $name cannot perform any action ${action.name} because it is defeated.")
    action.execute(this, target)
  }

  override def receiveAttack(incomingDamage: Int): Unit =
    if isDefeated then
      throw new InvalidTargetException(s"Unit $name cannot receive an attack because it is already defeated.")
    val effectiveDamage = math.max(0, incomingDamage - defense)
    currentHp = currentHp - effectiveDamage

  private var _currentPanel: Option[map.GamePanel] = None

  override def currentPanel: Option[map.GamePanel] = _currentPanel

  override def setPanel(panel: Option[map.GamePanel]): Unit =
    _currentPanel = panel

/** Represents a controllable character in the game */
trait Character extends GameUnit:
  def weaponSlot: Option[Weapon]
  def inventory: List[Usable]
  def equip(weapon: Weapon): Unit
  def equipSword(sword: items.Sword): Unit
  def equipDagger(dagger: items.Dagger): Unit
  def equipBow(bow: items.Bow): Unit
  def equipWand(wand: items.Wand): Unit
  def equipStaff(staff: items.Staff): Unit

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

  override def attackDamage: Int =
    _weaponSlot.map(_.attackPoints).getOrElse(0)

  // Helper method to assign the weapon once validated
  protected def setEquipped(weapon: Weapon): Unit = {
    _weaponSlot = Some(weapon)
    weapon.setOwner(Some(this))
  }

  // Double dispatch entry point
  override def equip(weapon: Weapon): Unit =
    weapon.equipTo(this)

  //Default behaviours: throw IncompatibleWeaponException unless overridden by allowed classes
  override def equipSword(sword: Sword): Unit =
    throw new IncompatibleWeaponException(s"$name cannot equip swords.")

  override def equipDagger(dagger: Dagger): Unit =
    throw new IncompatibleWeaponException(s"$name cannot equip daggers.")

  override def equipBow(bow: Bow): Unit =
    throw new IncompatibleWeaponException(s"$name cannot equip bows.")

  override def equipWand(wand: Wand): Unit =
    throw new IncompatibleWeaponException(s"$name cannot equip wands.")

  override def equipStaff(staff: Staff): Unit =
    throw new IncompatibleWeaponException(s"$name cannot equip staffs.")

  // Method to receive Heal
  override def receiveHeal(amount: Int): Unit =
    if isDefeated then
      throw new InvalidTargetException(s"$name cannot be healed because it is defeated.")
    currentHp = currentHp + amount


/** Represents a magic-user character */
trait MagicCharacter extends Character:
  def maxMp: Int
  def currentMp: Int
  def consumeMp(amount: Int): Unit

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

  override def consumeMp(amount: Int): Unit =
    if _currentMp < amount then
      throw new InsufficientManaException(s"$name has insufficient mana (required: $amount, current: ${_currentMp}")
    _currentMp -= amount


// Concrete Character Classes

/** Knight character class */
class Knight(name: String, maxHp: Int, defense: Int, weight: Int) extends AbstractCharacter(name, maxHp, defense, weight):
  override def equipSword(sword: items.Sword): Unit = setEquipped(sword)
  override def equipDagger(dagger: items.Dagger): Unit = setEquipped(dagger)
  override def equipBow(bow: items.Bow): Unit = setEquipped(bow)

/** Archer character class */
class Archer(name: String, maxHp: Int, defense: Int, weight: Int) extends AbstractCharacter(name, maxHp, defense, weight):
  override def equipSword(sword: items.Sword): Unit = setEquipped(sword)
  override def equipBow(bow: items.Bow): Unit = setEquipped(bow)
  override def equipWand(wand: items.Wand): Unit = setEquipped(wand)

/** Thief character class */
class Thief(name: String, maxHp: Int, defense: Int, weight: Int) extends AbstractCharacter(name, maxHp, defense, weight):
  override def equipSword(sword: items.Sword): Unit = setEquipped(sword)
  override def equipDagger(dagger: items.Dagger): Unit = setEquipped(dagger)

/** Black Mage character class */
class BlackMage(name: String, maxHp: Int, maxMp: Int, defense: Int, weight: Int) extends AbstractMagicCharacter(name, maxHp, maxMp, defense, weight):
  override def equipDagger(dagger: items.Dagger): Unit = setEquipped(dagger)
  override def equipWand(wand: items.Wand): Unit = setEquipped(wand)
  override def equipStaff(staff: items.Staff): Unit = setEquipped(staff)

/** White Mage character class */
class WhiteMage(name: String, maxHp: Int, maxMp: Int, defense: Int, weight: Int) extends AbstractMagicCharacter(name, maxHp, maxMp, defense, weight):
  override def equipBow(bow: items.Bow): Unit = setEquipped(bow)
  override def equipWand(wand: items.Wand): Unit = setEquipped(wand)
  override def equipStaff(staff: items.Staff): Unit = setEquipped(staff)

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

  // Method to receive Purify
  override def receivePurify(damage: Int): Unit =
    receiveAttack(damage)
