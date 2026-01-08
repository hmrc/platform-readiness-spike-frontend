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

package controllers.actions

import connectors.{QuestionConnector, TeamsAndRepositoriesConnector}
import controllers.routes
import models.Question
import models.repositories.{AssessedService, Tag}
import models.requests.{AssessedServiceRequest, IdentifierRequest}
import play.api.mvc.*
import play.api.mvc.Results.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import com.google.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import views.html.ErrorTemplate
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, Retrieval}
import uk.gov.hmrc.internalauth.client.AuthenticatedRequest

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@Singleton
class RepositoryActionFactory @Inject()(
                                         val repositoryConnector: TeamsAndRepositoriesConnector,
                                         val questionConnector: QuestionConnector,
                                         val parser: BodyParsers.Default,
                                         val errorView: ErrorTemplate,
                                         val messagesApi: MessagesApi
                                       )
                                       (implicit val executionContext: ExecutionContext) {
  def action(service: String) =
    new RepositoryActionBuilder(service, repositoryConnector, questionConnector, parser, errorView, messagesApi)
}

class RepositoryActionBuilder(
                        val service: String,
                        val repositoryConnector: TeamsAndRepositoriesConnector,
                        val questionConnector: QuestionConnector,
                        val parser: BodyParsers.Default,
                        val errorView: ErrorTemplate,
                        val messagesApi: MessagesApi
                      )(implicit val executionContext: ExecutionContext)
  extends ActionRefiner[RepositoryActionBuilder.AuthenticatedRequestWithUsername, AssessedServiceRequest] with I18nSupport {

  override def refine[A](
                          request: RepositoryActionBuilder.AuthenticatedRequestWithUsername[A]
                        ): Future[Either[Result, AssessedServiceRequest[A]]] = {
    given HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    given Request[A] = request

    repositoryConnector.getRepository(service).flatMap {
      case None => Future.successful(Left(NotFound(errorView("serviceNotFound.title", "serviceNotFound.heading", "serviceNotFound.message"))))
      case Some(repo) =>
        questionConnector.getCurrentQuestions(service).map { questionResponse =>
          val questionMap: Map[String, Question] = questionResponse.questions.map(q => q.questionId -> q).toMap
          val assessedService = AssessedService(
            service,
            repo.url,
            repo.serviceType,
            repo.tags.getOrElse(Set()).contains(Tag.AdminFrontend),
            repo.tags.getOrElse(Set()).contains(Tag.Api),
            questionMap
          )
          Right(AssessedServiceRequest(request, request.retrieval.value, assessedService))
        }
    }
  }

}

private object RepositoryActionBuilder {
  private type AuthenticatedRequestWithUsername[A] =
    AuthenticatedRequest[A, Retrieval.Username]
}