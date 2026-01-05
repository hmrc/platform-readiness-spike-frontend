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

package forms

import javax.inject.Inject
import forms.mappings.Mappings
import models.{Question, ReviewStatus}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

import java.time.Instant

class QuestionFormProvider @Inject() extends Mappings {

  def apply(service: String, questionId: String, teamMemberUsername: String, reviewerUsername: String): Form[Question] =
    Form(
      mapping(
        "teamComment" -> optional(text("question.teamComment.error.required")),
        "teamStatus" -> enumerable[ReviewStatus]("question.teamStatus.error.required"),
        "reviewerComment" -> optional(text("question.reviewerComment.error.required")),
        "reviewerStatus" -> enumerable[ReviewStatus]("question.reviewerStatus.error.required")
      )((teamComment, teamStatus, reviewerComment, reviewerStatus) =>
          Question(service,
            questionId,
            Instant.now,
            teamComment,
            teamStatus,
            Some(teamMemberUsername),
            reviewerComment,
            reviewerStatus,
            Some(reviewerUsername)))
        (q => Some(q.teamComment, q.teamStatus, q.reviewerComment, q.reviewerStatus))
    )
}