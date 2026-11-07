package cl.uchile.dcc
package controller

import actions.{Action, Target}
import combat.TurnScheduler
import exceptions.GameRuleException
import units.GameUnit


/** Manages the overarching game flow, turn scheduling and action delegation */
class GameController:

  private val scheduler = new TurnScheduler()
  private var _lastErrorMessage: Option[String] = None

  def lastErrorMessage: Option[String] = _lastErrorMessage

  /** Returns an immutable list of units currently in the game */
  def units: List[GameUnit] = scheduler.units

  /** Adds a unit to the game and scheduler */
  def addUnit(unit: GameUnit): Unit =
    scheduler.addUnit(unit)

  /** Removes a unit from the game and scheduler */
  def removeUnit(unit: GameUnit): Unit =
    scheduler.removeUnit(unit)

  /** Returns the unit currently taking its turn, if any */
  def currentTurnUnit: Option[GameUnit] =
    scheduler.currentTurnUnit

  /** Advances the game to the next turn by resetting the current unit and progressing action bars */
  def nextTurn(): Option[GameUnit] = {
    currentTurnUnit.foreach(scheduler.resetActionBar)

    // Increment action bars until at least one unit is ready (simulating action loop)
    while scheduler.currentTurnUnit.isEmpty && units.nonEmpty do
      scheduler.increaseAllActionBars(1)

    scheduler.currentTurnUnit
  }

  /** Returns all available actions for a specific unit */
  def availableActions(unit: GameUnit): List[Action] =
    unit.actions

  /** Executes an action performed by a source until targeting an entity, safely handling rule violations */
  def performAction(source: GameUnit, action: Action, target: Target): Boolean =
    try {
      source.doAction(action, target)
      _lastErrorMessage = None
      true
    }
    catch
      case ex: GameRuleException =>
        _lastErrorMessage = Some(ex.getMessage)
        false

  /** Resets the game to its initial empty state */
  def resetGame(): Unit =
    _lastErrorMessage = None
    for u <- scheduler.units do
      scheduler.removeUnit(u)