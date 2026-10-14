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
import models.{Question, QuestionResponse, ReviewStatus}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Results.Created
import uk.gov.hmrc.http.HeaderCarrier
import util.WireMockHelper

import java.time.{LocalDateTime, ZoneOffset}

class QuestionConnectorSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience with WireMockHelper {

  private lazy val app: Application = {
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.platform-readiness-spike.port" -> server.port(),
        "microservice.services.platform-readiness-spike.host" -> "localhost",
        "microservice.services.platform-readiness-spike.protocol" -> "http",
        "create-internal-auth-token-on-start" -> "false"
      )
      .build()

  }

  private val date = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)

  private val question = Question(
    service = "Service1",
    questionId = "Question1",
    lastUpdated = date,
    teamComment = Some("This looks really good"),
    teamStatus = ReviewStatus.Pass,
    teamMemberUsername = Some("user"),
    reviewerComment = Some("Not sure about this"),
    reviewerStatus = ReviewStatus.Fail,
    reviewerUsername = Some("Reviewer")
  )

  private val questionResponse = QuestionResponse(
    service = "123",
    questions = Seq(question)
  )

  private lazy val connector = app.injector.instanceOf[QuestionConnector]

  "getCurrentQuestions" - {

    val hc = HeaderCarrier()
    val url = "/platform-readiness-spike/current-questions/123"

    "must return a successful future when the service responds with OK and a Question object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.toJson(questionResponse).toString)
          )
      )

      connector.getCurrentQuestions("123")(hc).futureValue mustEqual questionResponse
    }

    "must return a failed future when the service responds with OK but a different object" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("""{ "element" : "value" }""").withStatus(OK))
      )

      val exception = connector.getCurrentQuestions("123")(hc).failed.futureValue
      exception mustEqual QuestionConnector.UnexpectedResponseException(200, """{ "element" : "value" }""")
    }

    "must return a failed future when the service responds with anything else" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.getCurrentQuestions("123")(hc).failed.futureValue
      exception mustEqual QuestionConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getCurrentQuestions("123")(hc).failed.futureValue
    }

  }


  "insertQuestion" - {

    val hc = HeaderCarrier()
    val url = "/platform-readiness-spike/question/"

    "must return a successful future when the service responds with CREATED" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(question))))
          .willReturn(
            aResponse()
              .withStatus(CREATED)
          )
      )
      connector.insertQuestion(question)(hc).futureValue mustEqual Created
    }


    "must return a failed future when the service responds with anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(question))))
          .willReturn(aResponse().withBody("body").withStatus(INTERNAL_SERVER_ERROR))
      )

      val exception = connector.insertQuestion(question)(hc).failed.futureValue
      exception mustEqual QuestionConnector.UnexpectedResponseException(500, "body")
    }

    "must return a failed future when there is a connection error" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(question))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.insertQuestion(question)(hc).failed.futureValue
    }

  }

}