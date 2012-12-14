package code
package comet

import code.snippet.TilesGridSnippet
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import scajong.Scajong
import scajong.model.Setup
import scajong.model.Tile
import scajong.util.SimpleNotification
import scajong.util.SimpleSubscriber
import scajong.view.SetupSelectedNotification
import scajong.view.TileClickedNotification
import scajong.view.View
import scajong.model.TilesChangedNotification
import scajong.model.WonNotification
import net.liftweb.http.js.JsCmd
import scajong.model.NewScoreBoardEntryNotification
import scajong.model.ScrambledNotification
import net.liftweb.http.SHtml
import scajong.model.SelectedTileNotification
import scajong.model.NoFurtherMovesNotification
import scajong.model.CreatedGameNotification
import scajong.view.ShowCreateGameMenuNotification
import scajong.view.ShowCreateGameMenuNotification
import scajong.view.ShowScoresNotification
import scajong.view.ShowScoresMenuNotification

/**
 * The screen real estate on the browser will be represented
 * by this component.  When the component changes on the server
 * the changes are automatically reflected in the browser.
 */
class TilesRenderer extends CometActor with CometListener with View with SimpleSubscriber {

  Scajong.game.addSubscriber(this)
  Scajong.controller.attachView(this)
  
  override def autoClose = true
  
  var latestNotifications = List[SimpleNotification]()
  val cellWidth = 30;
  val cellHeight = 20;
  val tileOffset = 5;
  
  override def processNotifications(sn:SimpleNotification) {
    println("processNotifications: " + sn)
    //TilesGridSnippet.latestNotification = TilesGridSnippet.latestNotification ::: List(sn)
    latestNotifications = latestNotifications ::: List(sn)
    reRender
//    sn match {
//      case n: WonNotification => {
//        if (n.inScoreBoard) {
//          //addScoreNotification = n
//          addNotification("AddScore")
//        } else addNotification("ShowScore", n.setup.id)
//      }
//      case n:NoFurtherMovesNotification => addNotification("NoFurtherMoves")
//      case n:TilesChangedNotification => addNotification("UpdateField")
//      case n:ScrambledNotification => addNotification("UpdateField")
//      case n:SelectedTileNotification => addNotification("UpdateField")
//      case n:CreatedGameNotification => addNotification("NewGame")
//      case n:NewScoreBoardEntryNotification => addNotification("ShowScore", n.setup.id, n.position.toString)
//      case _ => // Nothing
//    }
  }
  
  /**
   * When the component is instantiated, register as
   * a listener with the ScajongServer
   */
  def registerWith = ScajongServer

  /**
   * The CometActor is an Actor, so it processes messages.
   * In this case, we're listening for Vector[String],
   * and when we get one, update our private state
   * and reRender() the component.  reRender() will
   * cause changes to be sent to the browser.
   */
  override def lowPriority = {
    //case command: String => {
      //println("String command: " + command)
      //sendNotification(new TileClickedNotification(Scajong.game.tiles(command.toInt)))
    //}
//    case command: Int => {
//      if (Scajong.game.tiles.contains(command)) {
//        println("Int command: " + command)
//        val tile = Scajong.game.tiles(command)
//        sendNotification(new TileClickedNotification(tile))
//      }
//    }
    case command: Tile => {
      println("Tile command: " + command)
     	sendNotification(new TileClickedNotification(command))
    }
    case command: Setup => {
      println("Setup command: " + command)
      sendNotification(new SetupSelectedNotification(command))
    }
    case command: ShowCreateGameMenuNotification => {
      println("Show create game menu command: " + command)
      latestNotifications = latestNotifications ::: List(command)
      reRender
    }
    case command: ShowScoresMenuNotification => {
      println("Show score menu command: " + command)
      latestNotifications = latestNotifications ::: List(command)
      reRender
    }
    case command: ShowScoresNotification => {
      println("Show score menu command: " + command)
      latestNotifications = latestNotifications ::: List(command)
      reRender
    }
  }
  
  def handleSetupClick(setupId: String) : JsCmd = {
    ScajongServer ! new SetupSelectedNotification(Scajong.game.setupById(setupId))
  }

  def handleTileClick(tileId: String) : JsCmd = { ScajongServer ! Scajong.game.tiles(tileId.toInt) }
  
