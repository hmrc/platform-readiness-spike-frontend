/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ReviewStatusSpec extends AnyFreeSpec with Matchers {

  "getOverallStatus" - {
    "Return fail if a fail exists in the list" in {
      val statuses = Seq(ReviewStatus.Fail, ReviewStatus.NeedsReview, ReviewStatus.Pass, ReviewStatus.Warning)
      val expected = ReviewStatus.Fail
      val result = ReviewStatus.getOverallStatus(statuses)
      result mustEqual expected
    }

    "Return warning if a warning exists in the list, and there are no fails" in {
      val statuses = Seq(ReviewStatus.NeedsReview, ReviewStatus.NeedsReview, ReviewStatus.Pass, ReviewStatus.Warning)
      val expected = ReviewStatus.Warning
      val result = ReviewStatus.getOverallStatus(statuses)
      result mustEqual expected
    }

    "Return pass if all statuses equal pass" in {
      val statuses = Seq(ReviewStatus.Pass, ReviewStatus.Pass, ReviewStatus.Pass, ReviewStatus.Pass)
      val expected = ReviewStatus.Pass
      val result = ReviewStatus.getOverallStatus(statuses)
      result mustEqual expected
    }

    "Return NeedsReview if there are no warnings or fails, and if not all statuses equal pass" in {
      val statuses = Seq(ReviewStatus.NeedsReview, ReviewStatus.Pass, ReviewStatus.Pass, ReviewStatus.Pass)
      val expected = ReviewStatus.NeedsReview
      val result = ReviewStatus.getOverallStatus(statuses)
      result mustEqual expected
    }

    "Return NeedsReview if all statuses equal NeedsReview" in {
      val statuses = Seq(ReviewStatus.NeedsReview, ReviewStatus.NeedsReview, ReviewStatus.NeedsReview, ReviewStatus.NeedsReview)
      val expected = ReviewStatus.NeedsReview
      val result = ReviewStatus.getOverallStatus(statuses)
      result mustEqual expected
    }
  }

}
