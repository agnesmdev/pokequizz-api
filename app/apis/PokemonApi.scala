package apis

import com.google.inject.{ImplementedBy, Inject, Singleton}
import exceptions.ApiException
import helpers.LoggingHelper
import models.{Pokemon, PokemonLite, PokemonType}
import play.api.http.Status

import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[PokemonApiImpl])
trait PokemonApi {

  def getPokemonByType(pokemonType: PokemonType): Future[Seq[PokemonLite]]

  def getPokemonById(id: Int): Future[Pokemon]
}

@Singleton
class PokemonApiImpl @Inject()(client: PokemonApiClient)(implicit ec: ExecutionContext) extends PokemonApi with LoggingHelper {

  override def getPokemonByType(pokemonType: PokemonType): Future[Seq[PokemonLite]] = {
    logger.debug(s"Getting all Pokémon of type $pokemonType from API")

    client.pokemonByType(pokemonType).get().map { response =>
      response.status match {
        case Status.OK =>
          val pokemonResponse = response.json.as[PokemonByTypeResponse]
          val pokemon = pokemonResponse.pokemon.map(_.toPokemonLite)

          logger.debug(s"Successfully retrieved ${pokemon.length} Pokémon of type $pokemonType from API")
          pokemon
        case status => throw ApiException(status, response.body)
      }
    }
  }

  override def getPokemonById(id: Int): Future[Pokemon] = {
    logger.debug(s"Getting Pokémon with id $id from API")

    client.pokemonById(id).get().map { response =>
      response.status match {
        case Status.OK =>
          val pokemonResponse = response.json.as[PokemonByIdResponse]
          val pokemon = pokemonResponse.toPokemon

          logger.debug(s"Successfully retrieved ${pokemon.name} with id $id from API")
          pokemon
        case status => throw ApiException(status, response.body)
      }
    }
  }
}
