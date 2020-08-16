package apis

import exceptions.ApiException
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source

class PokemonApiSpec extends PlaySpec with MockitoSugar with ScalaFutures with BeforeAndAfter {

  implicit val patience: PatienceConfig = PatienceConfig(30.seconds, 1.seconds)

  val client: PokemonApiClient = mock[PokemonApiClient]
  val pokemonApi = new PokemonApiImpl(client)

  val request: WSRequest = mock[WSRequest]
  val response: WSResponse = mock[WSResponse]

  val id = 10123
  val electricPokemonJson: JsValue = Json.parse(Source.fromResource("electric_pokemon_api.json").mkString)
  val electricPokemon: Seq[PokemonLite] = electricPokemonJson.as[PokemonByTypeResponse].pokemon.map(_.toPokemonLite)
  val pokemonByIdJson: JsValue = Json.parse(Source.fromResource("pokemon_by_id.json").mkString)
  val pokemonById: Pokemon = pokemonByIdJson.as[PokemonByIdResponse].toPokemon


  before {
    reset(client, request, response)
  }

  "PokemonApi getPokemonByType" should {
    "succeed" in {
      when(client.pokemonByType(any[PokemonType])).thenReturn(request)
      when(request.get()).thenReturn(Future.successful(response))
      when(response.status).thenReturn(Status.OK)
      when(response.json).thenReturn(electricPokemonJson)

      val result = pokemonApi.getPokemonByType(ElectricType)

      whenReady(result) { pokemon =>
        pokemon mustEqual electricPokemon

        verify(client).pokemonByType(ElectricType)
        verify(request).get()
        verify(response).status
        verify(response).json
      }
    }

    "fail" in {
      when(client.pokemonByType(any[PokemonType])).thenReturn(request)
      when(request.get()).thenReturn(Future.successful(response))
      when(response.status).thenReturn(Status.BAD_GATEWAY)
      when(response.body).thenReturn("error")

      val result = pokemonApi.getPokemonByType(ElectricType)

      whenReady(result.failed) { e =>
        e mustEqual ApiException(Status.BAD_GATEWAY, "error")

        verify(client).pokemonByType(ElectricType)
        verify(request).get()
        verify(response).status
        verify(response).body
      }
    }
  }

  "PokemonApi getPokemonById" should {
    "succeed" in {
      when(client.pokemonById(anyInt)).thenReturn(request)
      when(request.get()).thenReturn(Future.successful(response))
      when(response.status).thenReturn(Status.OK)
      when(response.json).thenReturn(pokemonByIdJson)

      val result = pokemonApi.getPokemonById(id)

      whenReady(result) { pokemon =>
        pokemon mustEqual pokemonById

        verify(client).pokemonById(id)
        verify(request).get()
        verify(response).status
        verify(response).json
      }
    }

    "fail" in {
      when(client.pokemonById(anyInt)).thenReturn(request)
      when(request.get()).thenReturn(Future.successful(response))
      when(response.status).thenReturn(Status.INTERNAL_SERVER_ERROR)
      when(response.body).thenReturn("error")

      val result = pokemonApi.getPokemonById(id)

      whenReady(result.failed) { e =>
        e mustEqual ApiException(Status.INTERNAL_SERVER_ERROR, "error")

        verify(client).pokemonById(id)
        verify(request).get()
        verify(response).status
        verify(response).body
      }
    }
  }
}