  def handleScoreClick(setupId: String) : JsCmd = { ScajongServer.showScores(Scajong.game.setupById(setupId)) }
  
  def render = {
    println("TilesRenderer RENDER: " + latestNotifications)
    
    println("Current Notifications:")
    for (sn <- latestNotifications) yield {
      println("sn: " + sn)
    }
    
    var notification : SimpleNotification = null
    if (latestNotifications.size > 0) {
      notification = latestNotifications(0)
      latestNotifications = latestNotifications.drop(1)
    }
    
    if (null == notification) {
      showSetups
    } else {
      notification match {
        case n: CreatedGameNotification => {
          println("CREATED GAME Notification")
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
          <div class="tiles">
          	<h1>Your time: { n.ms / 1000.0 } seconds.</h1><br />
          	{
            	if (!n.inScoreBoard) {
            	  <h2>Missed scoreboard entry</h2>
            	} else {
            	  // TODO: form!
            	  <input id="" type="text" value="Anon"/><button class="">Speichern</button>
            	}
          	}
          </div>
        }
        case n: NoFurtherMovesNotification => {
          println("No Further Moves Notification")
          // TODO: No further moves
          <div class="tiles"></div>
        }
        case n: ScrambledNotification => {
          println("Scrambled Notification")
          // TODO: scramble... now what?
          showTiles
        }
        case n: NewScoreBoardEntryNotification => {
          println("New Score Board Entry Notification, pos: " + n.position)
          showScores(n.setup, n.position)
        }
        case n: ShowCreateGameMenuNotification => {
          println("Show Setups Notification")
          showSetups
        }
        case n: ShowScoresMenuNotification => {
          println("Show Scores Menu Notification")
          showScoresMenu
        }
        case n: ShowScoresNotification => {
          println("Show Scores Notification")
          showScores(n.setup)
        }
        case _ => {
          println("unknown notification: " + notification)
          <div class="tiles"></div>
        }
      }
    }
  }

  def showSetups = {
    val setups = Scajong.game.setups
    println("Setups: " + setups.size)

    // show menu of setups
    <div class="tiles">
    	<ul class="setupList"><li class="menuHeader">Start a new game</li>{

      for (setup <- setups) yield {
        <li>
            <a onclick={ SHtml.ajaxCall(setup.id, handleSetupClick _)._2.toJsCmd }>
              <img src={ "/setups/" + { setup.id } + ".png" }/><br />{setup.id}
            </a>
          </li>
      }
    } </ul></div>
  }
  
  def showScores(setup:Setup, marked:Int = 0) = {
    val scores = Scajong.game.scores.getScores(setup)
    println("Scores: " + scores.size)
    <div class="tiles"><table class="scoreTable"><tr>
    														<th colspan="3" class="nl">
    															<img src={ "/setups/" + { setup.id } + ".png" }/>
    															<br />{ setup.id }
    														</th>
    													</tr>
    													<tr>
    														<th class="pos">Position</th>
    														<th class="name">Name</th>
    														<th class="time">Time</th>
    													</tr>{
    	var pos = 0
    	var markedClass = ""
    	for (i <- 0 until scores.size) yield {
    	  pos = i + 1
    	  if (i == marked) {
    	  	markedClass = "marked"
    	  } else {
    	    markedClass = ""
    	  }
    	  <tr class={ markedClass }>
    	  	<td>{pos}</td>
    			<td>{ scores(i).name }</td>
    			<td>{ scores(i).ms / 1000.0 }</td>
    		</tr>
    	}
    }</table></div>
  }
  
  def showScoresMenu = {
    val setups = Scajong.game.setups
    println("Scores-Setups: " + setups.size)

    // show menu of scores
    <div class="tiles">
    	<ul class="setupList"><li class="menuHeader">Show scores</li>{

      for (setup <- setups) yield {
        <li>
            <a onclick={ SHtml.ajaxCall(setup.id, handleScoreClick _)._2.toJsCmd }>
              <img src={ "/setups/" + { setup.id } + ".png" }/><br />{setup.id}
            </a>
          </li>
      }
    } </ul></div>
  }
  
  def showTiles = {
    val tiles = Scajong.game.sortedTiles
    println("Tiles: " + tiles.size)

    <div class="tiles">{
      for (tile <- tiles) yield {
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