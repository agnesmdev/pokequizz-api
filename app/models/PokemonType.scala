package models

import play.api.libs.json.{JsString, Writes}

sealed trait PokemonType {
  def name: String

  override def toString: String = name.toLowerCase
}

object NormalType extends PokemonType {
  override val name: String = "NORMAL"
}

object FireType extends PokemonType {
  override val name: String = "FIRE"
}

object WaterType extends PokemonType {
  override val name: String = "WATER"
}

object GrassType extends PokemonType {
  override val name: String = "GRASS"
}

object FlyingType extends PokemonType {
  override val name: String = "FLYING"
}

object FightingType extends PokemonType {
  override val name: String = "FIGHTING"
}

object PoisonType extends PokemonType {
  override val name: String = "POISON"
}

object ElectricType extends PokemonType {
  override val name: String = "ELECTRIC"
}

object GroundType extends PokemonType {
  override val name: String = "GROUND"
}

object RockType extends PokemonType {
  override val name: String = "ROCK"
}

object PsychicType extends PokemonType {
  override val name: String = "PSYCHIC"
}

object IceType extends PokemonType {
  override val name: String = "ICE"
}

object BugType extends PokemonType {
  override val name: String = "BUG"
}

object GhostType extends PokemonType {
  override val name: String = "GHOST"
}

object SteelType extends PokemonType {
  override val name: String = "STEEL"
}

object DragonType extends PokemonType {
  override val name: String = "DRAGON"
}

object DarkType extends PokemonType {
  override val name: String = "DARK"
}

object FairyType extends PokemonType {
  override val name: String = "FAIRY"
}

case class UnknownType(name: String) extends PokemonType

object PokemonType {
  implicit val pokemonTypeWrites: Writes[PokemonType] = pt => JsString(pt.toString)

  def apply(value: String): PokemonType = value.toUpperCase match {
    case NormalType.name => NormalType
    case FireType.name => FireType
    case WaterType.name => WaterType
    case GrassType.name => GrassType
    case FlyingType.name => FlyingType
    case FightingType.name => FightingType
    case PoisonType.name => PoisonType
    case ElectricType.name => ElectricType
    case GroundType.name => GroundType
    case RockType.name => RockType
    case PsychicType.name => PsychicType
    case IceType.name => IceType
    case BugType.name => BugType
    case GhostType.name => GhostType
    case SteelType.name => SteelType
    case DragonType.name => DragonType
    case DarkType.name => DarkType
    case FairyType.name => FairyType
    case _ => UnknownType(value)
  }
}
