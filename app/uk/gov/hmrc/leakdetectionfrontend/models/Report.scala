/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.http.StringContextOps

import java.time.Instant


final case class ResolvedLeak(ruleId: String, description: String)

object ResolvedLeak {
  implicit val format: OFormat[ResolvedLeak] = Json.format[ResolvedLeak]
}

final case class LeakResolution(
  timestamp    : Instant,
  commitId     : String,
  resolvedLeaks: Seq[ResolvedLeak]
)

object LeakResolution {
  def create(reportWithLeaks: Report, cleanReport: Report): LeakResolution = {
    val resolvedLeaks =
      reportWithLeaks.inspectionResults
        .map(reportLine =>
          ResolvedLeak(
            ruleId      = reportLine.ruleId.getOrElse(""),
            description = reportLine.description
          )
        )
    LeakResolution(
      timestamp     = cleanReport.timestamp,
      commitId      = cleanReport.commitId,
      resolvedLeaks = resolvedLeaks
    )
  }
}

final case class Report(
  id               : String,
  repoName         : String,
  repoUrl          : String,
  commitId         : String,
  branch           : String,
  timestamp        : Instant,
  author           : String,
  inspectionResults: Seq[ReportLine],
  leakResolution   : Option[LeakResolution]
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
    implicit val leakResolutionFormat: Format[LeakResolution] =
      ( (__ \ "timestamp"    ).format[Instant]
      ~ (__ \ "commitId"     ).format[String]
      ~ (__ \ "resolvedLeaks").format[Seq[ResolvedLeak]]
      )(LeakResolution.apply, unlift(LeakResolution.unapply))

    ( (__ \ "_id"              ).format[String]
    ~ (__ \ "repoName"         ).format[String]
    ~ (__ \ "repoUrl"          ).format[String]
    ~ (__ \ "commitId"         ).format[String]
    ~ (__ \ "branch"           ).format[String]
    ~ (__ \ "timestamp"        ).format[Instant]
    ~ (__ \ "author"           ).format[String]
    ~ (__ \ "inspectionResults").format[Seq[ReportLine]]
    ~ (__ \ "leakResolution"   ).formatNullable[LeakResolution]
    )(Report.apply, unlift(Report.unapply))
  }
}

final case class Match(start: Int, end: Int) {
  def length: Int = end - start
}

object Match {
   implicit val format: Format[Match] = Json.format[Match]
}

final case class MatchedResult(
                                scope: String,
                                lineText: String,
                                lineNumber: Int,
                                ruleId: String,
                                description: String,
                                matches: List[Match],
                                isTruncated: Boolean = false
                              )

object MatchedResult {
  implicit val format: Format[MatchedResult] = Json.format[MatchedResult]
}

case class Result(filePath: String, scanResults: MatchedResult)

final case class ReportLine(
  filePath   : String,
  scope      : String,
  lineNumber : Int,
  urlToSource: String,
  ruleId     : Option[String],
  description: String,
  lineText   : String,
  matches    : List[Match],
  isTruncated: Option[Boolean]
)

object ReportLine {
  implicit val format: Format[ReportLine] = Json.format[ReportLine]
}