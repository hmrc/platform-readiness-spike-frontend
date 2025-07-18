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

package controllers.buildResilience

import com.google.inject.Inject
import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import pages.buildResilience.DoesNonstandardPatternPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.buildResilience.*
import viewmodels.govuk.summarylist.*
import views.html.buildResilience.CheckYourAnswersView

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val nonStandardPatternSummaryRow  =
        if request.userAnswers.get(DoesNonstandardPatternPage).getOrElse(false) then NonstandardPatternSummary.row(request.userAnswers)
        else None

      val list = SummaryListViewModel(
        rows = Seq(ServiceURLSummary.row(request.userAnswers),
          DoesNonstandardPatternSummary.row(request.userAnswers),
          nonStandardPatternSummaryRow,
          BreakBobbyRulesSummary.row(request.userAnswers),
          DeprecatedLibrariesSummary.row(request.userAnswers),
          UsingHTTPVerbsSummary.row(request.userAnswers),
          ReadMeFitForPurposeSummary.row(request.userAnswers),
          AppropriateTimeoutsSummary.row(request.userAnswers)
        ).flatten
      )

      Ok(view(list))

  }
}
