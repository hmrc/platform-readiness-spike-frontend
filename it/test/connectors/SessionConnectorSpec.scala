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
import models.UserAnswers
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Results.NoContent
import uk.gov.hmrc.http.HeaderCarrier
import util.WireMockHelper

import java.time.{LocalDateTime, ZoneOffset}

class SessionConnectorSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience with WireMockHelper {

  private lazy val app: Application =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.platform-readiness-spike.port" -> server.port(),
        "microservice.services.platform-readiness-spike.host" -> "localhost",
        "microservice.services.platform-readiness-spike.protocol" -> "http",
        "create-internal-auth-token-on-start" -> "false"
      )
      .build()

  private lazy val connector = app.injector.instanceOf[SessionConnector]

  "getUserAnswers" - {

    val hc = HeaderCarrier()
    val url = "/platform-readiness-spike/user-answers/123"
    val date = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
    val testUserAnswers = UserAnswers("123", lastUpdated = date)


    "must return a successful future when the service responds with OK and a UserAnswers object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.toJson(testUserAnswers).toString)
          )
      )

      connector.getUserAnswers("123")(hc).futureValue mustEqual Some(testUserAnswers)
    }

    "must return a successful future when the service responds with NOT_FOUND" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.getUserAnswers("123")(hc).futureValue mustEqual None
    }

    "must return a failed future when the service responds with OK but a different object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("""{ "element" : "value" }""").withStatus(OK))
      )

      val exception = connector.getUserAnswers("123")(hc).failed.futureValue
      exception mustEqual SessionConnector.UnexpectedResponseException(200, """{ "element" : "value" }""")
    }

    "must return a failed future when the service responds with anything else" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.getUserAnswers("123")(hc).failed.futureValue
      exception mustEqual SessionConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getUserAnswers("123")(hc).failed.futureValue
    }

  }


  "setUserAnswers" - {

    val hc = HeaderCarrier()
    val url = "/platform-readiness-spike/user-answers/"

    val date = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
    val testUserAnswers = UserAnswers("123", lastUpdated = date)

    "must return a successful future when the service responds with NO_CONTENT" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testUserAnswers))))
          .willReturn(
            aResponse()
              .withStatus(NO_CONTENT)
          )
      )
      connector.setUserAnswers(testUserAnswers)(hc).futureValue mustEqual NoContent
    }


    "must return a failed future when the service responds with anything else" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testUserAnswers))))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.setUserAnswers(testUserAnswers)(hc).failed.futureValue
      exception mustEqual SessionConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(testUserAnswers))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.setUserAnswers(testUserAnswers)(hc).failed.futureValue
    }

  }

}