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

package navigation

import base.SpecBase
import controllers.routes
import pages._
import models._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad()
      }

      "must go from the Index page to the Does Include Non-standard Pattern page" in {
        navigator.nextPage(IndexPage, NormalMode, UserAnswers("id")) mustBe routes.ServiceURLController.onPageLoad(NormalMode)
      }

      "must go from the Service URL page to the Does Include Non-standard Pattern page" in {
        navigator.nextPage(ServiceURLPage, NormalMode, UserAnswers("id")) mustBe routes.DoesNonstandardPatternController.onPageLoad(NormalMode)
      }

      "NORMALMODE must go from the Does Include Non-standard Pattern page to the Which Non-standard Pattern page WHEN answer is YES" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach{userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, NormalMode, userAnswers) mustBe routes.NonstandardPatternController.onPageLoad(NormalMode)
        }
      }

      "NORMALMODE must go from the Does Include Non-standard Pattern Page to Break Bobby Rules page WHEN answer is NO" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, false).foreach{userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, NormalMode, userAnswers) mustBe routes.BreakBobbyRulesController.onPageLoad(NormalMode)
        }
      }

      "must go from the Which Non-standard Pattern page to the Bobby Rules page" in {
        navigator.nextPage(NonstandardPatternPage, NormalMode, UserAnswers("id")) mustBe routes.BreakBobbyRulesController.onPageLoad(NormalMode)
      }

      "must go from the Bobby Rules page to the Deprecated HMRC Libraries page" in {
        navigator.nextPage(BreakBobbyRulesPage, NormalMode, UserAnswers("id")) mustBe routes.DeprecatedLibrariesController.onPageLoad(NormalMode)
      }

      "must go from the Deprecated HMRC Libraries page to the Using HTTP Verbs page" in {
        navigator.nextPage(DeprecatedLibrariesPage, NormalMode, UserAnswers("id")) mustBe routes.UsingHTTPVerbsController.onPageLoad(NormalMode)
      }

      "must go from the HTTP Verbs page to the ReadME Up-To-Date and fit for purpose page" in {
        navigator.nextPage(UsingHTTPVerbsPage, NormalMode, UserAnswers("id")) mustBe routes.ReadMeFitForPurposeController.onPageLoad(NormalMode)
      }

      "must go from the ReadME Up-To-Date and fit for purpose page to the CheckYourAnswers page" in {
        navigator.nextPage(ReadMeFitForPurposePage, NormalMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" - {

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe routes.CheckYourAnswersController.onPageLoad()
      }

      "CHECKMODE must go from the Does Include Non-standard Pattern Page to Which Non-standard Patterns page WHEN answer is YES" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe routes.NonstandardPatternController.onPageLoad(CheckMode)
        }
      }

      "CHECKMODE must go from the Does Include Non-standard Pattern Page to CheckYourAnswers page WHEN answer is NO" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, false).foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
        }
      }
    }
  }
}
