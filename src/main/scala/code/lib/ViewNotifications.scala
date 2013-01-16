package main.scala.code.lib

import scajong.util.SimpleNotification
import scajong.model.Setup
import scajong.model.Tile

// internal notifications for tiles renderer
case class ShowCreateGameMenuNotification() extends SimpleNotification
case class ShowScoresMenuNotification() extends SimpleNotification
case class ShowScoresNotification(val setup:Setup) extends SimpleNotification
case class BackToGameNotification() extends SimpleNotification

// internal notifications handled in view server
case class RequestHintNotification() extends SimpleNotification
case class RequestMoveablesNotification() extends SimpleNotification
case class RequestScrambleNotification() extends SimpleNotification
case class StartNewGameNotification(setup:Setup) extends SimpleNotification
case class TileClickNotification(tile:Tile) extends SimpleNotification
case class AddScoreNotification(setup:Setup, name:String, ms:Int) extends SimpleNotification

// internal notification used to add a new score entry and send a notification about this entry to the controller
case class AddNewScoreEntryNotification(val name:String) extends SimpleNotification