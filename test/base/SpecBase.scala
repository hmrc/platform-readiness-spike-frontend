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

package base

import controllers.actions.*
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import connectors.TeamsAndRepositoriesConnector
import auth.FrontendAuthStubProvider
import uk.gov.hmrc.internalauth.client.FrontendAuthComponents
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq as eqTo
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import uk.gov.hmrc.internalauth.client.test.StubBehaviour

trait SpecBase
  extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with BeforeAndAfterEach {
  
  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  val mockStubBehaviour: StubBehaviour = mock[StubBehaviour]

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[StubBehaviour].toInstance(mockStubBehaviour),
        bind[FrontendAuthComponents].toProvider[FrontendAuthStubProvider],
        bind[TeamsAndRepositoriesConnector].to[FakeTeamsAndRepositoriesConnector]
      )

  override protected def beforeEach(): Unit = {
    Mockito.reset[Any](
      mockStubBehaviour
    )
    super.beforeEach()
  }
}
