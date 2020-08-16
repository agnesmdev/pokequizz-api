package helpers

object PokemonHelper {

  private val pokemonTotal: Int = 896

  def isAForm(name: String): Boolean = name.contains("-")

  def isInNationalDex(number: Int): Boolean = number <= pokemonTotal

  def baseName(name: String): String = name.split("-").head

  def extractNumber(url: String): Int = url.split("/").last.toInt
}
