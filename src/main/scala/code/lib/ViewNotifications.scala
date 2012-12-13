package scajong.view

import scajong.util.SimpleNotification
import scajong.model.Setup

class ShowCreateGameMenuNotification extends SimpleNotification
class ShowScoresMenuNotification extends SimpleNotification
class ShowScoresNotification(val setup:Setup) extends SimpleNotification

