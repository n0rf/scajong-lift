package code
package comet

import net.liftweb._
import http._
import actor._
import scajong.model.Tile
import scajong.model.Setup
import scajong.model.ScoreEntry
import scajong.Scajong
import main.scala.code.lib._
import scajong.view.View
import scajong.util.SimpleNotification

/**
 * A singleton that provides communication features to all clients.
 * It's an Actor so it's thread-safe because only one
 * message will be processed at once.
 */
object LiftViewServer extends LiftActor with ListenerManager with View {

  private var update : Any = null
  
  Scajong.controller.attachView(this)
  override def autoClose = true
  
  /**
   * When we update the listeners, what message do we send?
   * We send the msgs, which is an immutable data structure,
   * so it can be shared with lots of threads without any
   * danger or locking.
   */
  def createUpdate = update
  
  
  override def processNotification(sn:SimpleNotification) {
    println("processNotifications: " + sn)
    doUpdate(sn)
  }
  
  override def lowPriority = {
    // internal notifications:
    case s: RequestHintNotification => {
      println("ScajongServer: lowPriority RequestHintNotification: " + s)
      Scajong.controller.requestHint
    }
    case s: RequestMoveablesNotification => {
      println("ScajongServer: lowPriority RequestMoveablesNotification: " + s)
      Scajong.controller.requestMoveables
    }
    case s: SimpleNotification => {
      println("---------- ScajongServer: lowPriority: " + s)
      doUpdate(s)
    }
  }
  
  private def doUpdate(updateInfo:Any) = {
    update = updateInfo
    updateListeners
  }
}
