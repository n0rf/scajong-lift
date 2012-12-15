package code
package comet

import scajong.Scajong
import net.liftweb.http.CometListener
import scajong.model.WonNotification
import scajong.model.Setup
import net.liftweb.http.js.JsCmd
import scajong.model.Tile
import scajong.model.NewScoreBoardEntryNotification
import scajong.util.SimpleNotification
import scajong.model.ScrambledNotification
import net.liftweb.http.CometActor
import scajong.util.SimpleSubscriber
import scajong.model.NoFurtherMovesNotification
import scajong.model.CreatedGameNotification
import scajong.view.View
import net.liftweb.http.SHtml
import scajong.model.TilePair
import scajong.model.StopHintNotification
import scajong.model.StartMoveablesNotification
import scajong.model.StopMoveablesNotification
import scajong.controller.Controller
import scajong.model.TilesRemovedNotification
import scajong.model.TileSelectedNotification
import main.scala.code.lib._
import scajong.model.StartHintNotification
/**
 * The screen real estate on the browser will be represented
 * by this component.  When the component changes on the server
 * the changes are automatically reflected in the browser.
 */
class TilesRenderer extends CometActor with CometListener with View {

  private var fieldTiles : List[Tile] = null
  private var selectedTile : Tile = null
  private var hintTiles : TilePair = null
  private var showMoveables : Boolean = false
  
  private val controller : Controller = Scajong.controller
  Scajong.controller.attachView(this)
  
  override def autoClose = true
  
  var wonNotification : WonNotification = null
  
  var latestNotifications = List[SimpleNotification]()
  val cellWidth = 30;
  val cellHeight = 20;
  val tileOffset = 5;
  
  override def processNotification(sn:SimpleNotification) {
    println("processNotifications: " + sn)
    latestNotifications = latestNotifications ::: List(sn)
    reRender
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
    case command: Tile => {
      println("Tile command: " + command)
      controller.selectTile(command)
     	//sendNotification(new TileClickedNotification(command))
    }
    case command: Setup => {
      println("Setup command: " + command)
      controller.startNewGame(command)
      //sendNotification(new SetupSelectedNotification(command))
    }
    case command: RequestHintNotification => {
      println("Request hint command: " + command)
      controller.requestHint
      //sendNotification(new RequestHintNotification)
    }
    case command: RequestMoveablesNotification => {
      println("Request moveables command: " + command)
      controller.requestMoveables
      //sendNotification(new RequestMoveablesNotification)
    }
    case command: AddNewScoreEntryNotification => {
      println("Add new score command: " + command)
      if (null != wonNotification) {
        controller.addScore(wonNotification.setup, command.name, wonNotification.ms)
      	//sendNotification(new AddScoreNotification(wonNotification.setup, command.name, wonNotification.ms))
      	wonNotification = null
      }
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
    controller.startNewGame(controller.setupById(setupId))
  }

  def handleTileClick(tileId: String) : JsCmd = {
    controller.selectTile(controller.tiles(tileId.toInt))
  }
  
  def handleScoreClick(setupId: String) : JsCmd = { 
    ScajongServer ! new ShowScoresNotification(controller.setupById(setupId)) 
  }
  
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
          fieldTiles = controller.sortedTiles
          showTiles()
        }
        case TilesRemovedNotification(tiles) => {
          println("TILE REMOVED Notification")
          fieldTiles = fieldTiles diff List(tiles.tile1, tiles.tile2)
          showTiles()
        }
        case TileSelectedNotification(tile) =>  {
          println("TILE CLICKED Notification: " + tile)
          selectedTile = tile
          showTiles()
        }
        case n: WonNotification => {
          println("WON Notification: " + n.setup + " " + n.ms + " " + n.inScoreBoard)
          handleWonNotification(n)
        }
        case n: NoFurtherMovesNotification => {
          println("No Further Moves Notification")
          // TODO: No further moves
          <div class="tiles"></div>
        }
        case n: ScrambledNotification => {
          println("Scrambled Notification")
          // TODO: scramble
          <div class="tiles"></div>
        }
        case NewScoreBoardEntryNotification(setup, position) => {
          println("New Score Board Entry Notification, pos: " + position)
          showScores(setup, position)
        }
        case n: ShowCreateGameMenuNotification => {
          println("Show Setups Notification")
          showSetups
        }
        case n: ShowScoresMenuNotification => {
          println("Show Scores Menu Notification")
          showScoresMenu
        }
        case ShowScoresNotification(setup) => {
          println("Show Scores Notification")
          showScores(setup)
        }
        case StartHintNotification(tiles) => {
          println("Start Hint Notification")
          hintTiles = tiles
          showTiles()
        }
        case StopHintNotification() => {
          println("Stop Hint Notification")
          hintTiles = null
          showTiles()
        }
        case StartMoveablesNotification() => {
          println("Start Moveable Notification")
          showMoveables = true
          showTiles()
        }
        case StopMoveablesNotification() => {
          println("Stop Moveable Notification")
          showMoveables = false
          showTiles()
        }
        case _ => {
          println("unknown notification: " + notification)
          <div class="tiles"></div>
        }
      }
    }
  }

  def showSetups = {
    val setups = controller.setups
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
    val scores = controller.scores.getScores(setup)
    println("Scores: " + scores.size)
    <div class="tiles">
    	<table class="scoreTable">
    	<tr>
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
    val setups = controller.setups
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
  
  def showTiles() = {
    println("Tiles: " + fieldTiles.size)
    var tileStr : String = ""

    <div class="tiles">{
      for (tile <- fieldTiles) yield {
        <div onclick={ SHtml.ajaxCall(controller.calcTileIndex(tile).toString, handleTileClick _)._2.toJsCmd } style={ "margin-top:20px; background-image:url('/tiles/tile.png'); position:absolute; z-index:" + { tile.z } + "; top:" + { tile.y * cellHeight - tile.z * tileOffset } + "px; left:" + { tile.x * cellWidth } + "px; " }>
          <img src={ "/tiles/" + { tile.tileType.name } + ".png" }/>
          {
            
            if (showMoveables && !controller.canMove(tile)) {
              <img src="/tiles/disabled.png" style="position: absolute; top:0px; left:0px;" />
            } else if (null != hintTiles && (hintTiles.tile1 == tile || hintTiles.tile2 == tile)) {
	            // show as hint (overlay with hint image)
	          	<img src="/tiles/hint.png" style="position: absolute; top:0px; left:0px;" />
	          } else if (tile == selectedTile) {
	          	<img src="/tiles/selected.png" style="position: absolute; top:0px; left:0px;" />
	          }
          }
        </div>
      }
    }</div>
  }
  
  def handleWonNotification(n:WonNotification) = {
    // store notification for add score action
    wonNotification = n
    <div class="tiles">
    	<h1>Your time: { n.ms / 1000.0 } seconds.</h1><br />
      {
      	if (!n.inScoreBoard) {
      	  <h2>Missed scoreboard entry</h2>
      	} else {
      	  <form class="lift:form.ajax">
      			<input class="lift:MenuSnippet.addScoreEntry" id="entry_name" value="" />
      			<input type="submit" value="Save"/>
      		</form>
      	}
    	}
    </div>
  }
}
