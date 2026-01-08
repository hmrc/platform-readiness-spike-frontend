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

import models.repositories.ServiceType.{Backend, Frontend}
import models.repositories.{GitRepository, Organisation, RepoType, ServiceType, Tag}
import play.api.libs.json.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, StringContextOps}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue

import java.time.Instant
import com.google.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestTeamsAndRepositoriesConnector @Inject()(
                                         httpClientV2  : HttpClientV2,
                                         servicesConfig: ServicesConfig
                                       )(using ExecutionContext) {

  import HttpReads.Implicits._

  private val teamsAndServicesBaseUrl: String =
    servicesConfig.baseUrl("teams-and-repositories")

  private given Writes[GitRepository] = GitRepository.writes

  def addRepositories(using HeaderCarrier): Future[Unit] = {
    val repositories: List[GitRepository] = List(
      testRepo("pertax-frontend", Frontend, None),
      testRepo("pertax", Backend, None),
      testRepo("digital-disclosure-service", Backend, None),
      testRepo("dms-submission-admin-frontend", Frontend, Some(Set(Tag.AdminFrontend)))
    )

    httpClientV2
      .post(url"$teamsAndServicesBaseUrl/test-only/repos")
      .withBody(Json.toJson(repositories))
      .execute
  }

  private def testRepo(name: String, serviceType: ServiceType, tags: Option[Set[Tag]]) = GitRepository(
    name = name,
    organisation = Some(Organisation.Mdtp),
    description = "abc",
    url = "https://github.com/hmrc/catalogue-frontend",
    createdDate = Instant.now,
    lastActiveDate = Instant.now,
    endOfLifeDate = Some(Instant.now),
    isPrivate = false,
    repoType = RepoType.Service,
    serviceType = Some(serviceType),
    tags = tags,
    digitalServiceName = None,
    owningTeams  = Seq.empty,
    language = None,
    isArchived = false,
    defaultBranch = "main",
    isDeprecated = false,
    teamNames = Seq.empty,
    prototypeName = None,
    prototypeAutoPublish = None
  )

}