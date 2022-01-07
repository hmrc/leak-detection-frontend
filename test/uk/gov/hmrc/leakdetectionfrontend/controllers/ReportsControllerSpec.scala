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

import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.internalauth.client.Retrieval
import uk.gov.hmrc.leakdetectionfrontend.services.ReportsService
import uk.gov.hmrc.leakdetectionfrontend.views.html.{Main, RepoList, ReportsForRepo, SingleReport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.internalauth.client.test.{FrontendAuthComponentsStub, StubBehaviour}

class ReportsControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "Reports list" should {

    "forward to internal-auth-frontend when not logged in" in {
      val mockedReportsService = mock[ReportsService]
      val authStubBehaviour = mock[StubBehaviour]
      val authComponent = {
        implicit val cc = stubMessagesControllerComponents()
        FrontendAuthComponentsStub(authStubBehaviour)
      }
      val mainTemplate = new Main()
      val controller = new ReportsController(config = null,
        reportsService = mockedReportsService,
        auth = authComponent,
        repo_list = new RepoList(mainTemplate),
        reports_for_repo = new ReportsForRepo(mainTemplate),
        report = new SingleReport(mainTemplate),
        mcc = stubMessagesControllerComponents())
      val request = FakeRequest().withHeaders("Accept" -> "application/json")
      val repos = List("repo1", "repo2")
      when(mockedReportsService.getRepositories).thenReturn(Future(repos))

      val result = controller.repositories(request)

      Helpers.status(result) shouldBe 303
      Helpers.header("Location", result) shouldBe Some("/internal-auth-frontend/sign-in?continue_url=%2Freports%2Frepositories")
    }


    "shows a list of outstanding reports when logged in" in {
      val mockedReportsService = mock[ReportsService]
      val authStubBehaviour = mock[StubBehaviour]
      val authComponent = {
        implicit val cc = stubMessagesControllerComponents()
        FrontendAuthComponentsStub(authStubBehaviour)
      }
      val mainTemplate = new Main()
      val controller = new ReportsController(config = null,
        reportsService = mockedReportsService,
        auth = authComponent,
        repo_list = new RepoList(mainTemplate),
        reports_for_repo = new ReportsForRepo(mainTemplate),
        report = new SingleReport(mainTemplate),
        mcc = stubMessagesControllerComponents())

      val request = FakeRequest().withSession(SessionKeys.authToken -> "Token token")
      when(authStubBehaviour.stubAuth(None, Retrieval.EmptyRetrieval)).thenReturn(Future.unit)

      val repos = List("repo1", "repo2")
      when(mockedReportsService.getRepositories).thenReturn(Future(repos))

      when(
        authStubBehaviour.stubAuth(
          None,
          Retrieval.EmptyRetrieval
        )
      ).thenReturn(Future.unit)


      val result = controller.repositories(request)
      Helpers.status(result) shouldBe 200

    }
  }
}
