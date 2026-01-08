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

import base.{FakeQuestionConnector, SpecBase}
import config.QuestionStructure
import connectors.QuestionConnector
import forms.QuestionFormProvider
import models.QuestionResponse
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.QuestionView
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{eq => eqTo}
import uk.gov.hmrc.internalauth.client.Retrieval
import uk.gov.hmrc.internalauth.client.Retrieval.Username
import scala.concurrent.Future

class QuestionControllerSpec extends SpecBase {

  private val sectionName = QuestionStructure.BuildAndResilience.name
  private val questionName = "nonstandard-pattern"

  "Question Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder()
        .overrides(
          bind[QuestionConnector].toInstance(new FakeQuestionConnector(QuestionResponse("some-frontend", Seq()))),
        ).build()

      running(application) {

        when(mockStubBehaviour.stubAuth(eqTo(None), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))

        val formProvider = application.injector.instanceOf[QuestionFormProvider]
        val form = formProvider("some-frontend", questionName, "User", "User")
        val request = FakeRequest(GET, routes.QuestionController.onPageLoad("some-frontend", sectionName, questionName).url)
          .withSession("authToken" -> "Token some-token")

        val result = route(application, request).value

        val view = application.injector.instanceOf[QuestionView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view("some-frontend", sectionName, questionName, form)(request, messages(application)).toString
      }
    }
  }
}
