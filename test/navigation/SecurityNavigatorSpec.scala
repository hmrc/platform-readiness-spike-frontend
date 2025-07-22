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
import controllers.security.routes as securityRoutes
import models.*
import pages.*
import pages.security.*

class SecurityNavigatorSpec extends SpecBase {

  val navigator = new SecurityNavigator

  "SecurityNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad()
      }

      "must go from the Frontend Authentication page to Public Microservice Auth page" in {
        navigator.nextPage(FrontendAuthenticationPage, NormalMode, UserAnswers("id")) mustBe securityRoutes.PublicMicroserviceAuthController.onPageLoad(NormalMode)
      }
      "must go from the Public Microservice Auth page to Protected Microservice Auth page" in {
        navigator.nextPage(PublicMicroserviceAuthPage, NormalMode, UserAnswers("id")) mustBe securityRoutes.ProtectedMicroserviceAuthController.onPageLoad(NormalMode)
      }
      "must go from Protected Microservice Auth page to Check Your Answers Page" in {
        navigator.nextPage(ProtectedMicroserviceAuthPage, NormalMode, UserAnswers("id")) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
      }
    }


    "in Check mode" - {

      "must go from the Frontend Authentication page to the Public Microservices Auth page WHEN Public Microservices Auth Page does not have an Answer" in {
        navigator.nextPage(FrontendAuthenticationPage, CheckMode, UserAnswers("id")) mustBe securityRoutes.PublicMicroserviceAuthController.onPageLoad(CheckMode)
      }
      "must go from the Frontend Authentication page to the Check Your Answers page WHEN Public Microservices Auth page has an Answer" in {
        UserAnswers("id").set(PublicMicroserviceAuthPage, true).foreach { userAnswers =>
          navigator.nextPage(FrontendAuthenticationPage, CheckMode, userAnswers) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Public Microservices Auth page to the Protected Microservices Auth page WHEN Protected Microservices Auth Page does not have an Answer" in {
        navigator.nextPage(PublicMicroserviceAuthPage, CheckMode, UserAnswers("id")) mustBe securityRoutes.ProtectedMicroserviceAuthController.onPageLoad(CheckMode)
      }
      "must go from the Public Microservice Auth page to the Check Your Answers page WHEN Protected Microservices Auth page has an Answer" in {
        UserAnswers("id").set(ProtectedMicroserviceAuthPage, true).foreach { userAnswers =>
          navigator.nextPage(PublicMicroserviceAuthPage, CheckMode, userAnswers) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      
      "must go from Protected Microservice Auth page to Check Your Answers Page" in {
        navigator.nextPage(ProtectedMicroserviceAuthPage, CheckMode, UserAnswers("id")) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}

