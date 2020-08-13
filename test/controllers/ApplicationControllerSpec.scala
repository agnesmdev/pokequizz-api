package controllers

import buildinfo.BuildInfo
import org.scalatestplus.play.PlaySpec
import play.api.http.{HeaderNames, MimeTypes}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Configuration}

class ApplicationControllerSpec extends PlaySpec {

  val app: Application = new GuiceApplicationBuilder()
    .loadConfig(env => Configuration.load(env))
    .build()

  "ApplicationController home" should {
    "redirect" in {
      val request = FakeRequest(GET, "/")

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual "/status"
    }
  }

  "ApplicationController status" should {
    "return html" in {
      val request = FakeRequest(GET, "/status").withHeaders(HeaderNames.ACCEPT -> MimeTypes.HTML)

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsString(result) mustEqual views.html.app.status(BuildInfo.name,
        BuildInfo.normalizedName,
        BuildInfo.version,
        BuildInfo.scalaVersion,
        BuildInfo.sbtVersion,
        BuildInfo.gitHeadCommit
      ).toString()
    }

    "return json" in {
      val request = FakeRequest(GET, "/status").withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.obj(
        "name" -> BuildInfo.name,
        "normalizedName" -> BuildInfo.normalizedName,
        "version" -> BuildInfo.version,
        "scalaVersion" -> BuildInfo.scalaVersion,
        "sbtVersion" -> BuildInfo.sbtVersion,
        "gitHeadCommit" -> BuildInfo.gitHeadCommit
      )
    }
  }
}
