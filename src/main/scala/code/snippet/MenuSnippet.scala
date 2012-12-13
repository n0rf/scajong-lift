package code
package snippet

import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd.unitToJsCmd
import net.liftweb.util.Helpers.strToCssBindPromoter
import scajong.Scajong
import scajong.model.CreatedGameNotification
import scajong.view.View
import code.comet.TilesRenderer
import code.comet.TilesRenderer
import code.comet.ScajongServer
import scajong.view.HintNotification
import scajong.view.MoveablesNotification

class MenuSnippet extends View {

  Scajong.controller.attachView(this)

  override def autoClose = true
  
  def createGame = "#createButton [onclick]" #> SHtml.ajaxInvoke(() => {
    println("createGame")
    ScajongServer.showCreateGameMenu
  })
  
  def showScoresMenu = "#showScoresMenuButton [onclick]" #> SHtml.ajaxInvoke(() => {
    println("showScoresMenu")
    ScajongServer.showScoresMenu
  })
  
  def showMoveables = "#moveablesButton [onclick]" #> SHtml.ajaxInvoke(() => {
    sendNotification(new MoveablesNotification)
  })
  
  def showHint = "#hintButton [onclick]" #> SHtml.ajaxInvoke(() => {
    sendNotification(new HintNotification)
  })
}