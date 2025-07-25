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

import base.SpecBase
import models.UserAnswers
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import services.SessionService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockitoSugar {

  class Harness(sessionService: SessionService) extends DataRetrievalActionImpl(sessionService) {

    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()
  "Data Retrieval Action" - {

    "when there is no data returned from downstream" - {

      "must set userAnswers to 'None' in the request" in {

        val sessionService = mock[SessionService]
        when(sessionService.getUserAnswers(refEq("id"))(any())) thenReturn Future(None)
        val action = new Harness(sessionService)

        val result = action.callTransform(IdentifierRequest(FakeRequest(), "id")).futureValue

        result.userAnswers must not be defined
      }
    }

    "when there is data returned from downstream" - {

      "must build a userAnswers object and add it to the request" in {

        val sessionService = mock[SessionService]
        when(sessionService.getUserAnswers(refEq("id"))(any())) thenReturn Future(Some(UserAnswers("id")))
        val action = new Harness(sessionService)

        val result = action.callTransform(new IdentifierRequest(FakeRequest(), "id")).futureValue

        result.userAnswers mustBe defined
      }
    }
  }
}
