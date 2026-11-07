package cl.uchile.dcc
package map

import actions.Target
import units.GameUnit
import scala.collection.mutable.ListBuffer
import exceptions.InvalidMovementException

/** Pure contract representing a game tile/panel on the map */
trait GamePanel extends Target:
  def x: Int
  def y: Int
  def units: List[GameUnit]
  def neighbors: List[GamePanel]
  def addUnit(unit: GameUnit): Unit
  def removeUnit(unit: GameUnit): Unit
  def addNeighbor(neighbor: GamePanel): Unit
  def receiveMove(unit: GameUnit): Unit

/** Concrete implementation of a map panel */
class Panel(val x: Int, val y: Int) extends GamePanel:

  private val _units: ListBuffer[GameUnit] = ListBuffer()
  private val _neighbors: ListBuffer[GamePanel] = ListBuffer()
  
  /** Returns an immutable list of units currently on this panel */
  override def units: List[GameUnit] = _units.toList
  
  /** Returns an immutable list of neighboring panels */
  override def neighbors: List[GamePanel] = _neighbors.toList
  
  /** Adds a unit to this panel */
  override def addUnit(unit: GameUnit): Unit =
    if !_units.contains(unit) then {
      _units += unit
      unit.setPanel(Some(this))
    }

  /** Removes a unit from this panel */
  override def removeUnit(unit: GameUnit): Unit =
    if _units.contains(unit) then {
      _units -= unit
      if unit.currentPanel.contains(this) then
        unit.setPanel(None)
    }

  /** Links a neighboring panel to this panel */
  override def addNeighbor(neighbor: GamePanel): Unit =
    if neighbor != this && !_neighbors.contains(neighbor) then
      _neighbors += neighbor

  /** Handles receiving a unit onto this panel as the target of a Move action */
  override def receiveMove(unit: GameUnit): Unit =
    unit.currentPanel match {
      case Some(panel) =>
        if !panel.neighbors.contains(this) then
          throw new InvalidMovementException(s"Cannot move unit ${unit.name} to panel ($x, $y) because it is not an adjacent neighbor.")
        panel.removeUnit(unit)
        addUnit(unit)
      case None =>
        addUnit(unit)
    }
