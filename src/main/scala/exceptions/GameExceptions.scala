package cl.uchile.dcc
package exceptions

/** Base exception for all domain-specific game rule violations */
class GameRuleException(message: String) extends Exception(message)

/** Thrown when a unit attempts an action while defeated (HP <= 0) */
class DefeatedUnitException(message: String) extends GameRuleException(message)

/** Thrown when a target cannot receive an action because it is defeated or invalid */
class InvalidTargetException(message: String) extends GameRuleException(message)

/** Thrown when a character attempts to equip an incompatible weapon */
class IncompatibleWeaponException(message: String) extends GameRuleException(message)

/** Thrown when a character attempts to perform an action without meeting weapon requirements */
class MissingWeaponException(message: String) extends GameRuleException(message)

/** Thrown when a magic character does not possess enough mana to cast a spell */
class InsufficientManaException(message: String) extends GameRuleException(message)

/** Thrown when a unit attempts to move to a panel that is not a neighbour */
class InvalidMovementException(message: String) extends GameRuleException(message)
