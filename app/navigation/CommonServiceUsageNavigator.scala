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

import controllers.commonServiceUsage.routes as commonServiceUsageRoutes
import controllers.routes
import models.*
import pages.*
import pages.commonServiceUsage.*
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class CommonServiceUsageNavigator @Inject() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case IntegrationCheckPage => _ => commonServiceUsageRoutes.NotifyDependantServicesController.onPageLoad(NormalMode)

    case NotifyDependantServicesPage => _ => commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case IntegrationCheckPage => userAnswers =>
      if (userAnswers.get(NotifyDependantServicesPage).isEmpty) {
        commonServiceUsageRoutes.NotifyDependantServicesController.onPageLoad(CheckMode)
      } else {
        commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
      }
    
    case _ => _ => commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
