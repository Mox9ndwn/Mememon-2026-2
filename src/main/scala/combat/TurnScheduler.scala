package cl.uchile.dcc
package combat

import units.{Character, GameUnit}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/** Pure contract for managing turns and action bars in combat */
trait Scheduler:
  def units: List[GameUnit]
  def addUnit(unit: GameUnit): Unit
  def removeUnit(unit: GameUnit): Unit
  def maxActionBar(unit: GameUnit): Int
  def currentActionBar(unit: GameUnit): Int
  def resetActionBar(unit: GameUnit): Unit
  def increaseAllActionBars(amount: Int): Unit
  def hasCompletedActionBar(unit: GameUnit): Boolean
  def completedUnitsOrderedBySurplus: List[GameUnit]
  def currentTurnUnit: Option[GameUnit]

/** Concrete implementation of the turn scheduler */
class TurnScheduler extends Scheduler:

  private val _units: ListBuffer[GameUnit] = ListBuffer()
  private val _actionBars: mutable.Map[GameUnit, Int] = mutable.Map()

  override def units: List[GameUnit] = _units.toList

  override def addUnit(unit: GameUnit): Unit =
    if !_units.contains(unit) then {
      _units += unit
      _actionBars(unit) = 0
    }

  override def removeUnit(unit: GameUnit): Unit = {
    _units -= unit
    _actionBars.remove(unit)
  }

  /** Calculates the maximum action bar capacity based on unit weight and equipped weapon */
  override def maxActionBar(unit: GameUnit): Int =
    unit match
      case c: Character =>
        val weaponWeight = c.weaponSlot.map(_.weight).getOrElse(0)
        c.weight + weaponWeight
      case other => other.weight

  override def currentActionBar(unit: GameUnit): Int =
    _actionBars.getOrElse(unit, 0)

  override def resetActionBar(unit: GameUnit): Unit =
    if _units.contains(unit) then _actionBars(unit) = 0

  /** Simultaneously increases the action bar of all participating units by the given amount */
  override def increaseAllActionBars(amount: Int): Unit =
    if amount > 0 then
      for unit <- _units do
        val current = _actionBars.getOrElse(unit, 0)
        _actionBars(unit) = current + amount

  override def hasCompletedActionBar(unit: GameUnit): Boolean =
    currentActionBar(unit) >= maxActionBar(unit)

  /** Returns all units that have completed their action bar, ordered descending by surplus */
  override def completedUnitsOrderedBySurplus: List[GameUnit] =
    _units
      .filter(hasCompletedActionBar)
      .sortBy(unit => -(currentActionBar(unit) - maxActionBar(unit)))
      .toList

  /** Returns the single unit currently entitled to take its turn (the one with the greatest surplus) */
  override def currentTurnUnit: Option[GameUnit] =
    completedUnitsOrderedBySurplus.headOption