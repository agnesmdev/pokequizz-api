package controllers

import buildinfo.BuildInfo
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

class ApplicationController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def home: Action[AnyContent] = Action { implicit request =>
    Redirect("/status")
  }

  def status: Action[AnyContent] = Action { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.app.status(BuildInfo.name,
        BuildInfo.normalizedName,
        BuildInfo.version,
        BuildInfo.scalaVersion,
        BuildInfo.sbtVersion,
        BuildInfo.gitHeadCommit
      ))
      case Accepts.Json() => Ok(Json.obj(
        "name" -> BuildInfo.name,
        "normalizedName" -> BuildInfo.normalizedName,
        "version" -> BuildInfo.version,
        "scalaVersion" -> BuildInfo.scalaVersion,
        "sbtVersion" -> BuildInfo.sbtVersion,
        "gitHeadCommit" -> BuildInfo.gitHeadCommit
      ))
    }
  }
}
