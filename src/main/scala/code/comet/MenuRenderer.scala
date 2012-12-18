package code
package comet

import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import code.snippet.MenuSnippet
import scajong.Scajong
/**
 * The screen real estate on the browser will be represented
 * by this component.  When the component changes on the server
 * the changes are automatically reflected in the browser.
 */
class MenuRenderer extends CometActor with CometListener {

  /**
   * When the component is instantiated, register as
   * a listener with the ScajongServer
   */
  def registerWith = LiftViewServer

  /**
   * The CometActor is an Actor, so it processes messages.
   * In this case, we're listening for Vector[String],
   * and when we get one, update our private state
   * and reRender() the component.  reRender() will
   * cause changes to be sent to the browser.
   */
  override def lowPriority = {
    case _ => {
      println("MenuRenderer: lowPriority!")
      reRender
    }
  }
  
  def render = {
    val gameRunning = Scajong.controller.tiles.size != 0
    <div class="menu">
    	<button class="lift:MenuSnippet.createGame" id="createButton">Create New Game</button>
    	<button class="lift:MenuSnippet.showScoresMenu" id="showScoresMenuButton">Show Highscores</button>
    	{
	    	if (gameRunning) {
	    	  if (!MenuSnippet.showBackToGameButton) {
						<button class="lift:MenuSnippet.showMoveables" id="moveablesButton">Show Moveables (+5 Seconds)</button>
						<button class="lift:MenuSnippet.showHint" id="hintButton">Show Hint (+15 Seconds)</button>
	    	  } else {
	    	    <button class="lift:MenuSnippet.backToGame" id="backToGameButton">Back To Game</button>
	    	  }
		    }
    	}
    </div>
  }
}
