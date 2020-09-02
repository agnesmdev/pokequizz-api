package apis

import helpers.PokemonHelper.extractNumber
import helpers.StringHelper
import models.{Pokemon, PokemonLite, PokemonType}
import play.api.libs.json.{Json, Reads}

case class PokemonByTypeResponse(pokemon: Seq[PokemonByTypeResource])

case class PokemonByIdResponse(species: PokemonSpecies, name: String, types: Seq[PokemonTypeResource]) extends StringHelper {
  def toPokemon: Pokemon = Pokemon(species.number, name.wordsCapitalize, types.head.toPokemonType, types.drop(1).lastOption.map(_.toPokemonType))
}

case class PokemonSpecies(name: String, url: String) {
  lazy val number: Int = extractNumber(url)
}

case class PokemonByTypeResource(pokemon: PokemonByTypeData) {
  def toPokemonLite: PokemonLite = PokemonLite(extractNumber(pokemon.url), pokemon.name.capitalize)
}

case class PokemonTypeResource(`type`: PokemonTypeData) {
  def toPokemonType: PokemonType = PokemonType(`type`.name)
}

case class PokemonByTypeData(name: String, url: String)

case class PokemonTypeData(name: String)

object PokemonByTypeResponse {
  implicit val pokemonByTypeResponseReads: Reads[PokemonByTypeResponse] = Json.reads[PokemonByTypeResponse]
}

object PokemonByIdResponse {
  implicit val pokemonByIdResponseReads: Reads[PokemonByIdResponse] = Json.reads[PokemonByIdResponse]
}

object PokemonSpecies {
  implicit val pokemonSpeciesReads: Reads[PokemonSpecies] = Json.reads[PokemonSpecies]
}

object PokemonByTypeResource {
  implicit val pokemonByTypeResourceReads: Reads[PokemonByTypeResource] = Json.reads[PokemonByTypeResource]
}

object PokemonTypeResource {
  implicit val pokemonTypeResourceReads: Reads[PokemonTypeResource] = Json.reads[PokemonTypeResource]
}

object PokemonByTypeData {
  implicit val pokemonByTypeDataReads: Reads[PokemonByTypeData] = Json.reads[PokemonByTypeData]
}

object PokemonTypeData {
  implicit val pokemonTypeDataReads: Reads[PokemonTypeData] = Json.reads[PokemonTypeData]
}
