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

import controllers.buildResilience.routes as buildRoutes
import controllers.dataPersistence.routes as dataRoutes
import controllers.routes
import models.*
import pages.*
import pages.buildResilience.{AppropriateTimeoutsPage, BreakBobbyRulesPage, DeprecatedLibrariesPage, DoesNonstandardPatternPage, NonstandardPatternPage, ReadMeFitForPurposePage, ServiceURLPage, UsingHTTPVerbsPage}
import pages.dataPersistence.{CorrectRetentionPeriodPage, FieldLevelEncryptionPage, MongoTestedWithIndexingPage, ProtectedMongoTTLPage, PublicMongoTTLPage, ResilientRecycleMongoPage, UsingMongoPage, UsingObjectStorePage}
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class Navigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {
    /*######################################################
    
                       BUILD & RESILIENCE
    
    #######################################################*/

    case IndexPage => _ => buildRoutes.ServiceURLController.onPageLoad(NormalMode)

    case ServiceURLPage => _ => buildRoutes.DoesNonstandardPatternController.onPageLoad(NormalMode)

    case DoesNonstandardPatternPage => userAnswers =>
      userAnswers.get(DoesNonstandardPatternPage) match {
        case Some(true) => buildRoutes.NonstandardPatternController.onPageLoad(NormalMode)
        case _ => buildRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)
      }

    case NonstandardPatternPage => _ => buildRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)

    case BreakBobbyRulesPage => _ => buildRoutes.DeprecatedLibrariesController.onPageLoad(NormalMode)

    case DeprecatedLibrariesPage => _ => buildRoutes.UsingHTTPVerbsController.onPageLoad(NormalMode)

    case UsingHTTPVerbsPage => _ => buildRoutes.ReadMeFitForPurposeController.onPageLoad(NormalMode)

    case ReadMeFitForPurposePage => _ => buildRoutes.AppropriateTimeoutsController.onPageLoad(NormalMode)

    case AppropriateTimeoutsPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    /*######################################################

                      DATA PERSISTENCE

    #######################################################*/

    case UsingMongoPage => _ => dataRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)

    case ResilientRecycleMongoPage => _ => dataRoutes.PublicMongoTTLController.onPageLoad(NormalMode)

    case PublicMongoTTLPage => _ => dataRoutes.FieldLevelEncryptionController.onPageLoad(NormalMode)

    case FieldLevelEncryptionPage => _ => dataRoutes.ProtectedMongoTTLController.onPageLoad(NormalMode)

    case ProtectedMongoTTLPage => _ => dataRoutes.MongoTestedWithIndexingController.onPageLoad(NormalMode)

    case MongoTestedWithIndexingPage => _ => dataRoutes.UsingObjectStoreController.onPageLoad(NormalMode)

    case UsingObjectStorePage => _ => dataRoutes.CorrectRetentionPeriodController.onPageLoad(NormalMode)

    case CorrectRetentionPeriodPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    /*######################################################
    
                       BUILD & RESILIENCE
    
    #######################################################*/

    case ServiceURLPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()
    
    case DoesNonstandardPatternPage => userAnswers =>
      userAnswers.get(DoesNonstandardPatternPage) match {
        case Some(true) => buildRoutes.NonstandardPatternController.onPageLoad(CheckMode)
        case _ => buildRoutes.CheckYourAnswersController.onPageLoad()
      }

    case NonstandardPatternPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    case BreakBobbyRulesPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    case DeprecatedLibrariesPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    case UsingHTTPVerbsPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    case ReadMeFitForPurposePage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    case AppropriateTimeoutsPage => _ => buildRoutes.CheckYourAnswersController.onPageLoad()

    /*######################################################

                      DATA PERSISTENCE

    #######################################################*/

    case UsingMongoPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case ResilientRecycleMongoPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case PublicMongoTTLPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case FieldLevelEncryptionPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case ProtectedMongoTTLPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case MongoTestedWithIndexingPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case UsingObjectStorePage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case CorrectRetentionPeriodPage => _ => dataRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => dataRoutes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
