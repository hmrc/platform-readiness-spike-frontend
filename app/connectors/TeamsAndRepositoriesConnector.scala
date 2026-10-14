/*
 * Copyright 2026 HM Revenue & Customs
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

import models.repositories.GitRepository
import play.api.libs.json.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.http.Status.OK

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

@Singleton
class TeamsAndRepositoriesConnectorImpl @Inject()(
                                               httpClientV2  : HttpClientV2,
                                               servicesConfig: ServicesConfig
                                             )(using ExecutionContext)
    extends TeamsAndRepositoriesConnector
    with ConnectorErrorHandler {

  import HttpReads.Implicits._

  private val teamsAndServicesBaseUrl: String =
    servicesConfig.baseUrl("teams-and-repositories")

  private given Reads[GitRepository] = GitRepository.reads

  def allRepositories(using HeaderCarrier): Future[Seq[GitRepository]] =
    httpClientV2
      .get(url"$teamsAndServicesBaseUrl/api/v2/repositories?organisation=mdtp&repoType=service&archived=false")
      .execute[HttpResponse]
      .flatMap { response =>
        if (response.status == OK) {
          response.json.validate[Seq[GitRepository]] match {
            case JsSuccess(repositories, _) => Future.successful(repositories.sortBy(_.name))
            case JsError(_) =>
              Future.failed(TeamsAndRepositoriesConnector.UnexpectedResponseException(response.status, response.body))
          }
        } else {
          handleError(TeamsAndRepositoriesConnector.UnexpectedResponseException(response.status, response.body))
        }
      }

  def getRepository(name: String)(using HeaderCarrier): Future[Option[GitRepository]] =
    httpClientV2
      .get(url"$teamsAndServicesBaseUrl/api/v2/repositories?name=$name&organisation=mdtp&repoType=service&archived=false")
      .execute[HttpResponse]
      .flatMap { response =>
        if (response.status == OK) {
          response.json.validate[Seq[GitRepository]] match {
            case JsSuccess(repositories, _) => Future.successful(repositories.find(_.name == name))
            case JsError(_) =>
              Future.failed(TeamsAndRepositoriesConnector.UnexpectedResponseException(response.status, response.body))
          }
        } else {
          handleError(TeamsAndRepositoriesConnector.UnexpectedResponseException(response.status, response.body))
        }
      }

}

@ImplementedBy(classOf[TeamsAndRepositoriesConnectorImpl])
trait TeamsAndRepositoriesConnector {
  def allRepositories(using HeaderCarrier): Future[Seq[GitRepository]]
  def getRepository(name: String)(using HeaderCarrier): Future[Option[GitRepository]]
}

object TeamsAndRepositoriesConnector {
  final case class UnexpectedResponseException(status: Int, body: String) extends Exception with NoStackTrace {
    override def getMessage: String = s"Unexpected response from Teams and Repositories, status: $status, body: $body"
  }
}