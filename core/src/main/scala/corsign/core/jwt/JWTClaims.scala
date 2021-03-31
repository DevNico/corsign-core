package corsign.core.jwt

import com.nimbusds.jose.shaded.json.JSONObject
import com.nimbusds.jose.util.JSONObjectUtils
import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import corsign.core.model.Payload
import play.api.libs.json.Json

import java.util.{Date, UUID}
import scala.util.{Failure, Success, Try}


case class JWTClaims(
  sub: UUID,
  iss: String,
  aud: String,
  exp: Long,
  nbf: Long,
  iat: Long,
  payload: Payload) {

  def toNimbus =
    new JWTClaimsSet.Builder()
      .issuer(iss)
      .subject(sub.toString)
      .expirationTime(new Date(1000 * exp))
      .notBeforeTime(new Date(1000 * nbf))
      .issueTime(new Date(1000 * iat))
      .audience(aud)
      .claim("pld",
        JSONObjectUtils.parse(payload.toJson.toString()))
      .build();
}


object JWTClaims {

  def fromNimbus(njwt: SignedJWT): JWTClaims = {
    val claims = njwt.getJWTClaimsSet
    val json = Json.parse(Try(new JSONObject(claims.getJSONObjectClaim("pld")).toJSONString) match {
      case Success(value) => value
      case Failure(_) => "{}"
    }).as[Payload]

    JWTClaims(
      UUID.fromString(claims.getSubject),
      claims.getIssuer,
      claims.getAudience.get(0),
      claims.getExpirationTime.getTime,
      claims.getNotBeforeTime.getTime,
      claims.getIssueTime.getTime,
      json
    )
  }

  implicit val reads  = Json.reads[JWTClaims]
  implicit val writes = Json.writes[JWTClaims]
}