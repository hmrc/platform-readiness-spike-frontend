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
import models.NormalMode
import navigation.{FakeNavigator, Navigator}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.IndexView

class IndexControllerSpec extends SpecBase {

  "Index Controller" - {

    "must return OK and the correct view for a GET" in {

      val navigator = new FakeNavigator(buildResilience.routes.ServiceURLController.onPageLoad(NormalMode))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[Navigator].toInstance(navigator))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.IndexController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(buildResilience.routes.ServiceURLController.onPageLoad(NormalMode).url)(request, messages(application)).toString
      }
    }
  }
}
