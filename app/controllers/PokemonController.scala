package controllers

import exceptions._
import helpers.LoggingHelper
import javax.inject.Inject
import models.{PokemonType, UnknownType}
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.PokemonService

import scala.concurrent.{ExecutionContext, Future}

class PokemonController @Inject()(cache: AsyncCacheApi, cc: ControllerComponents, pokemonService: PokemonService)
                                 (implicit ec: ExecutionContext)
  extends AbstractController(cc) with LoggingHelper {

  def getPokemonByType(`type`: String): EssentialAction = Action.async { implicit request =>
    logger.info(s"Request to get all Pokémon with type ${`type`}")

    PokemonType(`type`) match {
      case UnknownType(name) =>
        logger.warn(s"Failed to get Pokémon with type $name, error: unknown type")
        Future.successful(BadRequest(s"Unknown type $name"))
      case pokemonType => cache.get[JsValue](s"pokemon.type.$pokemonType").flatMap {
        case Some(pokemon) =>
          logger.info(s"Cached result for Pokémon with type $pokemonType")
          Future.successful(Ok(pokemon))
        case None => pokemonService.getPokemonByType(pokemonType).map { pokemon =>
          logger.info(s"Successfully retrieved ${pokemon.length} Pokémon with type $pokemonType")

          val jsonPokemon = Json.toJson(pokemon)
          cache.set(s"pokemon.type.$pokemonType", jsonPokemon)
          Ok(jsonPokemon)
        }
      }.recover {
        case e: TechnicalException =>
          logger.warn(s"Failed to get Pokémon with type $pokemonType, error: ${e.getMessage}", e)
          BadGateway(e.getMessage)
        case e =>
          logger.error(s"Failed to get Pokémon with type $pokemonType, error: ${e.getMessage}", e)
          InternalServerError(e.getMessage)
      }
    }
  }
}
