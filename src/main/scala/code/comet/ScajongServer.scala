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
import scajong.model.ScoreEntry
import scajong.Scajong
import scajong.view.AddNewScoreEntryNotification
import scajong.view.RequestMoveablesNotification
import scajong.view.RequestHintNotification

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
  
  override def lowPriority = {
    case SetupSelectedNotification(setup) => {
      println("ScajongServer: lowPriority SetupSelectedNotification: " + setup)
      doUpdate(setup)
    }
    case s: Tile => {
      println("ScajongServer: lowPriority Tile selection: " + s)
      doUpdate(s)
    }
    case s: ShowCreateGameMenuNotification => {
      println("ScajongServer: lowPriority ShowCreateGameMenuNotification")
      doUpdate(s)
    }
    case s: ShowScoresMenuNotification => {
      println("ScajongServer: lowPriority ShowScoresMenuNotification")
      doUpdate(s)
    }
    case s: ShowScoresNotification => {
      println("ScajongServer: lowPriority ShowScoresNotification: " + s.setup)
      doUpdate(s)
    }
    case s: AddNewScoreEntryNotification => {
      println("ScajongServer: lowPriority Score Entry add: " + s)
      doUpdate(s)
    }
    case s: RequestMoveablesNotification => {
      println("ScajongServer: lowPriority RequestMoveablesNotification: " + s)
      doUpdate(s)
    }
    case s: RequestHintNotification => {
      println("ScajongServer: lowPriority RequestHintNotification: " + s)
      doUpdate(s)
    }
  }
  
  private def doUpdate(updateInfo:Any) = {
    update = updateInfo
    updateListeners
  }
}
