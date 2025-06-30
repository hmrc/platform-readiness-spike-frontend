/*
 * Copyright 2023 HM Revenue & Customs
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

package views

import base.ViewSpecBase
import matchers.ViewMatchers
import models.NormalMode
import play.twirl.api.Html
import views.html.IndexView

class IndexViewSpec extends ViewSpecBase with ViewMatchers {

  val page: IndexView = inject[IndexView]

  "view" should {

    def createView: Html =
      page(controllers.buildResilience.routes.ServiceURLController.onPageLoad(NormalMode).url)(request, messages)

    val view = createView

    "have title" in {
      view.select("title").text() must include(messages("index.title"))
    }

    "contain heading" in {
      view.getElementsByClass("govuk-heading-xl").text() mustBe messages("index.heading")
    }

    "display the start now button when full disclosure journey enabled is true" in {
      view.getElementsByClass("govuk-button").first() must haveId("start")
      view.getElementsByClass("govuk-button").text() mustBe messages("site.continue")
      view
        .getElementById("start")
        .attr("href") mustBe controllers.buildResilience.routes.ServiceURLController.onPageLoad(NormalMode).url
    }
  }  
}
