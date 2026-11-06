package cl.uchile.dcc
package items

import units.Character

/** Base interface for any usable item */
trait Usable:
  def name: String

/** Interface for weapons */
trait Weapon extends Usable:
  def attackPoints: Int
  def weight: Int
  def owner: Option[Character]
  def setOwner(newOwner: Option[Character]): Unit
  def equipTo(character: Character): Unit

/** Interface for magic weapons */
trait MagicWeapon extends Weapon:
  def magicAttackPoints: Int

/** Interface for potions */
trait Potion extends Usable


// Abstract Weapon Classes

/** Abstract base class for common weapons */
abstract class AbstractWeapon(
                             val name: String,
                             val attackPoints: Int,
                             val weight: Int,
                             ) extends Weapon:

  private var _owner: Option[Character] = None

  override def owner: Option[Character] = _owner

  override def setOwner(newOwner: Option[Character]): Unit =
    _owner = newOwner
  
/** Abstract base class for magic weapons */
abstract class AbstractMagicWeapon(
                                  name: String,
                                  attackPoints: Int,
                                  val magicAttackPoints: Int,
                                  weight: Int,
                                  ) extends AbstractWeapon(name, attackPoints, weight) with MagicWeapon
  

// Concrete Weapon Classes

class Sword(name: String, attackPoints: Int, weight: Int) extends AbstractWeapon(name, attackPoints, weight):
  override def equipTo(character: Character): Unit = character.equipSword(this)
  
class Dagger(name: String, attackPoints: Int, weight: Int) extends AbstractWeapon(name, attackPoints, weight):
  override def equipTo(character: Character): Unit = character.equipDagger(this)
  
class Bow(name: String, attackPoints: Int, weight: Int) extends AbstractWeapon(name, attackPoints, weight):
  override def equipTo(character: Character): Unit = character.equipBow(this)
  
class Wand(name: String, attackPoints: Int, magicAttackPoints: Int, weight: Int) extends AbstractMagicWeapon(name, attackPoints, magicAttackPoints, weight):
  override def equipTo(character: Character): Unit = character.equipWand(this)

class Staff(name: String, attackPoints: Int, magicAttackPoints: Int, weight: Int) extends AbstractMagicWeapon(name, attackPoints, magicAttackPoints, weight):
  override def equipTo(character: Character): Unit = character.equipStaff(this)


// Potion Classes

abstract class AbstractPotion(val name: String) extends Potion

class HealingPotion(name: String = "Healing Potion") extends AbstractPotion(name)
class FortitudePotion(name: String = "Fortitude Potion") extends AbstractPotion(name)
class ManaPotion(name: String = "Mana Potion") extends AbstractPotion(name)
class MagicStrengthPotion(name: String = "Magic Strength Potion") extends AbstractPotion(name)