package apis

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.PokemonType
import play.api.Configuration
import play.api.libs.ws.{WSClient, WSRequest}


@ImplementedBy(classOf[PokemonApiClientImpl])
trait PokemonApiClient {

  def pokemonByType(pokemonType: PokemonType): WSRequest

  def pokemonById(id: Int): WSRequest
}

@Singleton
class PokemonApiClientImpl @Inject()(ws: WSClient, configuration: Configuration) extends PokemonApiClient {

  private val host: String = configuration.get[String]("pokemon.api.host")

  override def pokemonByType(pokemonType: PokemonType): WSRequest = {
    ws.url(s"$host/type/$pokemonType")
  }

  override def pokemonById(id: Int): WSRequest = {
    ws.url(s"$host/pokemon/$id")
  }
}
