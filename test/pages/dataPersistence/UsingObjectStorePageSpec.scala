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

package pages.dataPersistence

import base.SpecBase
import models.UserAnswers

class UsingObjectStorePageSpec extends SpecBase {

  "UsingObjectStorePage" - {

    "Must Clear Object Pages when answer is changed from YES to NO" in {
      val userAnswersWithObjectPages = for {
        withObject <- UserAnswers("id").set(UsingObjectStorePage, true)
        withRetention <- withObject.set(CorrectRetentionPeriodPage, false)
      } yield withRetention

      val actual = userAnswersWithObjectPages.flatMap(_.set(UsingObjectStorePage, false))

      actual.foreach(_.get(CorrectRetentionPeriodPage) mustBe None)
    }
    "Must Not Clear Object Pages when Answer is changed from NO to YES" in {
      val userAnswersWithObjectPages = for {
        withObject <- UserAnswers("id").set(UsingObjectStorePage, false)
        withRetention <- withObject.set(CorrectRetentionPeriodPage, false)
      } yield withRetention

      val actual = userAnswersWithObjectPages.flatMap(_.set(UsingObjectStorePage, true))

      actual.foreach(_.get(CorrectRetentionPeriodPage) mustBe Some(false))
    }
    "Must Clear Object Pages when Answer is unchanged and Answer is NO" in {
      val userAnswersWithObjectPages = for {
        withObject <- UserAnswers("id").set(UsingObjectStorePage, false)
        withRetention <- withObject.set(CorrectRetentionPeriodPage, false)
      } yield withRetention

      val actual = userAnswersWithObjectPages.flatMap(_.set(UsingObjectStorePage, false))

      actual.foreach(_.get(CorrectRetentionPeriodPage) mustBe None)
    }
    "Must not do anything when Answer is unchanged and Answer is YES" in {
      val userAnswersWithObjectPages = for {
        withObject <- UserAnswers("id").set(UsingObjectStorePage, true)
        withRetention <- withObject.set(CorrectRetentionPeriodPage, false)
      } yield withRetention
      
      val actual = userAnswersWithObjectPages.flatMap(_.set(UsingObjectStorePage, true))
      
      actual.foreach(_.get(CorrectRetentionPeriodPage) mustBe Some(false))
    }
  }
}
