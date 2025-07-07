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
import controllers.dataPersistence.routes as dataPersistenceRoutes
import controllers.security.routes as securityRoutes
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

    /*######################################################

                      DATA PERSISTENCE

    #######################################################*/

    case UsingMongoPage => _ => dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)

    case ResilientRecycleMongoPage => _ => dataPersistenceRoutes.PublicMongoTTLController.onPageLoad(NormalMode)

    case PublicMongoTTLPage => _ => dataPersistenceRoutes.FieldLevelEncryptionController.onPageLoad(NormalMode)

    case FieldLevelEncryptionPage => _ => dataPersistenceRoutes.ProtectedMongoTTLController.onPageLoad(NormalMode)

    case ProtectedMongoTTLPage => _ => dataPersistenceRoutes.MongoTestedWithIndexingController.onPageLoad(NormalMode)

    case MongoTestedWithIndexingPage => _ => dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)

    case UsingObjectStorePage => _ => dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(NormalMode)

    case CorrectRetentionPeriodPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    /*######################################################
    
                       BUILD & RESILIENCE
    
    #######################################################*/

    case ServiceURLPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
    
    case DoesNonstandardPatternPage => userAnswers =>
      userAnswers.get(DoesNonstandardPatternPage) match {
        case Some(true) => buildResilienceRoutes.NonstandardPatternController.onPageLoad(CheckMode)
        case _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case NonstandardPatternPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    case BreakBobbyRulesPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    case DeprecatedLibrariesPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    case UsingHTTPVerbsPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    case ReadMeFitForPurposePage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    case AppropriateTimeoutsPage => _ => buildResilienceRoutes.CheckYourAnswersController.onPageLoad()

    /*######################################################

                      DATA PERSISTENCE

    #######################################################*/

    case UsingMongoPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case ResilientRecycleMongoPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case PublicMongoTTLPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case FieldLevelEncryptionPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case ProtectedMongoTTLPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case MongoTestedWithIndexingPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case UsingObjectStorePage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case CorrectRetentionPeriodPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => securityRoutes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
