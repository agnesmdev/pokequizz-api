package helpers

trait StringHelper {

  implicit class StringUtil(s: String) {
    def wordsCapitalize: String = s.split("-").map(_.capitalize).mkString(" ")
  }
}
