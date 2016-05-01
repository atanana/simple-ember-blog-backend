package utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

object JsonDate {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }

  val jodaDateFormat: Format[DateTime] = Format(jodaDateReads, jodaDateWrites)
}
