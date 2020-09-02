package services

import apis.PokemonApi
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class PokemonServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures with BeforeAndAfter {

  implicit val patience: PatienceConfig = PatienceConfig(30.seconds, 1.seconds)

  val pokemonApi: PokemonApi = mock[PokemonApi]
  val pokemonService = new PokemonServiceImpl(pokemonApi)

  val classicalPokemonLite: PokemonLite = PokemonLite(25, "pikachu")
  val formPokemonLite: PokemonLite = PokemonLite(642, "thundurus-incarnate")
  val additionalFormPokemonLite: PokemonLite = PokemonLite(10020, "thundurus-therian")
  val pokemonLite: Seq[PokemonLite] = Seq(classicalPokemonLite, formPokemonLite, additionalFormPokemonLite)

  val classicalPokemon: Pokemon = Pokemon(25, "pikachu", ElectricType)
  val formPokemon: Pokemon = Pokemon(642, "thundurus-incarnate", ElectricType, Some(FlyingType))

  before {
    reset(pokemonApi)
  }

  "PokemonService getWastedTime" should {
    "succeed" in {
      when(pokemonApi.getPokemonByType(any[PokemonType])).thenReturn(Future.successful(pokemonLite))
      when(pokemonApi.getPokemonById(anyInt())).thenReturn(
        Future.successful(formPokemon),
        Future.successful(classicalPokemon)
      )

      val result = pokemonService.getPokemonByType(ElectricType)

      whenReady(result) { pokemon =>
        pokemon mustEqual Seq(classicalPokemon, formPokemon)

        verify(pokemonApi).getPokemonByType(ElectricType)
        verify(pokemonApi).getPokemonById(classicalPokemonLite.id)
        verify(pokemonApi).getPokemonById(formPokemonLite.id)
        verify(pokemonApi, never()).getPokemonById(additionalFormPokemonLite.id)
        verifyNoMoreInteractions(pokemonApi)
      }
    }
  }
}
