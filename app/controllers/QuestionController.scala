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
import forms.{QuestionFormModel, QuestionFormProvider}
import models.Question
import models.ReviewStatus.NeedsReview
import models.requests.ReviewMode
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{ErrorTemplate, QuestionView}
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, Retrieval}

import scala.concurrent.{ExecutionContext, Future}

class QuestionController @Inject()(
                                   override val messagesApi: MessagesApi,
                                   auth: FrontendAuthComponents,
                                   val controllerComponents: MessagesControllerComponents,
                                   view: QuestionView,
                                   errorView: ErrorTemplate,
                                   formProvider: QuestionFormProvider,
                                   connector: QuestionConnector,
                                   repositoryActionFactory: RepositoryActionFactory
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  def onPageLoad(service: String, section: String, question: String): Action[AnyContent] =
    (auth.authenticatedAction(
      continueUrl = routes.QuestionController.onPageLoad(service, section, question),
      retrieval = Retrieval.username
    )() andThen repositoryActionFactory.action(service)) { implicit request =>

      QuestionStructure.sectionsMap(section).find(_.name == question) match {
        case Some(q) => {
          val savedQuestion: Option[Question] = request.assessedService.answeredQuestions.get(question)
          val preparedForm = (savedQuestion, request.reviewMode) match {
            case (Some(sq), ReviewMode.TeamMember) => formProvider(request.reviewMode).fill(QuestionFormModel(sq.teamComment, sq.teamStatus))
            case (Some(sq), ReviewMode.Reviewer) => formProvider(request.reviewMode).fill(QuestionFormModel(sq.reviewerComment, sq.reviewerStatus))
            case _ => formProvider(request.reviewMode)
          }
          Ok(view(service, section, question, preparedForm, savedQuestion, request.reviewMode))
        }
        case None => NotFound(errorView("questionNotFound.title", "questionNotFound.heading", "questionNotFound.message"))
      }
  }

  def onSubmit(service: String, section: String, question: String): Action[AnyContent] = (auth.authenticatedAction(
    continueUrl = routes.QuestionController.onPageLoad(service, section, question),
    retrieval = Retrieval.username
  ) andThen repositoryActionFactory.action(service)).async {
    implicit request =>

      val savedQuestion: Option[Question] = request.assessedService.answeredQuestions.get(question)

      formProvider(request.reviewMode).bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(service, section, question, formWithErrors, savedQuestion, request.reviewMode))),

        value =>
          val updatedQuestion = (savedQuestion, request.reviewMode) match {
            case (Some(sq), ReviewMode.TeamMember) => sq.copy(teamComment = value.comment, teamStatus = value.status)
            case (Some(sq), ReviewMode.Reviewer) => sq.copy(reviewerComment = value.comment, reviewerStatus = value.status)
            case (None, ReviewMode.TeamMember) => Question(service, question, teamComment = value.comment, teamStatus = value.status)
            case (None, ReviewMode.Reviewer) => Question(service, question, reviewerComment = value.comment, reviewerStatus = value.status)
            case _ => throw new Exception("Viewers shouldn't be able to submit questions")
          }
          for {
            _ <- connector.insertQuestion(updatedQuestion)
          } yield Redirect(controllers.routes.SectionController.onPageLoad(service, section))
      )
  }

}
