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

package controllers

import base.{FakeQuestionConnector, SpecBase}
import config.QuestionStructure
import connectors.QuestionConnector
import models.{Question, QuestionResponse, ReviewStatus}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import viewmodels.SectionSummary
import views.html.AssessmentSectionsView

import java.time.Instant

class AssessmentSectionsControllerSpec extends SpecBase {

  private val emptySections = QuestionStructure.sections.map(assessmentSection =>
    SectionSummary(
      title = assessmentSection.name,
      href = controllers.routes.SectionController.onPageLoad("service123", assessmentSection.name).url,
      teamStatus = ReviewStatus.NeedsReview,
      reviewerStatus = ReviewStatus.NeedsReview
    )
  )

  "AssessmentSections Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder()
        .overrides(
          bind[QuestionConnector].toInstance(new FakeQuestionConnector(QuestionResponse("service123", Seq()))),
        ).build()

      running(application) {
        val request = FakeRequest(GET, routes.AssessmentSectionsController.onPageLoad("service123").url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AssessmentSectionsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view("service123", emptySections, ReviewStatus.NeedsReview, ReviewStatus.NeedsReview)(request, messages(application)).toString
      }
    }
  }

  "createViewModel" - {

    def generateQuestion(questionId: String, teamStatus: ReviewStatus, reviewerStatus: ReviewStatus) = questionId -> Question(
      service = "service123",
      questionId = questionId,
      lastUpdated = Instant.now,
      teamComment = None,
      teamStatus = teamStatus,
      teamMemberUsername = None,
      reviewerComment = None,
      reviewerStatus = reviewerStatus,
      reviewerUsername = None
    )

    "returns NeedsReview where all questions have been unanswered" in {
      val section = QuestionStructure.BuildAndResilience
      val questions = Map(
        generateQuestion("nonstandard-pattern", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("bobby-rules", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("http-verbs", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("deprecated-libraries", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("readme", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("appropriate-timeouts", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
      )

      val expected = SectionSummary(
        title = section.name,
        href = controllers.routes.SectionController.onPageLoad("service123", section.name).url,
        teamStatus = ReviewStatus.NeedsReview,
        reviewerStatus = ReviewStatus.NeedsReview
      )
      val result = AssessmentSectionsController.createViewModel("service123", section, questions)
      result mustEqual expected
    }

    "determines the statuses by the underlying questions" in {
      val section = QuestionStructure.BuildAndResilience
      val questions = Map(
        generateQuestion("nonstandard-pattern", ReviewStatus.Warning, ReviewStatus.NeedsReview),
        generateQuestion("bobby-rules", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("http-verbs", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("deprecated-libraries", ReviewStatus.NeedsReview, ReviewStatus.Fail),
        generateQuestion("readme", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
        generateQuestion("appropriate-timeouts", ReviewStatus.NeedsReview, ReviewStatus.NeedsReview),
      )

      val expected = SectionSummary(
        title = section.name,
        href = controllers.routes.SectionController.onPageLoad("service123", section.name).url,
        teamStatus = ReviewStatus.Warning,
        reviewerStatus = ReviewStatus.Fail
      )
      val result = AssessmentSectionsController.createViewModel("service123", section, questions)
      result mustEqual expected
    }
  }

}
