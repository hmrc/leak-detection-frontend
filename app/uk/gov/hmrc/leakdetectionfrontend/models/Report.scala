/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.leakdetectionfrontend.models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import java.time.Instant


final case class Report(
  id               : String,
  repoName         : String,
  repoUrl          : String,
  commitId         : String,
  branch           : String,
  timestamp        : Instant,
  author           : String,
)

object Report {

  val apiFormat: Format[Report] = {
    // default Instant Reads is fine, but we want Writes to include .SSS even when 000
    val instantFormatter =
      java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(java.time.ZoneOffset.UTC)

    implicit val instantWrites: Writes[Instant] =
      (instant: Instant) => JsString(instantFormatter.format(instant))

    reportFormat
  }


  private def reportFormat(implicit instantFormat: Format[Instant]): OFormat[Report] = {
    ( (__ \ "_id"              ).format[String]
    ~ (__ \ "repoName"         ).format[String]
    ~ (__ \ "repoUrl"          ).format[String]
    ~ (__ \ "commitId"         ).format[String]
    ~ (__ \ "branch"           ).format[String]
    ~ (__ \ "timestamp"        ).format[Instant]
    ~ (__ \ "author"           ).format[String]
    )(Report.apply, unlift(Report.unapply))
  }
}





