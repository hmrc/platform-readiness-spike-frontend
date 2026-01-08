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

package controllers.test

import connectors.{TeamsAndRepositoriesConnector, TestTeamsAndRepositoriesConnector}
import models.repositories.{GitRepository, Tag}
import models.requests.AssessedServiceRequest
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import uk.gov.hmrc.internalauth.client.{FrontendAuthComponents, Retrieval}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView
import views.html.components.serviceLink

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TestDataController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 auth: FrontendAuthComponents,
                                 teamsAndRepositoriesConnector: TeamsAndRepositoriesConnector,
                                 testConnector: TestTeamsAndRepositoriesConnector,
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] =
    auth.authenticatedAction(
      continueUrl = controllers.test.routes.TestDataController.onPageLoad(),
      retrieval = Retrieval.username
    )().async { implicit request =>
      for {
        _ <- testConnector.addRepositories
      } yield Redirect(controllers.routes.IndexController.onPageLoad())
  }

}
