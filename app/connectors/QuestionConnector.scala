/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import com.google.inject.{ImplementedBy, Inject, Singleton}
import config.Service
import models.{Question, QuestionResponse}
import play.api.Configuration
import play.api.http.Status.*
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Result
import play.api.mvc.Results.Created
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace


@Singleton
class QuestionConnectorImpl @Inject() (
  httpClient: HttpClientV2,
  configuration: Configuration,
)(implicit ec: ExecutionContext)
    extends QuestionConnector
    with ConnectorErrorHandler {

  private val service: Service = configuration.get[Service]("microservice.services.platform-readiness-spike")
  private val baseUrl          = s"${service.baseUrl}/platform-readiness-spike"

  def getCurrentQuestions(service: String)(implicit hc: HeaderCarrier): Future[QuestionResponse] = {
    httpClient
      .get(url"$baseUrl/current-questions/$service")
      .execute
      .flatMap { response =>
        if (response.status == OK) {
          response.json.validate[QuestionResponse] match {
            case JsSuccess(questions, _) => Future.successful(questions)
            case JsError(_)               =>
              Future.failed(QuestionConnector.UnexpectedResponseException(response.status, response.body))
          }
        } else {
          handleError(QuestionConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def insertQuestion(question: Question)(implicit hc: HeaderCarrier): Future[Result] =
    httpClient
      .post(url"$baseUrl/question/")
      .withBody(Json.toJson(question))
      .execute
      .flatMap { response =>
        if (response.status == CREATED) {
          Future.successful(Created)
        } else {
          handleError(QuestionConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
}

@ImplementedBy(classOf[QuestionConnectorImpl])
trait QuestionConnector {
  def getCurrentQuestions(service: String)(implicit hc: HeaderCarrier): Future[QuestionResponse]
  def insertQuestion(question: Question)(implicit hc: HeaderCarrier): Future[Result]
}

object QuestionConnector {
  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from Platform Readiness, status: $status, body: $body"
  }
}
