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

package uk.gov.hmrc.leakdetectionfrontend.controllers

import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.leakdetectionfrontend.config.AppConfig
import uk.gov.hmrc.leakdetectionfrontend.connectors.LeakDetectionConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RedirectController @Inject()(config: AppConfig,
                                   leakDetectionConnector: LeakDetectionConnector,
                                   cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends FrontendController(cc){

  def sendToCatalogueRepositories() = Action {
    Redirect(url"${config.catalogueUrl}/leak-detection/repositories".toString)
  }


  def sendToCatalogueRepository(repoName:String) = Action {
    Redirect(url"${config.catalogueUrl}/leak-detection/repositories/$repoName".toString)
  }

  def sendToCatalogueReport(reportId:String) = Action.async { implicit request =>
    for {
      report    <- leakDetectionConnector.getReport(reportId)
      repoName   = report.map(_.repoName)
      branchName = report.map(_.branch)
      url        = repoName.map(repo => url"${config.catalogueUrl}/leak-detection/repositories/$repo/${branchName.getOrElse("main")}".toString)
      fallback   = url"${config.catalogueUrl}/leak-detection/repositories".toString
    } yield Redirect(url.getOrElse(fallback))
  }
}
