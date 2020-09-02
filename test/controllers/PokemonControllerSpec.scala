package controllers

import exceptions.ApiException
import models.{ElectricType, Pokemon, PokemonType, SteelType, WaterType}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Configuration}
import services.PokemonService

import scala.concurrent.Future

class PokemonControllerSpec extends PlaySpec with MockitoSugar with ScalaFutures with BeforeAndAfter {

  val pokemonService: PokemonService = mock[PokemonService]
  val app: Application = new GuiceApplicationBuilder()
    .loadConfig(env => Configuration.load(env))
    .overrides(bind[PokemonService].toInstance(pokemonService))
    .build()

  before {
    reset(pokemonService)
  }

  val pokemon = Seq(
    Pokemon(25, "Pikachu", ElectricType, None),
    Pokemon(81, "Magnemite", ElectricType, Some(SteelType))
  )

  "PokemonController getPokemonByType" should {
    "return Ok" in {
      val request = FakeRequest(GET, s"/pokemon/types/$ElectricType")
      when(pokemonService.getPokemonByType(any[PokemonType])).thenReturn(Future.successful(pokemon))

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.toJson(pokemon)
      verify(pokemonService).getPokemonByType(ElectricType)
    }

    "return Ok with cache" in {
      val request = FakeRequest(GET, s"/pokemon/types/$ElectricType")

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.toJson(pokemon)
      verifyNoInteractions(pokemonService)
    }

    "return BadRequest if type is incorrect" in {
      val incorrectType = "light"
      val request = FakeRequest(GET, s"/pokemon/types/$incorrectType")

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual s"Unknown type $incorrectType"
      verifyNoInteractions(pokemonService)
    }

    "return BadGateway if external service fails" in {
      val request = FakeRequest(GET, s"/pokemon/types/$WaterType")
      when(pokemonService.getPokemonByType(any[PokemonType])).thenReturn(Future.failed(ApiException(500, "error")))

      val result = route(app, request).value

      status(result) mustEqual BAD_GATEWAY
      contentAsString(result) mustEqual "status: 500, body: error"
      verify(pokemonService).getPokemonByType(WaterType)
    }

    "return InternalServerError if unexpected error happens" in {
      val request = FakeRequest(GET, s"/pokemon/types/$WaterType")
      when(pokemonService.getPokemonByType(any[PokemonType])).thenReturn(Future.failed(new Exception("boom")))

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
      contentAsString(result) mustEqual "boom"
      verify(pokemonService).getPokemonByType(WaterType)
    }
  }
}
