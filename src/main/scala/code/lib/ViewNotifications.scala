package scajong.view

import scajong.util.SimpleNotification
import scajong.model.Setup

// internal notifications for tiles renderer
case class ShowCreateGameMenuNotification() extends SimpleNotification
case class ShowScoresMenuNotification() extends SimpleNotification
case class ShowScoresNotification(val setup:Setup) extends SimpleNotification

// internal notification used to add a new score entry and send a notification about this entry to the controller
case class AddNewScoreEntryNotification(val name:String) extends SimpleNotification
