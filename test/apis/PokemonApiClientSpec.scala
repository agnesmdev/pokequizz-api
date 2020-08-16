package apis

import models._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.duration._

class PokemonApiClientSpec extends PlaySpec with MockitoSugar with ScalaFutures with BeforeAndAfter {

  implicit val patience: PatienceConfig = PatienceConfig(30.seconds, 1.seconds)

  val host = "host"
  val id = 123

  val ws: WSClient = mock[WSClient]
  val configuration: Configuration = mock[Configuration]
  when(configuration.get[String]("pokemon.api.host")).thenReturn(host)

  val client = new PokemonApiClientImpl(ws, configuration)

  before {
    reset(ws)
  }

  "PokemonApiClient pokemonByType" should {
    "succeed" in {
      client.pokemonByType(ElectricType)
      verify(ws).url(s"$host/type/$ElectricType")
    }
  }
  "PokemonApiClient pokemonById" should {
    "succeed" in {
      client.pokemonById(id)
      verify(ws).url(s"$host/pokemon/$id")
    }
  }
}
