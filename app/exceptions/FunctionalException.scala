package exceptions

import java.util.UUID

import play.api.libs.json.JsObject

sealed class FunctionalException(m: String, c: Throwable) extends Exception(m, c)

case class NotFoundException(id: UUID) extends FunctionalException(s"Element with id $id does not exist", null)

case class InvalidJsonException(error: String) extends FunctionalException(s"Invalid json input, error: $error", null)

case class InvalidParameterException(e: Throwable) extends FunctionalException(s"Invalid parameter, error: ${e.getMessage}", null)

case class InvalidConfigurationException(message: String) extends FunctionalException(s"Invalid configuration, error: $message", null)

case class ConflictException(message: String) extends FunctionalException(message, null)
