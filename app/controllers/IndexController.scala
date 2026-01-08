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

package controllers

import connectors.{TeamsAndRepositoriesConnector, TestTeamsAndRepositoriesConnector}
import controllers.actions.IdentifierAction
import models.repositories.{GitRepository, Tag}
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import views.html.components.serviceLink

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 identify: IdentifierAction,
                                 view: IndexView,
                                 teamsAndRepositoriesConnector: TeamsAndRepositoriesConnector,
                                 testConnector: TestTeamsAndRepositoriesConnector,
                                 serviceLink: serviceLink
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = (identify).async { implicit request =>
    for {
      _ <- testConnector.addRepositories
      repos <- teamsAndRepositoriesConnector.allRepositories
      table = IndexController.tableFromRepos(repos)
    } yield Ok(view(table))
  }

}

object IndexController {

  def tableFromRepos(repos: Seq[GitRepository])(implicit messages: Messages): Table = {
    createTable(repos.map(r => storeEntryToTableRow(r)))
  }

  private def createTable(rows: Seq[Seq[TableRow]])(implicit messages: Messages): Table =
    Table(
      rows = rows,
      head = Some(
        Seq(
          HeadCell(Text(messages("Service"))),
          HeadCell(Text(messages("Service type"))),
          HeadCell(Text(messages("Admin service?")))
        )
      ),
      firstCellIsHeader = true,
      attributes = Map("id" -> "service-table"),
      caption = Some(messages("index.heading")),
      captionClasses = "govuk-heading-xl"
    )

  private def storeEntryToTableRow(repo: GitRepository)(implicit messages: Messages): Seq[TableRow] = {
    val html = serviceLink()(repo.name)
    Seq(
      TableRow(HtmlContent(html)),
      TableRow(Text(repo.serviceType.map(_.toString).getOrElse("N/A"))),
      TableRow(Text(repo.isAdminService.toString))
    )
  }
}

