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

package uk.gov.hmrc.leakdetectionfrontend.controllers

import com.google.inject.Inject

import javax.inject.Singleton
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.leakdetectionfrontend.config.AppConfig
import uk.gov.hmrc.leakdetectionfrontend.models.Report
import uk.gov.hmrc.leakdetectionfrontend.services.ReportsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.leakdetectionfrontend.controllers.{routes => appRoutes}

import scala.concurrent.ExecutionContext

@Singleton
class ReportsController @Inject()(config          : AppConfig,
                                  reportsService  : ReportsService,
                                  repo_list       : uk.gov.hmrc.leakdetectionfrontend.views.html.RepoList,
                                  reports_for_repo: uk.gov.hmrc.leakdetectionfrontend.views.html.ReportsForRepo,
                                  report          : uk.gov.hmrc.leakdetectionfrontend.views.html.SingleReport,
                                  mcc             : MessagesControllerComponents)(
                                  implicit ec: ExecutionContext)
  extends FrontendController(mcc) {

  def repositories: Action[AnyContent] = Action.async { implicit request =>
    reportsService.getRepositories().map { repoNames =>
      render {
        case Accepts.Html() => Ok(repo_list(repoNames.toList))
        case _              => Ok(Json.toJson(repoNames))
      }
    }
  }

  def reportsForRepository(repository: String): Action[AnyContent] = Action.async { implicit request =>
    reportsService.getLatestReportsForEachBranch(repository).map { reports =>
      Ok(reports_for_repo(repository, reports))
    }
  }

  def redirectToRepositories: Action[AnyContent] = Action { implicit request =>
    Redirect(appRoutes.ReportsController.repositories)
  }

  def showReport(reportId: String): Action[AnyContent] = Action.async { implicit request =>
    reportsService.getReport(reportId)
      .map(_
        .map { r =>
          val leakFrequencies =
            r.leakResolution
              .map(_.resolvedLeaks.groupBy(identity).mapValues(_.size))
              .getOrElse(Map())
          Ok(report(r, leakFrequencies, config.leakResolutionUrl))
        }
        .getOrElse(NotFound)
      )
  }
}