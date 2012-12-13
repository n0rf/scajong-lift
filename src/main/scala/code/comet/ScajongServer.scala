package code
package comet

import net.liftweb._
import http._
import actor._
import scajong.model.Tile
import scajong.view.SetupSelectedNotification
import scajong.model.Setup
import scajong.view.ShowCreateGameMenuNotification
import scajong.view.ShowScoresNotification
import scajong.view.ShowScoresMenuNotification

/**
 * A singleton that provides communication features to all clients.
 * It's an Actor so it's thread-safe because only one
 * message will be processed at once.
 */
object ScajongServer extends LiftActor with ListenerManager {

  private var update : Any = null

  /**
   * When we update the listeners, what message do we send?
   * We send the msgs, which is an immutable data structure,
   * so it can be shared with lots of threads without any
   * danger or locking.
   */
  def createUpdate = update
  
  def showCreateGameMenu = {
    println("showCreateGameMenu")
    update = new ShowCreateGameMenuNotification
    updateListeners
  }
  
  def showScoresMenu = {
    println("update showScoresMenu")
    update = new ShowScoresMenuNotification
    updateListeners
  }
  
  def showScores(setup:Setup) = {
    println("showScores")
    update = new ShowScoresNotification(setup)
    updateListeners
  }
  
  override def lowPriority = {
    case s: SetupSelectedNotification => {
      println("ScajongServer: lowPriority SetupSelectedNotification: " + s.setup)
      //setup = s.setup
      update = s.setup
      updateListeners
    }
    case s: Tile => {
      println("ScajongServer: lowPriority Tile selection: " + s)
      //tile = s
      update = s
      updateListeners
    }
  }
}
