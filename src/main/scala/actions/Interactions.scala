package cl.uchile.dcc
package actions

import units.GameUnit

/** Represents an entity capable of initiating and executing actions */
trait Source:
  def name: String
  def actions: List[Action]
  def addAction(action: Action): Unit
  def findAction(actionName: String): Option[Action]
  def doAction(action: Action, target: Target): Unit

/** Represents an entity of receiving actions effects */
trait Target:
  def receiveAttack(damage: Int): Unit = ()
  def receiveMove(unit: GameUnit): Unit = ()