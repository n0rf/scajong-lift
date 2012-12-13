package code
package snippet

import scala.collection.immutable.List

import code.comet.ScajongServer
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmd.unitToJsCmd
import net.liftweb.http.js.JsExp.strToJsExp
import net.liftweb.util.Helpers.strToCssBindPromoter
import scajong.Scajong
import scajong.model.CreatedGameNotification
import scajong.model.NewScoreBoardEntryNotification
import scajong.model.NoFurtherMovesNotification
import scajong.model.ScrambledNotification
import scajong.model.SelectedTileNotification
import scajong.model.TilesChangedNotification
import scajong.model.WonNotification
import scajong.util.SimpleNotification
import scajong.view.SetupSelectedNotification
import scajong.view.View

object TilesGridSnippet extends View {

  var latestNotification = List[SimpleNotification]()

  Scajong.controller.attachView(this)
  
  override def autoClose = true

  val cellWidth = 30;
  val cellHeight = 20;
  val tileOffset = 5;

  def createGame = "#hintButton [onclick]" #> {
    println("create game click")
    <div></div>
  }

  def render = {
    println("TilesGridSnippet RENDER: " + latestNotification)
    if (latestNotification.size == 0) {
      def handleSetupClick(setupId: String): JsCmd = {
        ScajongServer ! new SetupSelectedNotification(Scajong.game.setupById(setupId))
      }
      val setups = Scajong.game.setups
      println("tilessnippet render: Setups: " + setups.size)

      // show new game page
      <div class="tiles"> <ul class="setupList">{

        for (setup <- setups) yield {
          <li>
            <a onclick={ SHtml.ajaxCall(setup.id, handleSetupClick _)._2.toJsCmd }>
              <img src={ "/setups/" + { setup.id } + ".png" }/>
            </a>
          </li>
        }
        //    		for (var i=0; i< setups.length; i++) {
        //					htmlText += '<li><a onclick="' + functionName + '(' + i + ');" href="#">';
        //                    htmlText += '<img src="setups/' + setups[i].id + '.png" /><br />' + setups[i].name + '</a></li>';
        //				}
      } </ul></div>
    } else {
      
      println("Current Notifications:")
      for (sn <- latestNotification) yield {
        println("sn: " + sn)
      }
      
      val notification = latestNotification(0)
      latestNotification = latestNotification.drop(1)
      // notification.moveable .hint
      notification match {
        case n: CreatedGameNotification => {
          println("CREATED GAME Notification: ")
          showTiles
        }
        case n: TilesChangedNotification => {
          println("TILE CHANGED Notification")
          showTiles
        }
        case n: SelectedTileNotification =>  {
          println("TILE CLICKED Notification: " + n.tile)
          showTiles
        }
        case n: WonNotification => {
          println("WON Notification: " + n.setup + " " + n.ms + " " + n.inScoreBoard)
          // TODO: show scoreboard
          <div class="tiles"></div>
        }
        case n: NoFurtherMovesNotification => {
          println("No Further Moves Notification")
          // TODO: No further moves... now what?
          <div class="tiles"></div>
        }
        case n: ScrambledNotification => {
          println("Scrambled Notification")
          // TODO: scramble... now what?
          showTiles
        }
        case n: NewScoreBoardEntryNotification => {
          println("New Score Board Entry Notification")
          <div class="tiles"></div>
        }
      }
    }
  }

  def showTiles = {
    def handleTileClick(id: String): JsCmd = { ScajongServer ! Scajong.game.tiles(id.toInt) }

    val tiles = Scajong.game.sortedTiles
    println("tilessnippet render: Tiles: " + tiles.size)

    <div class="tiles">{
      for (tile <- tiles) yield {
        //val s = "<div class=\"lift:TileSnippet?x=" + tile.x + "\"></td>"

        //<div class={"lift:TileSnippet?x=" + { tile.x } + "&y=" + tile.y} ></div>
        // onclick={SHtml.ajaxCall(tile.tileType.id, setCell _)._2.toJsCmd}

        //          x = tile.x
        //          y = tile.y
        //          z = tile.z
        val id = Scajong.game.calcTileIndex(tile);
        <div onclick={ SHtml.ajaxCall(id.toString, handleTileClick _)._2.toJsCmd } style={ "background-image:url('/tiles/tile.png'); position:absolute; z-index:" + { tile.z } + "; top:" + { tile.y * cellHeight - tile.z * tileOffset } + "px; left:" + { tile.x * cellWidth } + "px; " }>
          <img src={ "/tiles/" + { tile.tileType.name } + ".png" }/>
          {
            if (!Scajong.game.canMove(tile)) {
              <img src="/tiles/disabled.png" style={ "position: absolute; top:0px; left:0px;" }/>
            }
            if (tile == Scajong.game.selected) {
              <img src="/tiles/selected.png" style={ "position: absolute; top:0px; left:0px;" }/>
            }
          }
        </div>
      }
    }</div>
  }
}