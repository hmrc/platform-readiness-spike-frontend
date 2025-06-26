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
import models.UserAnswers
import play.api.Configuration
import play.api.http.Status.*
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Result
import play.api.mvc.Results.NoContent
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace


@Singleton
class SessionConnectorImpl @Inject() (
  httpClient: HttpClientV2,
  configuration: Configuration,
)(implicit ec: ExecutionContext)
    extends SessionConnector
    with ConnectorErrorHandler {

  private val service: Service = configuration.get[Service]("microservice.services.platform-readiness-spike")
  private val baseUrl          = s"${service.baseUrl}/platform-readiness-spike"

  def getUserAnswers(userId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    httpClient
      .get(url"$baseUrl/user-answers/$userId")
      .execute
      .flatMap { response =>
        if (response.status == OK) {
          response.json.validate[UserAnswers] match {
            case JsSuccess(userAnswers, _) => Future.successful(Some(userAnswers))
            case JsError(_)               =>
              Future.failed(SessionConnector.UnexpectedResponseException(response.status, response.body))
          }
        } else if (response.status == NOT_FOUND) {
          Future.successful(None)
        } else {
          handleError(SessionConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
  }

  def setUserAnswers(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result] =
    httpClient
      .put(url"$baseUrl/user-answers/")
      .withBody(Json.toJson(userAnswers))
      .execute
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(NoContent)
        } else {
          handleError(SessionConnector.UnexpectedResponseException(response.status, response.body))
        }
      }
}

@ImplementedBy(classOf[SessionConnectorImpl])
trait SessionConnector {
  def getUserAnswers(userId: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]
  def setUserAnswers(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Result]
}

object SessionConnector {
  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from Platform Readiness Spike, status: $status, body: $body"
  }
}
