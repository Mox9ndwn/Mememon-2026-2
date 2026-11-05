package cl.uchile.dcc
package map

import actions.Target
import units.GameUnit
import scala.collection.mutable.ListBuffer

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
    if !_units.contains(unit) then _units += unit
    
  /** Removes a unit from this panel */
  override def removeUnit(unit: GameUnit): Unit =
    _units -= unit
    
  /** Links a neighboring panel to this panel */
  override def addNeighbor(neighbor: GamePanel): Unit =
    if neighbor != this && !_neighbors.contains(neighbor) then _neighbors += neighbor

  /** Handles receiving a unit onto this panel as the target of a Move action */
  override def receiveMove(unit: GameUnit): Unit =
    addUnit(unit)
