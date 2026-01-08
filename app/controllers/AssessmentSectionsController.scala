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

import config.{AssessmentSection, QuestionStructure}
import connectors.{QuestionConnector, TeamsAndRepositoriesConnector}
import controllers.actions.*
import models.ReviewStatus.NeedsReview
import models.repositories.GitRepository
import models.{Question, ReviewStatus}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.SectionSummary
import views.html.{AssessmentSectionsView, ErrorTemplate}
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, Retrieval}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AssessmentSectionsController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       auth: FrontendAuthComponents,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: AssessmentSectionsView,
                                       questionConnector: QuestionConnector,
                                       repositoryConnector: TeamsAndRepositoriesConnector,
                                       repositoryActionFactory: RepositoryActionFactory
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(service: String): Action[AnyContent] =
    (auth.authenticatedAction(
      continueUrl = routes.AssessmentSectionsController.onPageLoad(service),
      retrieval = Retrieval.username
    ) andThen repositoryActionFactory.action(service)) { implicit request =>

      val sections: Seq[SectionSummary] = QuestionStructure.sections(request.assessedService.isAdminService).map(
        s => AssessmentSectionsController.createViewModel(service, s, request.assessedService.answeredQuestions)
      )
      val overallTeamStatus = ReviewStatus.getOverallStatus(sections.map(_.teamStatus))
      val overallReviewerStatus = ReviewStatus.getOverallStatus(sections.map(_.reviewerStatus))
      Ok(view(service, sections, overallTeamStatus, overallReviewerStatus))
  }

}

object AssessmentSectionsController {

  def createViewModel(service: String, assessmentSection: AssessmentSection, questionMap: Map[String, Question]): SectionSummary = {
    val questions: Seq[Question] = assessmentSection.questionPages.map(q =>
      questionMap.getOrElse(q.name, Question(service = service, questionId = q.name, teamStatus = NeedsReview, reviewerStatus = NeedsReview))
    )
    val sectionTeamStatus = ReviewStatus.getOverallStatus(questions.map(_.teamStatus))
    val sectionReviewerStatus = ReviewStatus.getOverallStatus(questions.map(_.reviewerStatus))

    SectionSummary(
      title = assessmentSection.name,
      href = controllers.routes.SectionController.onPageLoad(service, assessmentSection.name).url,
      teamStatus = sectionTeamStatus,
      reviewerStatus = sectionReviewerStatus
    )
  }

}
