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

      "must go from the Using Mongo page to the Resilient Recycling Mongo WHEN answer is YES" in {
        UserAnswers("id").set(UsingMongoPage, true).foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, NormalMode, userAnswers) mustBe dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(NormalMode)
        }
      }
      "must go from the Using Mongo Page to Using Object Store page WHEN answer is NO" in {
        UserAnswers("id").set(UsingMongoPage, false).foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, NormalMode, userAnswers) mustBe dataPersistenceRoutes.UsingObjectStoreController.onPageLoad(NormalMode)
        }
      }
      "must go from the Resilient Recycling Mongo page to the Public Mongo TTL page" in {
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
      "must go from the Using Object Store page to Correct Retention Period page WHEN answer is YES" in {
        UserAnswers("id").set(UsingObjectStorePage, true).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, NormalMode, userAnswers) mustBe dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(NormalMode)
        }
      }
      "must go from the Using Object Store page to Check Your Answers page WHEN answer is NO" in {
        UserAnswers("id").set(UsingObjectStorePage, false).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, NormalMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      "must go from the Correct Retention Period page to the Check Your Answers page" in {
        navigator.nextPage(CorrectRetentionPeriodPage, NormalMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }
    }

    "in Check mode" - {

      "must go from the Using Mongo page to the Resilient Recycling Mongo WHEN answer is YES AND Resilient Recycling Mongo does not have an Answer" in {
        UserAnswers("id").set(UsingMongoPage, true).foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.ResilientRecycleMongoController.onPageLoad(CheckMode)
        }
      }
      "must go from the Using Mongo Page to Check Your Answers page WHEN answer is Yes AND Resilient Recycling Mongo has an Answer " in {
        val updatedUserAnswers = for {
          withMongo <- UserAnswers("id").set(UsingMongoPage, true)
          withResilient <- withMongo.set(ResilientRecycleMongoPage, true)
        } yield withResilient

        updatedUserAnswers.foreach { userAnswers =>
            navigator.nextPage(UsingMongoPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
          }
      }
      "must go from the Using Mongo Page to Check Your Answers page WHEN answer is NO AND Resilient Recycle Mongo has an Answer" in {
        val updatedUserAnswers = for {
          withMongo <- UserAnswers("id").set(UsingMongoPage, false)
          withResilient <- withMongo.set(ResilientRecycleMongoPage, true)
        } yield withResilient

        updatedUserAnswers.foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from Resilient Recycling Mongo to Public Mongo TTL WHEN Public Mongo TTL does not have an answer" in {
        navigator.nextPage(ResilientRecycleMongoPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.PublicMongoTTLController.onPageLoad(CheckMode)
      }
      "must go from the Resilient Recycling Mongo page to the Check Your Answers page WHEN Public Mongo TTL has an Answer" in {
        UserAnswers("id").set(PublicMongoTTLPage, true).foreach { userAnswers =>
          navigator.nextPage(ResilientRecycleMongoPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Public Mongo TTL page to the Check Your Answers page WHEN Field Level Encryption does not have an answer" in {
        navigator.nextPage(PublicMongoTTLPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.FieldLevelEncryptionController.onPageLoad(CheckMode)
      }
      "must go from the Public Mongo TTL page to the Check Your Answers page WHEN Field Level Encryption has an Answer" in {
        UserAnswers("id").set(FieldLevelEncryptionPage, true).foreach { userAnswers =>
          navigator.nextPage(PublicMongoTTLPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Field Level Encryption page to the Protected Mongo page WHEN Protected Mongo does not have an Answer" in {
        navigator.nextPage(FieldLevelEncryptionPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.ProtectedMongoTTLController.onPageLoad(CheckMode)
      }
      "must go from the Field Level Encryption page to the Check Your Answers page WHEN Protected Mongo has an Answer" in {
        UserAnswers("id").set(ProtectedMongoTTLPage, true).foreach { userAnswers =>
          navigator.nextPage(FieldLevelEncryptionPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Protected Mongo TTL page to the Mongo Tested With Indexing page WHEN Mongo Tested With Indexing does not have an Answer" in {
        navigator.nextPage(ProtectedMongoTTLPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.MongoTestedWithIndexingController.onPageLoad(CheckMode)
      }
      "must go from the Protected Mongo TTL page to the Check Your Answers page WHEN Mongo Tested With Indexing has an Answer" in {
        UserAnswers("id").set(MongoTestedWithIndexingPage, true).foreach { userAnswers =>
          navigator.nextPage(ProtectedMongoTTLPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Mongo Tested With Indexing page to the Check Your Answers page" in {
        navigator.nextPage(MongoTestedWithIndexingPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }

      "must go from the Using Object Store page to Correct Retention Period page WHEN answer is YES AND Correct Retention Period does not have an Answer" in {
        UserAnswers("id").set(UsingObjectStorePage, true).foreach { userAnswers =>
          navigator.nextPage(UsingObjectStorePage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CorrectRetentionPeriodController.onPageLoad(CheckMode)
        }
      }
      "must go from the Using Object Store page to Check Your Answers page WHEN answer is YES WHEN Correct Retention Period has an Answer" in {
        val updatedUserAnswers = for {
          withObject <- UserAnswers("id").set(UsingObjectStorePage, true)
          withRetention <- withObject.set(CorrectRetentionPeriodPage, true)
        } yield withRetention

        updatedUserAnswers.foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
      "must go from the Using Object Store page to Check Your Answers page WHEN answer is NO WHEN Correct Retention Period has an Answer" in {
        val updatedUserAnswers = for {
          withObject <- UserAnswers("id").set(UsingObjectStorePage, false)
          withRetention <- withObject.set(CorrectRetentionPeriodPage, true)
        } yield withRetention

        updatedUserAnswers.foreach { userAnswers =>
          navigator.nextPage(UsingMongoPage, CheckMode, userAnswers) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from the Correct Retention Period page to the Check Your Answers page" in {
        navigator.nextPage(CorrectRetentionPeriodPage, CheckMode, UserAnswers("id")) mustBe dataPersistenceRoutes.CheckYourAnswersController.onPageLoad()
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe securityRoutes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
