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

package services

import connectors.SessionConnector
import models.UserAnswers
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Results.NoContent
import uk.gov.hmrc.http.HeaderCarrier

import java.time.{LocalDateTime, ZoneOffset}
import scala.concurrent.Future


class SessionServiceSpec
  extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar
  with BeforeAndAfterEach{

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mockSessionConnector)
  }

  val hc = HeaderCarrier()
  val mockSessionConnector = mock[SessionConnector]
  val sessionService = new SessionServiceImpl(mockSessionConnector)
  val date = LocalDateTime.of(2022, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
  val testUserAnswers = UserAnswers("123", lastUpdated = date)

  "getUserAnswers" should {
    "pass the correct userId to connector" in {

      when(mockSessionConnector.getUserAnswers("123")(hc)) thenReturn Future.successful(Some(testUserAnswers))
      sessionService.getUserAnswers("123")(hc).futureValue mustEqual Some(testUserAnswers)
      
    }
  }
  "setUserAnswers" should {
    "pass the correct UserAnswers to connector" in {

      when(mockSessionConnector.setUserAnswers(testUserAnswers)(hc)) thenReturn Future.successful(NoContent)
      sessionService.setUserAnswers(testUserAnswers)(hc).futureValue mustEqual NoContent

    }
  }
}