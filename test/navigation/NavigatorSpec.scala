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
import controllers.buildResilience.routes as buildResilienceRoutes
import controllers.dataPersistence.routes as dataPersistenceRoutes
import controllers.commonServiceUsage.routes as commonServiceUsageRoutes
import models.*
import pages.*
import pages.buildResilience.{AppropriateTimeoutsPage, BreakBobbyRulesPage, DeprecatedLibrariesPage, DoesNonstandardPatternPage, NonstandardPatternPage, ReadMeFitForPurposePage, ServiceURLPage, UsingHTTPVerbsPage}
import pages.commonServiceUsage.{IntegrationCheckPage, NotifyDependantServicesPage}
import pages.dataPersistence.{CorrectRetentionPeriodPage, FieldLevelEncryptionPage, MongoTestedWithIndexingPage, ProtectedMongoTTLPage, PublicMongoTTLPage, ResilientRecycleMongoPage, UsingMongoPage, UsingObjectStorePage}

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "in Build & Resilience" - {

        "must go from a page that doesn't exist in the route map to Index" in {
          case object UnknownPage extends Page
          navigator.nextPage(UnknownPage, NormalMode, UserAnswers("id")) mustBe routes.IndexController.onPageLoad()
        }

        "must go from the Index page to the Does Include Non-standard Pattern page" in {
          navigator.nextPage(IndexPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.ServiceURLController.onPageLoad(NormalMode)
        }

        "must go from the Service URL page to the Does Include Non-standard Pattern page" in {
          navigator.nextPage(ServiceURLPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.DoesNonstandardPatternController.onPageLoad(NormalMode)
        }

        "NORMALMODE must go from the Does Include Non-standard Pattern page to the Which Non-standard Pattern page WHEN answer is YES" in {
          UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach { userAnswers =>
            navigator.nextPage(DoesNonstandardPatternPage, NormalMode, userAnswers) mustBe buildResilienceRoutes.NonstandardPatternController.onPageLoad(NormalMode)
          }
        }

        "NORMALMODE must go from the Does Include Non-standard Pattern Page to Break Bobby Rules page WHEN answer is NO" in {
          UserAnswers("id").set(DoesNonstandardPatternPage, false).foreach { userAnswers =>
            navigator.nextPage(DoesNonstandardPatternPage, NormalMode, userAnswers) mustBe buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)
          }
        }

        "must go from the Which Non-standard Pattern page to the Bobby Rules page" in {
          navigator.nextPage(NonstandardPatternPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.BreakBobbyRulesController.onPageLoad(NormalMode)
        }

        "must go from the Bobby Rules page to the Deprecated HMRC Libraries page" in {
          navigator.nextPage(BreakBobbyRulesPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.DeprecatedLibrariesController.onPageLoad(NormalMode)
        }

        "must go from the Deprecated HMRC Libraries page to the Using HTTP Verbs page" in {
          navigator.nextPage(DeprecatedLibrariesPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.UsingHTTPVerbsController.onPageLoad(NormalMode)
        }

        "must go from the HTTP Verbs page to the ReadME Up-To-Date and fit for purpose page" in {
          navigator.nextPage(UsingHTTPVerbsPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.ReadMeFitForPurposeController.onPageLoad(NormalMode)
        }

        "must go from the ReadME Up-To-Date and fit for purpose page to the Appropriate Timeouts page" in {
          navigator.nextPage(ReadMeFitForPurposePage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.AppropriateTimeoutsController.onPageLoad(NormalMode)
        }

        "must go from the Appropriate Timeouts page to the Check Your Answers page" in {
          navigator.nextPage(AppropriateTimeoutsPage, NormalMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }


      "in Data Persistence" - {

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

      "in Common Service Usage" - {

        "must go from the Integration Check page to Notify Dependant Services page" in {
          navigator.nextPage(IntegrationCheckPage, NormalMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.NotifyDependantServicesController.onPageLoad(NormalMode)
        }
        "must go from the Notify Dependant Services page to Check Your Answers page" in {
          navigator.nextPage(NotifyDependantServicesPage, NormalMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
        }
      }
    }

    "in Check mode" - {

      "in Build & Resilience" - {

        "must go from the Service URL page to the Check Your Answers page" in {
          navigator.nextPage(ServiceURLPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }

        "CHECKMODE must go from the Does Include Non-standard Pattern Page to Which Non-standard Patterns page WHEN answer is YES" in {
          UserAnswers("id").set(DoesNonstandardPatternPage, true).foreach { userAnswers =>
            navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.NonstandardPatternController.onPageLoad(CheckMode)
          }
        }

        "CHECKMODE must go from the Does Include Non-standard Pattern Page to CheckYourAnswers page WHEN answer is NO" in {
          UserAnswers("id").set(DoesNonstandardPatternPage, false).foreach { userAnswers =>
            navigator.nextPage(DoesNonstandardPatternPage, CheckMode, userAnswers) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
          }
        }

        "must go from the Which Non-standard Pattern page to the Check Your Answers page" in {
          navigator.nextPage(NonstandardPatternPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }

        "must go from the Bobby Rules page to the Check Your Answers page" in {
          navigator.nextPage(BreakBobbyRulesPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }

        "must go from the Deprecated HMRC Libraries page to the Check Your Answers page" in {
          navigator.nextPage(DeprecatedLibrariesPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }

        "must go from the HTTP Verbs page to the Check Your Answers page" in {
          navigator.nextPage(UsingHTTPVerbsPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }

        "must go from the ReadME Up-To-Date and fit for purpose page to the Check Your Answers page" in {
          navigator.nextPage(ReadMeFitForPurposePage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }

        "must go from the Appropriate Timeouts page to the Check Your Answers page" in {
          navigator.nextPage(AppropriateTimeoutsPage, CheckMode, UserAnswers("id")) mustBe buildResilienceRoutes.CheckYourAnswersController.onPageLoad()
        }
      }


      "in Data Persistence" - {

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

      "in Common Service Usage" - {

        "must go from the Integration Check page to Check Your Answers page" in {
          navigator.nextPage(IntegrationCheckPage, CheckMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
        }
        "must go from the Notify Dependant Services page to Check Your Answers" in {
          navigator.nextPage(NotifyDependantServicesPage, CheckMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
        }
      }

      "must go from a page that doesn't exist in the edit route map to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, UserAnswers("id")) mustBe commonServiceUsageRoutes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
