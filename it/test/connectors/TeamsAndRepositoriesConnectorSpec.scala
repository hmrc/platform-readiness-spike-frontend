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

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.Fault
import models.repositories.{GitRepository, Organisation, RepoType, ServiceType}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.HeaderCarrier
import util.WireMockHelper
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.Instant

class TeamsAndRepositoriesConnectorSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience with WireMockHelper {

  private lazy val app: Application = {
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.teams-and-repositories.port" -> server.port(),
        "microservice.services.teams-and-repositories.host" -> "localhost",
        "microservice.services.teams-and-repositories.protocol" -> "http",
        "create-internal-auth-token-on-start" -> "false"
      )
      .build()

  }

  private val testRepo = GitRepository(
    name = "some-service",
    organisation = Some(Organisation.Mdtp),
    description = "abc",
    url = "https://github.com/hmrc/catalogue-frontend",
    createdDate = Instant.now,
    lastActiveDate = Instant.now,
    endOfLifeDate = Some(Instant.now),
    isPrivate = false,
    repoType = RepoType.Service,
    serviceType = Some(ServiceType.Frontend),
    tags = None,
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

  private lazy val connector = app.injector.instanceOf[TeamsAndRepositoriesConnector]

  "allRepositories" - {

    given Writes[GitRepository] = GitRepository.writes
    given HeaderCarrier = HeaderCarrier()
    val url = "/api/v2/repositories\\?organisation=mdtp&repoType=service&archived=false"

    "must return a successful future when the service responds with OK and a seq of repositories" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.toJson(Seq(testRepo)).toString)
          )
      )

      connector.allRepositories.futureValue mustEqual Seq(testRepo)
    }

    "must return a failed future when the service responds with OK but a different object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("""{ "element" : "value" }""").withStatus(OK))
      )

      val exception = connector.allRepositories.failed.futureValue
      exception mustEqual TeamsAndRepositoriesConnector.UnexpectedResponseException(200, """{ "element" : "value" }""")
    }

    "must return a failed future when the service responds with anything else" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.allRepositories.failed.futureValue
      exception mustEqual TeamsAndRepositoriesConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.allRepositories.failed.futureValue
    }

  }


}