package models

import play.api.libs.json.{Json, Writes}

case class Pokemon(number: Int,
                   name: String,
                   primaryType: PokemonType,
                   secondaryType: Option[PokemonType] = None)

case class PokemonLite(id: Int, name: String)

object Pokemon {
  implicit val pokemonWrites: Writes[Pokemon] = Json.writes[Pokemon]
}