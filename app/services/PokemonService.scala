package services

import apis.PokemonApi
import com.google.inject.{ImplementedBy, Inject, Singleton}
import helpers.LoggingHelper
import helpers.PokemonHelper._
import models.{Pokemon, PokemonLite, PokemonType}

import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[PokemonServiceImpl])
trait PokemonService {

  def getPokemonByType(pokemonType: PokemonType): Future[Seq[Pokemon]]
}

@Singleton
class PokemonServiceImpl @Inject()(pokemonApi: PokemonApi)(implicit ec: ExecutionContext) extends PokemonService with LoggingHelper {

  override def getPokemonByType(pokemonType: PokemonType): Future[Seq[Pokemon]] = {
    logger.debug(s"Getting all Pokémon with type $pokemonType")

    for {
      pokemonLite <- pokemonApi.getPokemonByType(pokemonType)
      filteredPokemon = filterPokemon(pokemonLite)
      pokemon <- Future.traverse(filteredPokemon)(pl => pokemonApi.getPokemonById(pl.id))
    } yield {
      pokemon.sortBy(_.number)
    }
  }

  private def filterPokemon(pokemon: Seq[PokemonLite]): Seq[PokemonLite] = {
    pokemon.filter { p =>
      !isAForm(p.name) || isInNationalDex(p.id) || anotherFormExists(pokemon, p)
    }
  }

  private def anotherFormExists(pokemon: Seq[PokemonLite], form: PokemonLite): Boolean = {
    (pokemon diff Seq(form)).forall(p => !p.name.contains(baseName(form.name)))
  }
}