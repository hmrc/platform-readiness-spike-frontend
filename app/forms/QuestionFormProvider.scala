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
import models.requests.ReviewMode

import java.time.Instant

case class QuestionFormModel(
                              comment: Option[String],
                              status: ReviewStatus
                            )


class QuestionFormProvider @Inject() extends Mappings {

  def apply(reviewMode: ReviewMode): Form[QuestionFormModel] = {
    val (commentField, statusField) = reviewMode match {
      case ReviewMode.TeamMember => ("teamComment", "teamStatus")
      case _ => ("reviewerComment", "reviewerStatus")
    }

    Form(
      mapping(
        commentField -> optional(text(s"question.$commentField.error.required")),
        statusField -> enumerable[ReviewStatus](s"question.$statusField.error.required")
      )(QuestionFormModel.apply)(q => Some((q.comment, q.status)))
    )
  }

}


