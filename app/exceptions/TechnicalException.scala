package exceptions

sealed class TechnicalException(m: String, c: Throwable) extends Exception(m, c)

case class ConnectionException(e: Throwable) extends TechnicalException(s"Connection failed, error: ${e.getMessage}", e)