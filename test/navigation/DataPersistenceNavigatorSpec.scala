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
import controllers.dataPersistence.routes as dataPersistenceRoutes
import controllers.security.routes as securityRoutes
import models.*
import pages.*
import pages.dataPersistence.*

class DataPersistenceNavigatorSpec extends SpecBase {

  val navigator = new DataPersistenceNavigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from the Using Mongo page to the Resilient Recycling Mongo page" in {
        navigator.nextPage(UsingMongoPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)
      }
      "must go from the UResilient Recycling Mongo page to the Public Mongo TTL page" in {
        navigator.nextPage(ResilientRecycleMongoPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.PublicMongoTTLController.onPageLoad(NormalMode)
      }
      "must go from the Public Mongo TTL page to the Field Level Encryption page" in {
        navigator.nextPage(PublicMongoTTLPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.FieldLevelEncryptionController.onPageLoad(NormalMode)
      }
      "must go from the Field Level Encryption page to the Protected Mongo TTL page" in {
        navigator.nextPage(FieldLevelEncryptionPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.ProtectedMongoTTLController.onPageLoad(NormalMode)
      }
      "must go from the Protected Mongo TTL page to the Mongo Tested With Indexing page" in {
        navigator.nextPage(ProtectedMongoTTLPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.MongoTestedWithIndexingController.onPageLoad(NormalMode)
      }
      "must go from the Mongo Tested With Indexing page to the Using Object Store page" in {
        navigator.nextPage(MongoTestedWithIndexingPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)
      }
      "must go from the Object Store page to the Correct Retention Period page" in {
        navigator.nextPage(UsingObjectStorePage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(NormalMode)
      }
      "must go from the Correct Retention Period page to the Check Your Answers page" in {
        navigator.nextPage(CorrectRetentionPeriodPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" - {

      "must go from the Using Mongo page to the Check Your Answers page" in {
        navigator.nextPage(UsingMongoPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the UResilient Recycling Mongo page to the Check Your Answers page" in {
        navigator.nextPage(ResilientRecycleMongoPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Public Mongo TTL page to the Check Your Answers page" in {
        navigator.nextPage(PublicMongoTTLPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Field Level Encryption page to the Check Your Answers page" in {
        navigator.nextPage(FieldLevelEncryptionPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Protected Mongo TTL page to the Check Your Answers page" in {
        navigator.nextPage(ProtectedMongoTTLPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Mongo Tested With Indexing page to the Check Your Answers page" in {
        navigator.nextPage(MongoTestedWithIndexingPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Object Store page to the CCheck Your Answers page" in {
        navigator.nextPage(UsingObjectStorePage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Correct Retention Period page to the Check Your Answers page" in {
        navigator.nextPage(CorrectRetentionPeriodPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
    }
  }
}
