package apis

import com.google.inject.{ImplementedBy, Inject, Singleton}
import exceptions.ApiException
import helpers.LoggingHelper
import models.{Pokemon, PokemonLite, PokemonType}
import play.api.cache.AsyncCacheApi
import play.api.http.Status

import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[PokemonApiImpl])
trait PokemonApi {

  def getPokemonByType(pokemonType: PokemonType): Future[Seq[PokemonLite]]

  def getPokemonById(id: Int): Future[Pokemon]
}

@Singleton
class PokemonApiImpl @Inject()(client: PokemonApiClient, cache: AsyncCacheApi)(implicit ec: ExecutionContext) extends PokemonApi with LoggingHelper {

  override def getPokemonByType(pokemonType: PokemonType): Future[Seq[PokemonLite]] = {
    logger.debug(s"Getting all Pokémon of type $pokemonType from API")

    cache.get[Seq[PokemonLite]](s"api.type.$pokemonType").flatMap {
      case Some(pokemon) =>
        logger.debug(s"Cached result for all Pokémon of type $pokemonType from API")
        Future.successful(pokemon)
      case None => client.pokemonByType(pokemonType).get().map { response =>
        response.status match {
          case Status.OK =>
            val pokemonResponse = response.json.as[PokemonByTypeResponse]
            val pokemon = pokemonResponse.pokemon.map(_.toPokemonLite)
            cache.set(s"api.type.$pokemonType", pokemon)

            logger.debug(s"Successfully retrieved ${pokemon.length} Pokémon of type $pokemonType from API")
            pokemon
          case status => throw ApiException(status, response.body)
        }
      }
    }
  }

  override def getPokemonById(id: Int): Future[Pokemon] = {
    logger.debug(s"Getting Pokémon with id $id from API")

    cache.get[Pokemon](s"api.id.$id").flatMap {
      case Some(pokemon) =>
        logger.debug(s"Cached result for Pokémon with id $id from API")
        Future.successful(pokemon)
      case None => client.pokemonById(id).get().map { response =>
        response.status match {
          case Status.OK =>
            val pokemonResponse = response.json.as[PokemonByIdResponse]
            val pokemon = pokemonResponse.toPokemon
            cache.set(s"api.id.$id", pokemon)

            logger.debug(s"Successfully retrieved ${pokemon.name} with id $id from API")
            pokemon
          case status => throw ApiException(status, response.body)
        }
      }
    }
  }
}
