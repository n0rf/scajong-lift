package bootstrap.liftweb

import net.liftmodules.JQueryModule
import net.liftweb.common.Full
import net.liftweb.http.Html5Properties
import net.liftweb.http.LiftRules
import net.liftweb.http.LiftRulesMocker.toLiftRules
import net.liftweb.http.Req
import net.liftweb.sitemap.LocPath.stringToLocPath
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.SiteMap
import net.liftweb.util.Vendor.valToVender

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("code")

//    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQueryArtifacts
//
//    //Show the spinny image when an Ajax call starts
//    LiftRules.ajaxStart =
//      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
//    
//    // Make the spinny image go away when it ends
//    LiftRules.ajaxEnd =
//      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    //LiftRules.dispatch.append(RootController)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    // Build SiteMap
    def sitemap(): SiteMap = SiteMap(
      Menu("Scajong - Mahjong in scala-lift") / "index"
    )
    LiftRules.setSiteMap(sitemap)
    
    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery172
    JQueryModule.init()
  }
}
