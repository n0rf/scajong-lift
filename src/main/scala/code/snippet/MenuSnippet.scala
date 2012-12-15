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
import net.liftweb.http.js.JsCmds.SetValById
import scajong.model.ScoreEntry
import main.scala.code.lib.ShowScoresMenuNotification
import main.scala.code.lib.RequestMoveablesNotification
import main.scala.code.lib.RequestHintNotification
import main.scala.code.lib.AddNewScoreEntryNotification
import scajong.util.SimpleNotification
import main.scala.code.lib.ShowCreateGameMenuNotification

class MenuSnippet {
  
  def createGame = "#createButton [onclick]" #> SHtml.ajaxInvoke(() => {
    ScajongServer ! new ShowCreateGameMenuNotification
  })

  def showScoresMenu = "#showScoresMenuButton [onclick]" #> SHtml.ajaxInvoke(() => {
    ScajongServer ! new ShowScoresMenuNotification
  })

  def showMoveables = "#moveablesButton [onclick]" #> SHtml.ajaxInvoke(() => {
    ScajongServer ! new RequestMoveablesNotification
  })

  def showHint = "#hintButton [onclick]" #> SHtml.ajaxInvoke(() => {
    ScajongServer ! new RequestHintNotification
  })

  def addScoreEntry = SHtml.onSubmit(name => {
    ScajongServer ! new AddNewScoreEntryNotification(name)
    SetValById("entry_name", "")
  })
}