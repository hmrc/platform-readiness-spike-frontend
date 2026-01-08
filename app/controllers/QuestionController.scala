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
import forms.QuestionFormProvider
import models.Question

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{ErrorTemplate, QuestionView}

import scala.concurrent.{ExecutionContext, Future}

class QuestionController @Inject()(
                                   override val messagesApi: MessagesApi,
                                   identify: IdentifierAction,
                                   val controllerComponents: MessagesControllerComponents,
                                   view: QuestionView,
                                   errorView: ErrorTemplate,
                                   formProvider: QuestionFormProvider,
                                   connector: QuestionConnector,
                                   repositoryActionFactory: RepositoryActionFactory
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(service: String, section: String, question: String): Action[AnyContent] = (identify andThen repositoryActionFactory.action(service)) {
    implicit request =>

      val form = formProvider(service, question, "User", "User")

      QuestionStructure.sectionsMap(section).find(_.name == question) match {
        case Some(q) => {
          val savedQuestion: Option[Question] = request.assessedService.answeredQuestions.get(question)
          val preparedForm = savedQuestion match {
            case None => form
            case Some(value) => form.fill(value)
          }
          Ok(view(service, section, question, preparedForm))
        }
        case None => NotFound(errorView("questionNotFound.title", "questionNotFound.heading", "questionNotFound.message"))
      }
  }

  def onSubmit(service: String, section: String, question: String): Action[AnyContent] = (identify).async {
    implicit request =>

      val form = formProvider(service, question, "User", "User")

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(service, section, question, formWithErrors))),

        value =>
          for {
            _ <- connector.insertQuestion(value)
          } yield Redirect(controllers.routes.SectionController.onPageLoad(service, section))
      )
  }

}
