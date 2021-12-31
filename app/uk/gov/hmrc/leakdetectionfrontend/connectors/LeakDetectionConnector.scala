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

package uk.gov.hmrc.leakdetectionfrontend.connectors

import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, StringContextOps}
import uk.gov.hmrc.leakdetectionfrontend.models.Report
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LeakDetectionConnector @Inject()(  http          : HttpClient,
                                         servicesConfig: ServicesConfig,
                                      )(implicit val ec: ExecutionContext) {

  private val baseUrl = servicesConfig.baseUrl("leak-detection")

  private implicit val hc = HeaderCarrier()

  private implicit val rptf = Report.apiFormat

  def getRepositories: Future[Seq[String]] = http.GET[Seq[String]](url"$baseUrl/api/repository")

  def getLatestReportsForEachBranch(repository: String): Future[Seq[Report]] = http.GET[Seq[Report]](url"$baseUrl/api/repository/$repository/latest")

  def getLatestReportForDefaultBranch(repository: String): Future[Option[Report]] = http.GET[Option[Report]](url"$baseUrl/api/repository/$repository/default/latest")

  def getReport(reportId: String): Future[Option[Report]] = http.GET[Option[Report]](url"$baseUrl/api/report/$reportId")


}
