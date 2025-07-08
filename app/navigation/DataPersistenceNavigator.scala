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

import controllers.dataPersistence.routes as dataPersistenceRoutes
import controllers.routes
import controllers.security.routes as securityRoutes
import models.*
import pages.*
import pages.dataPersistence.*
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class DataPersistenceNavigator @Inject()() {

  private val normalRoutes: Page => UserAnswers => Call = {

    case UsingMongoPage => userAnswers =>
      userAnswers.get(UsingMongoPage) match {
        case Some(true) => dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)
        case _ => dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)
      }

    case ResilientRecycleMongoPage => _ => dataPersistenceRoutes.PublicMongoTTLController.onPageLoad(NormalMode)

    case PublicMongoTTLPage => _ => dataPersistenceRoutes.FieldLevelEncryptionController.onPageLoad(NormalMode)

    case FieldLevelEncryptionPage => _ => dataPersistenceRoutes.ProtectedMongoTTLController.onPageLoad(NormalMode)

    case ProtectedMongoTTLPage => _ => dataPersistenceRoutes.MongoTestedWithIndexingController.onPageLoad(NormalMode)

    case MongoTestedWithIndexingPage => _ => dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)

    case UsingObjectStorePage => userAnswers =>
      userAnswers.get(UsingObjectStorePage) match {
        case Some(true) => dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(NormalMode)
        case _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case CorrectRetentionPeriodPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case UsingMongoPage => userAnswers =>
      userAnswers.get(UsingMongoPage) match {
        case Some(true) => dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)
        case _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }

    case ResilientRecycleMongoPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case PublicMongoTTLPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case FieldLevelEncryptionPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case ProtectedMongoTTLPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case MongoTestedWithIndexingPage => _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()

    case UsingObjectStorePage => userAnswers =>
      userAnswers.get(UsingObjectStorePage) match {
        case Some(true) => dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(CheckMode)
        case _ => dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
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
