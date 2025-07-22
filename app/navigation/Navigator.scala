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

import controllers.buildResilience.routes as buildResilienceRoutes
import controllers.routes
import models.*
import pages.*
import pages.buildResilience.*
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case IndexPage => _ => buildResilienceRoutes.ServiceURLController.onPageLoad(NormalMode)

    case ServiceURLPage => _ => buildResilienceRoutes.DoesNonstandardPatternController.onPageLoad(NormalMode)

    case DoesNonstandardPatternPage => userAnswers =>
      userAnswers.get(DoesNonstandardPatternPage) match {
        case Some(true) => buildResilienceRoutes.NonstandardPatternController.onPageLoad(NormalMode)
        case _ => buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)
      }

    case NonstandardPatternPage => _ => buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)

    case BreakBobbyRulesPage => _ => buildResilienceRoutes.DeprecatedLibrariesController.onPageLoad(NormalMode)

    case DeprecatedLibrariesPage => _ => buildResilienceRoutes.UsingHTTPVerbsController.onPageLoad(NormalMode)

    case UsingHTTPVerbsPage => _ => buildResilienceRoutes.ReadMeFitForPurposeController.onPageLoad(NormalMode)

    case ReadMeFitForPurposePage => _ => buildResilienceRoutes.AppropriateTimeoutsController.onPageLoad(NormalMode)

    case AppropriateTimeoutsPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case ServiceURLPage => userAnswers =>
      if (userAnswers.get(DoesNonstandardPatternPage).isEmpty) {
        buildResilienceRoutes.DoesNonstandardPatternController.onPageLoad(CheckMode)
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case DoesNonstandardPatternPage => userAnswers =>
      if (userAnswers.get(NonstandardPatternPage).isEmpty) {
        if (userAnswers.get(DoesNonstandardPatternPage).contains(true)) {
          buildResilienceRoutes.NonstandardPatternController.onPageLoad(CheckMode)
        } else {
          buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case NonstandardPatternPage => userAnswers =>
      if (userAnswers.get(BreakBobbyRulesPage).isEmpty) {
        buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(CheckMode)
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case BreakBobbyRulesPage => userAnswers =>
      if (userAnswers.get(DeprecatedLibrariesPage).isEmpty) {
        buildResilienceRoutes.DeprecatedLibrariesController.onPageLoad(CheckMode)
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case DeprecatedLibrariesPage => userAnswers =>
      if (userAnswers.get(UsingHTTPVerbsPage).isEmpty) {
        buildResilienceRoutes.UsingHTTPVerbsController.onPageLoad(CheckMode)
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case UsingHTTPVerbsPage => userAnswers =>
      if (userAnswers.get(ReadMeFitForPurposePage).isEmpty) {
        buildResilienceRoutes.ReadMeFitForPurposeController.onPageLoad(CheckMode)
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case ReadMeFitForPurposePage => userAnswers =>
      if (userAnswers.get(AppropriateTimeoutsPage).isEmpty) {
        buildResilienceRoutes.AppropriateTimeoutsController.onPageLoad(CheckMode)
      } else {
        buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }
    
    case _ => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
