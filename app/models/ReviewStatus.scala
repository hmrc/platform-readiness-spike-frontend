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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

sealed trait ReviewStatus

object ReviewStatus extends Enumerable.Implicits {

  case object NeedsReview extends WithName("needsReview") with ReviewStatus
  case object Fail extends WithName("fail") with ReviewStatus
  case object Warning extends WithName("warning") with ReviewStatus
  case object Pass extends WithName("pass") with ReviewStatus

  val values: Seq[ReviewStatus] = Seq(
    NeedsReview, Fail, Warning, Pass
  )

  def getOverallStatus(statuses: Seq[ReviewStatus]): ReviewStatus = {
    if (statuses.contains(ReviewStatus.Fail)) ReviewStatus.Fail
    else if (statuses.contains(ReviewStatus.Warning)) ReviewStatus.Warning
    else if (statuses.contains(ReviewStatus.NeedsReview)) ReviewStatus.NeedsReview
    else if (statuses.forall(_ == ReviewStatus.Pass)) ReviewStatus.Pass
    else ReviewStatus.NeedsReview
  }

  def options(selectedStatus: Option[String])(implicit messages: Messages): Seq[SelectItem] = values.map {
    value =>
      SelectItem(
        text     = messages(s"reviewStatus.${value.toString}"),
        value    = Some(value.toString),
        selected = selectedStatus.contains(value.toString)
      )
  }

  def tagColour(status: ReviewStatus): String = status match {
    case NeedsReview => "blue"
    case Fail => "red"
    case Warning => "yellow"
    case Pass => "green"
  }

  implicit val enumerable: Enumerable[ReviewStatus] =
    Enumerable(values.map(v => v.toString -> v): _*)

}