/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers

import base.SpecBase
import services.TeamsAndRepositoriesService
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.IndexView
import play.api.i18n.{Messages, MessagesApi}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{eq => eqTo}
import uk.gov.hmrc.internalauth.client.Retrieval
import uk.gov.hmrc.internalauth.client.Retrieval.Username
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "Index Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockStubBehaviour.stubAuth(eqTo(None), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))

      val application = applicationBuilder()
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)
          .withSession("authToken" -> "Token some-token")

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndexView]

        val realMessagesApi: MessagesApi = application.injector.instanceOf[MessagesApi]
        given Messages = realMessagesApi.preferred(request)
        val table = IndexController.tableFromRepos(TeamsAndRepositoriesService.repos.values.toSeq)

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(table)(request, messages(application)).toString
      }
    }
  }
}
