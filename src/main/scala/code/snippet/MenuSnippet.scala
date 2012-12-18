package code
package snippet

import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd.unitToJsCmd
import net.liftweb.util.Helpers.strToCssBindPromoter
import scajong.Scajong
import scajong.view.View
import code.comet.TilesRenderer
import code.comet.LiftViewServer
import net.liftweb.http.js.JsCmds.SetValById
import scajong.model.ScoreEntry
import main.scala.code.lib.ShowScoresMenuNotification
import main.scala.code.lib.RequestMoveablesNotification
import main.scala.code.lib.RequestHintNotification
import main.scala.code.lib.AddNewScoreEntryNotification
import scajong.util.SimpleNotification
import main.scala.code.lib.ShowCreateGameMenuNotification
import main.scala.code.lib.BackToGameNotification

/**
 * Snippet class for menu actions.
 */
object MenuSnippet {
  
  var showBackToGameButton : Boolean = false
  
  /**
   * CreateGame snippet renders create game button with ajax function. 
   */
  def createGame = "#createButton [onclick]" #> SHtml.ajaxInvoke(() => {
    if (Scajong.controller.tiles.size != 0) {
    	showBackToGameButton = true
    }
    LiftViewServer ! new ShowCreateGameMenuNotification
  })

  /**
   * ShowScoresMenu snippet renders button with ajax function to show scores menu.
   */
  def showScoresMenu = "#showScoresMenuButton [onclick]" #> SHtml.ajaxInvoke(() => {
    if (Scajong.controller.tiles.size != 0) {
    	showBackToGameButton = true
    }
    LiftViewServer ! new ShowScoresMenuNotification
  })

  /**
   * ShowMoveables snippet renders button with ajax function to show moveable tiles. 
   */
  def showMoveables = "#moveablesButton [onclick]" #> SHtml.ajaxInvoke(() => {
    if (!showBackToGameButton && Scajong.controller.tiles.size != 0) {
    	LiftViewServer ! new RequestMoveablesNotification
    }
  })

  /**
   * ShowHint snippet renders button with ajax function to show hint tiles. 
   */
  def showHint = "#hintButton [onclick]" #> SHtml.ajaxInvoke(() => {
    if (!showBackToGameButton && Scajong.controller.tiles.size != 0) {
    	LiftViewServer ! new RequestHintNotification
    }
  })

  /**
   * AddScoreEntry snippet for add a new score entry form.
   */
  def addScoreEntry = SHtml.onSubmit(name => {
    LiftViewServer ! new AddNewScoreEntryNotification(name)
    SetValById("entry_name", "")
  })
  
  /**
   * BackToGame snippet for add a new score entry form.
   */
  def backToGame = "#backToGameButton [onclick]" #> SHtml.ajaxInvoke(() => {
    showBackToGameButton = false
    LiftViewServer ! new BackToGameNotification
  })
}