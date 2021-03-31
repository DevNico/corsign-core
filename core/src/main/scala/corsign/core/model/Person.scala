package corsign.core.model

import play.api.libs.json.Json

import java.util.{Date, UUID}

case class Person(
  uuid: UUID,
  firstname: String,
  lastname: String,
  gender: Option[String] = None,
  birthday: Option[Date] = None,
  phoneNumber: Option[String] = None,
  email: Option[String] = None,
  idCardNumber: Option[String] = None
) extends JsonSerializeable {

  def toJson = Json.toJson(this)(Person.writes)
}

object Person {
  implicit val reads  = Json.reads[Person]
  implicit val writes = Json.writes[Person]
}