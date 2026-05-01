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

import config.{QuestionPage, QuestionStructure}
import connectors.QuestionConnector
import controllers.actions.*
import models.ReviewStatus.{NeedsReview, Pass}
import models.{Question, ReviewStatus}
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, Retrieval}

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SectionView
import viewmodels.QuestionSummary

import scala.concurrent.ExecutionContext

class SectionController @Inject()(
                                   override val messagesApi: MessagesApi,
                                   auth: FrontendAuthComponents,
                                   val controllerComponents: MessagesControllerComponents,
                                   view: SectionView,
                                   connector: QuestionConnector,
                                   repositoryActionFactory: RepositoryActionFactory
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(service: String, section: String): Action[AnyContent] =
    (auth.authenticatedAction(
      continueUrl = routes.SectionController.onPageLoad(service, section),
      retrieval = Retrieval.username
    )() andThen repositoryActionFactory.action(service)) { implicit request =>
      
      val questionSummaries = QuestionStructure.sectionsMap(section).map { q =>
        val currentQuestion = request.assessedService.answeredQuestions.get(q.name)
        SectionController.createViewModel(service, section, q, currentQuestion)
      }
      val overallTeamStatus = ReviewStatus.getOverallStatus(questionSummaries.map(_.teamStatus))
      val overallReviewerStatus = ReviewStatus.getOverallStatus(questionSummaries.map(_.reviewerStatus))
      Ok(view(service, section, questionSummaries, overallTeamStatus, overallReviewerStatus))
  }

}

object SectionController {

  def createViewModel(service: String, section: String, question: QuestionPage, currentQuestion: Option[Question]): QuestionSummary = {
    QuestionSummary(
      title = question.name,
      href = controllers.routes.QuestionController.onPageLoad(service, section, question.name).url,
      teamStatus = currentQuestion.map(_.teamStatus).getOrElse(ReviewStatus.NeedsReview),
      reviewerStatus = currentQuestion.map(_.reviewerStatus).getOrElse(ReviewStatus.NeedsReview)
    )
  }

}
