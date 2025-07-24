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
import controllers.adminServices.routes as adminServicesRoutes
import models.*
import pages.*
import pages.adminServices.{AccessProductionAdminFrontendPage, NoPublicRoutePage, StrideOrInternalAuthPage, StrideOrVPNPage}

class AdminServicesNavigatorSpec extends SpecBase {

  val navigator = new AdminServicesNavigator

  "AdminServicesNavigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad()
      }

      "must go from the No Public Routes page to Stride or VPN page" in {
        navigator.nextPage(NoPublicRoutePage, NormalMode, UserAnswers("id")) mustBe adminServicesRoutes.StrideOrVPNController.onPageLoad(NormalMode)
      }
      "must go from the Stride or VPN page to Stride or Internal Auth page" in {
        navigator.nextPage(StrideOrVPNPage, NormalMode, UserAnswers("id")) mustBe adminServicesRoutes.StrideOrInternalAuthController.onPageLoad(NormalMode)
      }
      "must go from Stride or Internal Auth page to Access Prod Admin Frontend Page" in {
        navigator.nextPage(StrideOrInternalAuthPage, NormalMode, UserAnswers("id")) mustBe adminServicesRoutes.AccessProductionAdminFrontendController.onPageLoad(NormalMode)
      }
      "must go from Access Prod Admin Frontend page to Check Your Answers Page" in {
        navigator.nextPage(AccessProductionAdminFrontendPage, NormalMode, UserAnswers("id")) mustBe adminServicesRoutes.CheckYourAnswersController.onPageLoad()
      }
    }


    "in Check mode" - {

      "must go from the No Public Route page to the Stride or VPN page WHEN Stride or VPN Page does not have an Answer" in {
        navigator.nextPage(NoPublicRoutePage, CheckMode, UserAnswers("id")) mustBe adminServicesRoutes.StrideOrVPNController.onPageLoad(CheckMode)
      }
      "must go from the No Public Routes page to the Check Your Answers page WHEN Stride or VPN page has an Answer" in {
        UserAnswers("id").set(StrideOrVPNPage, true).foreach { userAnswers =>
          navigator.nextPage(NoPublicRoutePage, CheckMode, userAnswers) mustBe adminServicesRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Stride or VPN page to the Stride or Internal Auth page WHEN Stride or Internal Auth Page does not have an Answer" in {
        navigator.nextPage(StrideOrVPNPage, CheckMode, UserAnswers("id")) mustBe adminServicesRoutes.StrideOrInternalAuthController.onPageLoad(CheckMode)
      }
      "must go from the Stride or VPN page to the Check Your Answers page WHEN Protected Microservices Auth page has an Answer" in {
        UserAnswers("id").set(StrideOrInternalAuthPage, true).foreach { userAnswers =>
          navigator.nextPage(StrideOrVPNPage, CheckMode, userAnswers) mustBe adminServicesRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Stride or Internal Auth page to the Access Prod Admin Frontend page WHEN Access Prod Admin Frontend Page does not have an Answer" in {
        navigator.nextPage(StrideOrInternalAuthPage, CheckMode, UserAnswers("id")) mustBe adminServicesRoutes.AccessProductionAdminFrontendController.onPageLoad(CheckMode)
      }
      "must go from the Stride or Internal Auth page to the Check Your Answers page WHEN Access Prod Admin Frontend page has an Answer" in {
        UserAnswers("id").set(AccessProductionAdminFrontendPage, true).foreach { userAnswers =>
          navigator.nextPage(StrideOrInternalAuthPage, CheckMode, userAnswers) mustBe adminServicesRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      
      "must go from Access Prod Admin Frontend page to Check Your Answers Page" in {
        navigator.nextPage(AccessProductionAdminFrontendPage, CheckMode, UserAnswers("id")) mustBe adminServicesRoutes.CheckYourAnswersController.onPageLoad()
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe adminServicesRoutes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}

