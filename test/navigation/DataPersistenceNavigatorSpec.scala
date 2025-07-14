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

      "NORMALMODE must go from the Using Mongo page to the Resilient Recycling Mongo WHEN answer is YES" in {
        val oldAnswers = UserAnswers("id")
        UserAnswers("id").set(UsingMongoPage, true).foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, NormalMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)
        }
      }
      "NORMALMODE must go from the Using Mongo Page to Using Object Store page WHEN answer is NO" in {
        val oldAnswers = UserAnswers("id")
        UserAnswers("id").set(UsingMongoPage, false).foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, NormalMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)
        }
      }
      "must go from the Resilient Recycling Mongo page to the Public Mongo TTL page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(ResilientRecycleMongoPage, NormalMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.PublicMongoTTLController.onPageLoad(NormalMode)
      }
      "must go from the Public Mongo TTL page to the Field Level Encryption page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(PublicMongoTTLPage, NormalMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.FieldLevelEncryptionController.onPageLoad(NormalMode)
      }
      "must go from the Field Level Encryption page to the Protected Mongo TTL page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(FieldLevelEncryptionPage, NormalMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.ProtectedMongoTTLController.onPageLoad(NormalMode)
      }
      "must go from the Protected Mongo TTL page to the Mongo Tested With Indexing page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(ProtectedMongoTTLPage, NormalMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.MongoTestedWithIndexingController.onPageLoad(NormalMode)
      }
      "must go from the Mongo Tested With Indexing page to the Using Object Store page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(MongoTestedWithIndexingPage, NormalMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)
      }
      "NORMALMODE must go from the Using Object Store page to Correct Retention Period page WHEN answer is YES" in {
        val oldAnswers = UserAnswers("id")
        UserAnswers("id").set(UsingObjectStorePage, true).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, NormalMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(NormalMode)
        }
      }
      "NORMALMODE must go from the Using Object Store page to Check Your Answers page WHEN answer is NO" in {
        val oldAnswers = UserAnswers("id")
        UserAnswers("id").set(UsingObjectStorePage, false).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, NormalMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      "must go from the Correct Retention Period page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(CorrectRetentionPeriodPage, NormalMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" - {

      "CHECKMODE must go from the Using Mongo page to the Resilient Recycling Mongo WHEN answer is From NO to YES when oldAnswer was NO" in {
        UserAnswers("id").set(UsingMongoPage, false).foreach { oldAnswers =>
          UserAnswers("id").set(UsingMongoPage, true).foreach { userAnswers =>
            navigator.nextPage(UsingMongoPage, CheckMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)
          }
        }
      }
      "CHECKMODE must go from the Using Mongo Page to Check Your Answers page WHEN answer is from YES to NO when oldAnswer was YES" in {
        UserAnswers("id").set(UsingMongoPage, true).foreach { oldAnswers =>
          UserAnswers("id").set(UsingMongoPage, false).foreach { userAnswers =>
            navigator.nextPage(UsingMongoPage, CheckMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
          }
        }
      }
      "CHECKMODE must go from the Using Mongo Page to Check Your Answers page WHEN answer is from YES to YES when oldAnswer was YES" in {
        UserAnswers("id").set(UsingMongoPage, true).foreach { oldAnswers =>
          UserAnswers("id").set(UsingMongoPage, true).foreach { userAnswers =>
            navigator.nextPage(UsingMongoPage, CheckMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
          }
        }
      }
      "CHECKMODE must go from the Using Mongo Page to Check Your Answers page WHEN answer is from NO to NO when oldAnswer was NO" in {
        UserAnswers("id").set(UsingMongoPage, false).foreach { oldAnswers =>
          UserAnswers("id").set(UsingMongoPage, false).foreach { userAnswers =>
            navigator.nextPage(UsingMongoPage, CheckMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
          }
        }
      }
      "must go from the Resilient Recycling Mongo page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(ResilientRecycleMongoPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Public Mongo TTL page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(PublicMongoTTLPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Field Level Encryption page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(FieldLevelEncryptionPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Protected Mongo TTL page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(ProtectedMongoTTLPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "must go from the Mongo Tested With Indexing page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(MongoTestedWithIndexingPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
      "CHECKMODE must go from the Using Object Store page to Correct Retention Period page WHEN answer is YES" in {
        val oldAnswers = UserAnswers("id")
        UserAnswers("id").set(UsingObjectStorePage, true).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, CheckMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(CheckMode)
        }
      }
      "CHECKMODE must go from the Using Object Store page to Check Your Answers page WHEN answer is NO" in {
        val oldAnswers = UserAnswers("id")
        UserAnswers("id").set(UsingObjectStorePage, false).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, CheckMode, userAnswers, oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      "must go from the Correct Retention Period page to the Check Your Answers page" in {
        val oldAnswers = UserAnswers("id")
        navigator.nextPage(CorrectRetentionPeriodPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        val oldAnswers = UserAnswers("id")
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id"), oldAnswers) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
