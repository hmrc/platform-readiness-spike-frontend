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
import controllers.buildResilience.routes as buildResilienceRoutes
import models.*
import pages.*
import pages.buildResilience.*
class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad()
      }

      "must go from the Index page to the Does Include Non-standard Pattern page" in {
        navigator.nextPage(IndexPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.ServiceURLController.onPageLoad(NormalMode)
      }

      "must go from the Service URL page to the Does Include Non-standard Pattern page" in {
        navigator.nextPage(ServiceURLPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.DoesNonstandardPatternController.onPageLoad(NormalMode)
      }

      "must go from the Does Include Non-standard Pattern page to the Which Non-standard Pattern page WHEN answer is YES" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, NormalMode, userAnswers) mustBe buildResilienceRoutes.NonstandardPatternController.onPageLoad(NormalMode)
        }
      }

      "must go from the Does Include Non-standard Pattern Page to Break Bobby Rules page WHEN answer is NO" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, false).foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, NormalMode, userAnswers) mustBe buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)
        }
      }

      "must go from the Which Non-standard Pattern page to the Bobby Rules page" in {
        navigator.nextPage(NonstandardPatternPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)
      }

      "must go from the Bobby Rules page to the Deprecated HMRC Libraries page" in {
        navigator.nextPage(BreakBobbyRulesPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.DeprecatedLibrariesController.onPageLoad(NormalMode)
      }

      "must go from the Deprecated HMRC Libraries page to the Using HTTP Verbs page" in {
        navigator.nextPage(DeprecatedLibrariesPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.UsingHTTPVerbsController.onPageLoad(NormalMode)
      }

      "must go from the HTTP Verbs page to the ReadME Up-To-Date and fit for purpose page" in {
        navigator.nextPage(UsingHTTPVerbsPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.ReadMeFitForPurposeController.onPageLoad(NormalMode)
      }

      "must go from the ReadME Up-To-Date and fit for purpose page to the Appropriate Timeouts page" in {
        navigator.nextPage(ReadMeFitForPurposePage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.AppropriateTimeoutsController.onPageLoad(NormalMode)
      }

      "must go from the Appropriate Timeouts page to the Check Your Answers page" in {
        navigator.nextPage(AppropriateTimeoutsPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" - {
      
      "must go from the Service URL page to the Does Nonstandard Pattern page WHEN Does Nonstandard Pattern Page does not have an Answer" in {
        navigator.nextPage(ServiceURLPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.DoesNonstandardPatternController.onPageLoad(CheckMode)
      }
      "must go from the Service URL page to the Check Your Answers page WHEN Does Nonstandard Pattern has an Answer" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach { userAnswers =>
          navigator.nextPage(ServiceURLPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Does Include Non-standard Pattern Page to Which Non-standard Patterns page WHEN answer is YES AND Non Standard Pattern Page does not have an Answer" in {
        UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.NonstandardPatternController.onPageLoad(CheckMode)
        }
      }
      "must go from the Does Include Non-standard Pattern Page to Check Your Answers page WHEN answer is Yes AND Non Standard Pattern Page has an Answer " in {
        val updatedUserAnswers = for {
          withDoesNonstandard <- UserAnswers("id").set(DoesNonstandardPatternPage, true)
          withNonstandard <- withDoesNonstandard.set(NonstandardPatternPage, "true")
        } yield withNonstandard

        updatedUserAnswers.foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      "must go from the Using Mongo Page to Check Your Answers page WHEN answer is NO AND Non Standard Pattern Page has an Answer" in {
        val updatedUserAnswers = for {
          withDoesNonstandard <- UserAnswers("id").set(DoesNonstandardPatternPage, false)
          withNonstandard <- withDoesNonstandard.set(NonstandardPatternPage, "true")
        } yield withNonstandard

        updatedUserAnswers.foreach { userAnswers =>
          navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Which Non-standard Pattern page to the Break Bobby Rules page WHEN Break Bobby Rules Page does not have an Answer" in {
        navigator.nextPage(NonstandardPatternPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(CheckMode)
      }
      "must go from the Which Non-standard Pattern page to the Check Your Answers page WHEN Break Bobby Rules Page has an Answer" in {
        UserAnswers("id").set(BreakBobbyRulesPage, true).foreach { userAnswers =>
          navigator.nextPage(NonstandardPatternPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Break Bobby Rules page to the Deprecated HMRC Libraries page WHEN Deprecated HMRC Libraries Page does not have an Answer" in {
        navigator.nextPage(BreakBobbyRulesPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.DeprecatedLibrariesController.onPageLoad(CheckMode)
      }
      "must go from the Which Non-standard Pattern page to the Check Your Answers page WHEN Deprecated HMRC Libraries Page has an Answer" in {
        UserAnswers("id").set(DeprecatedLibrariesPage, true).foreach { userAnswers =>
          navigator.nextPage(BreakBobbyRulesPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Deprecated HMRC Libraries page to the HTTP Verbs page WHEN HTTP Verbs Page does not have an Answer" in {
        navigator.nextPage(DeprecatedLibrariesPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.UsingHTTPVerbsController.onPageLoad(CheckMode)
      }
      "must go from the Deprecated HMRC Libraries page to the Check Your Answers page WHEN HTTP Verbs Page has an Answer" in {
        UserAnswers("id").set(UsingHTTPVerbsPage, true).foreach { userAnswers =>
          navigator.nextPage(DeprecatedLibrariesPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the HTTP Verbs page to the ReadME Up-To-Date page WHEN ReadME Up-To-Date Page does not have an Answer" in {
        navigator.nextPage(UsingHTTPVerbsPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.ReadMeFitForPurposeController.onPageLoad(CheckMode)
      }
      "must go from the HTTP Verbs page to the Check Your Answers page WHEN ReadME Up-To-Date Page has an Answer" in {
        UserAnswers("id").set(ReadMeFitForPurposePage, true).foreach { userAnswers =>
          navigator.nextPage(UsingHTTPVerbsPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the ReadME Up-To-Date page to the Appropriate Timeouts page WHEN Appropriate Timeouts Page does not have an Answer" in {
        navigator.nextPage(ReadMeFitForPurposePage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.AppropriateTimeoutsController.onPageLoad(CheckMode)
      }
      "must go from the ReadME Up-To-Date page to the Check Your Answers page WHEN ReadME Up-To-Date Page has an Answer" in {
        UserAnswers("id").set(AppropriateTimeoutsPage, true).foreach { userAnswers =>
          navigator.nextPage(ReadMeFitForPurposePage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Appropriate Timeouts page to the Check Your Answers page" in {
        navigator.nextPage(AppropriateTimeoutsPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
    }
  }
}