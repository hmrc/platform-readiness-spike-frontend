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
import controllers.commonServiceUsage.routes as commonServiceUsageRoutes
import controllers.routes
import models.*
import pages.*
import pages.commonServiceUsage.{IntegrationCheckPage, NotifyDependantServicesPage}

class CommonServiceUsageNavigatorSpec extends SpecBase {

  val navigator = new CommonServiceUsageNavigator

  "Navigator" - {

    "in Normal mode" - {
      
      "must go from the Integration Check page to Notify Dependant Services page" in {
        navigator.nextPage(IntegrationCheckPage, NormalMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.NotifyDependantServicesController.onPageLoad(NormalMode)
      }
      "must go from the Notify Dependant Services page to Check Your Answers page" in {
        navigator.nextPage(NotifyDependantServicesPage, NormalMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" - {
      
      "must go from the Integration Check page to Check Your Answers page" in {
        navigator.nextPage(IntegrationCheckPage, CheckMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Notify Dependant Services page to Check Your Answers page" in {
        navigator.nextPage(NotifyDependantServicesPage, CheckMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
    }
  } 
}

