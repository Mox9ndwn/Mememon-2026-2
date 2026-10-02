package cl.uchile.dcc
package units

import scala.collection.mutable.ListBuffer
import items.{Usable, Weapon}

/** Represents a generic game unit */
trait GameUnit:
  def name: String
  def maxHp: Int
  def currentHp: Int
  def defense: Int
  def weight: Int
  def isDefeated: Boolean

/** Represents a controllable character in the game */
trait Character extends GameUnit:
  def weaponSlot: Option[Weapon]
  def inventory: List[Usable]

/** Represents a magic-user character */
trait MagicCharacter extends Character:
  def maxMp: Int
  def currentMp: Int

/** Abstract base class sharing common character state and behavior */
abstract class AbstractCharacter(
                                val name: String,
                                val maxHp: Int,
                                val defense: Int,
                                val weight: Int
                                ) extends Character:

  private var _currentHp: Int = maxHp
  override def currentHp: Int = _currentHp
  override def isDefeated: Boolean = _currentHp <= 0
  override def weaponSlot: Option[Weapon] = None
  override def inventory: List[Usable] = List()

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
           val name: String,
           val maxHp: Int,
           val attack: Int,
           val defense: Int,
           val weight: Int
           ) extends GameUnit:

  private var _currentHp: Int = maxHp

  override def currentHp: Int = _currentHp
  override def isDefeated: Boolean = _currentHp <= 0