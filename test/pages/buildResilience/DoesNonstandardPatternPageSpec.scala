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

package pages.buildResilience

import base.SpecBase
import models.UserAnswers

class DoesNonstandardPatternPageSpec extends SpecBase {

  "DoesNonstandardPatternPage" - {

    "Must Clear Nonstandard Pages when answer is changed from YES to NO" in {
      val userAnswersWithNonstandardPages = for {
        withDoesNonstandard <- UserAnswers("id").set(DoesNonstandardPatternPage, true)
        withNonstandard <- withDoesNonstandard.set(NonstandardPatternPage, "false")
      } yield withNonstandard

      val actual = userAnswersWithNonstandardPages.flatMap(_.set(DoesNonstandardPatternPage, false))

      actual.foreach(_.get(NonstandardPatternPage) mustBe None)
    }
    "Must Not Clear Nonstandard Pages when Answer is changed from NO to YES" in {
      val userAnswersWithNonstandardPages = for {
        withDoesNonstandard <- UserAnswers("id").set(DoesNonstandardPatternPage, false)
        withNonstandard <- withDoesNonstandard.set(NonstandardPatternPage, "false")
      } yield withNonstandard

      val actual = userAnswersWithNonstandardPages.flatMap(_.set(DoesNonstandardPatternPage, true))

      actual.foreach(_.get(NonstandardPatternPage) mustBe Some("false"))
    }
    "Must Clear Nonstandard Pages when Answer is unchanged and Answer is NO" in {
      val userAnswersWithNonstandardPages = for {
        withDoesNonstandard <- UserAnswers("id").set(DoesNonstandardPatternPage, false)
        withNonstandard <- withDoesNonstandard.set(NonstandardPatternPage, "false")
      } yield withNonstandard

      val actual = userAnswersWithNonstandardPages.flatMap(_.set(DoesNonstandardPatternPage, false))

      actual.foreach(_.get(NonstandardPatternPage) mustBe None)
    }
    "Must not do anything when Answer is unchanged and Answer is YES" in {
      val userAnswersWithNonstandardPages = for {
        withDoesNonstandard <- UserAnswers("id").set(DoesNonstandardPatternPage, true)
        withNonstandard <- withDoesNonstandard.set(NonstandardPatternPage, "false")
      } yield withNonstandard

      val actual = userAnswersWithNonstandardPages.flatMap(_.set(DoesNonstandardPatternPage, true))

      actual.foreach(_.get(NonstandardPatternPage) mustBe Some("false"))
    }
  }
  
}
