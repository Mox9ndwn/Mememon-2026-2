package cl.uchile.dcc
package player

import units.GameUnit
import scala.collection.mutable.ListBuffer

/** Represents a player in the game who manages a team of units */
class Player(val name: String, initialUnits: List[GameUnit] = List()):
  private val _units: ListBuffer[GameUnit] = ListBuffer.from(initialUnits)

  /** Returns an immutable list copy of the player's units */
  def units: List[GameUnit] = _units.toList

  /** Adds a unit to the player's team */
  def addUnit(unit: GameUnit): Unit = _units += unit

  /** A player is defeated if they have no units or all units are defeated */
  def isDefeated: Boolean = _units.isEmpty || _units.forall(_.isDefeated)


