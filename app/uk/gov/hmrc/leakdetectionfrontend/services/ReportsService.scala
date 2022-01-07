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

package uk.gov.hmrc.leakdetectionfrontend.services

import uk.gov.hmrc.leakdetectionfrontend.connectors.LeakDetectionConnector
import uk.gov.hmrc.leakdetectionfrontend.models.Report

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReportsService @Inject()(ldsConnector: LeakDetectionConnector)(implicit ec: ExecutionContext) {

  def getRepositories: Future[Seq[String]] = ldsConnector.getRepositories

  def getLatestReportsForEachBranch(repository: String): Future[Seq[Report]] = ldsConnector.getLatestReportsForEachBranch(repository)

  def getLatestReportForDefaultBranch(repository: String): Future[Option[Report]] = ldsConnector.getLatestReportForDefaultBranch(repository)

  def getReport(reportId: String): Future[Option[Report]] = ldsConnector.getReport(reportId)

}
