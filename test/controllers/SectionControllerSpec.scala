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
import config.{QuestionPage, QuestionStructure}
import connectors.QuestionConnector
import models.{Question, QuestionResponse, ReviewStatus}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import viewmodels.QuestionSummary
import views.html.SectionView
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{eq => eqTo}
import uk.gov.hmrc.internalauth.client.Retrieval
import uk.gov.hmrc.internalauth.client.Retrieval.Username
import scala.concurrent.Future
import java.time.Instant

class SectionControllerSpec extends SpecBase {

  private val sectionName = QuestionStructure.BuildAndResilience.name

  private val emptyQuestions = QuestionStructure.BuildAndResilience.questionPages.map(question =>
    QuestionSummary(
      title = question.name,
      href = controllers.routes.QuestionController.onPageLoad("pertax-frontend", sectionName, question.name).url,
      teamStatus = ReviewStatus.NeedsReview,
      reviewerStatus = ReviewStatus.NeedsReview
    )
  )

  "Section Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockStubBehaviour.stubAuth(eqTo(None), eqTo(Retrieval.username))).thenReturn(Future.successful(Username("username")))

      val application = applicationBuilder()
        .overrides(
          bind[QuestionConnector].toInstance(new FakeQuestionConnector(QuestionResponse("pertax-frontend", Seq()))),
        ).build()

      running(application) {
        val request = FakeRequest(GET, routes.SectionController.onPageLoad("pertax-frontend", sectionName).url)
          .withSession("authToken" -> "Token some-token")

        val result = route(application, request).value

        val view = application.injector.instanceOf[SectionView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view("pertax-frontend", sectionName, emptyQuestions, ReviewStatus.NeedsReview, ReviewStatus.NeedsReview)(request, messages(application)).toString
      }
    }
  }

  "createViewModel" - {

    val questionName = "question-name"

    "defaults to NeedsReview where question isn't answered" in {
      val expected = QuestionSummary(
        title = questionName,
        href = controllers.routes.QuestionController.onPageLoad("pertax-frontend", sectionName, questionName).url,
        teamStatus = ReviewStatus.NeedsReview,
        reviewerStatus = ReviewStatus.NeedsReview
      )
      val result = SectionController.createViewModel("pertax-frontend", sectionName, QuestionPage(questionName), None)
      result mustEqual expected
    }

    "uses the statuses where the question is populated" in {
      val question = Question(
        service = "pertax-frontend",
        questionId = questionName,
        lastUpdated = Instant.now,
        teamComment = Some("Some comment"),
        teamStatus = ReviewStatus.Pass,
        teamMemberUsername = None,
        reviewerComment = None,
        reviewerStatus = ReviewStatus.Fail,
        reviewerUsername = None
      )
      val expected = QuestionSummary(
        title = questionName,
        href = controllers.routes.QuestionController.onPageLoad("pertax-frontend", sectionName, questionName).url,
        teamStatus = ReviewStatus.Pass,
        reviewerStatus = ReviewStatus.Fail
      )
      val result = SectionController.createViewModel("pertax-frontend", sectionName, QuestionPage(questionName), Some(question))
      result mustEqual expected
    }
  }
}
